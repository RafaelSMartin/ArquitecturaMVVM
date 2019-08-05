package com.rsmartin.arquitecturamvvm.repository;

import androidx.lifecycle.LiveData;

import com.rsmartin.arquitecturamvvm.AppExecutors;
import com.rsmartin.arquitecturamvvm.Utils.RateLimiter;
import com.rsmartin.arquitecturamvvm.api.ApiResponse;
import com.rsmartin.arquitecturamvvm.api.WebServiceApi;
import com.rsmartin.arquitecturamvvm.db.GitHubDb;
import com.rsmartin.arquitecturamvvm.db.RepoDao;
import com.rsmartin.arquitecturamvvm.model.Repo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Se encargara de acceder a nuestro Webservice y a RepoDao
 */

@Singleton
public class RepoRepository {

    private final GitHubDb db;
    private final RepoDao repoDao;
    private final WebServiceApi githubService;
    private final AppExecutors appExecutors;

    private RateLimiter<String> repoListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public RepoRepository(AppExecutors appExecutors, GitHubDb db, RepoDao repoDao, WebServiceApi githubService) {
        this.appExecutors = appExecutors;
        this.db = db;
        this.repoDao = repoDao;
        this.githubService = githubService;
    }

    public LiveData<Resource<List<Repo>>> loadRepos(String owner){
        return new NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors){

            @Override
            protected boolean shouldFetchData(List<Repo> data) {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner);
            }

            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                return repoDao.loadRepositories(owner);
            }

            @Override
            protected void saveCallResult(List<Repo> item) {
                repoDao.insertRepos(item);
            }

            @Override
            protected LiveData<ApiResponse<List<Repo>>> createCall() {
                return githubService.getRepos(owner);
            }

            @Override
            protected void onFetchFailed(){
                repoListRateLimit.reset(owner);
            }

        }.asLiveData();
    }
}
