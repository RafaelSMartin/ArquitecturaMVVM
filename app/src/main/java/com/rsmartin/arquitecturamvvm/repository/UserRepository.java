package com.rsmartin.arquitecturamvvm.repository;

import androidx.lifecycle.LiveData;

import com.rsmartin.arquitecturamvvm.AppExecutors;
import com.rsmartin.arquitecturamvvm.api.ApiResponse;
import com.rsmartin.arquitecturamvvm.api.WebServiceApi;
import com.rsmartin.arquitecturamvvm.db.UserDao;
import com.rsmartin.arquitecturamvvm.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {

    private final UserDao userDao;
    private final WebServiceApi gitHubService;
    private final AppExecutors appExecutors;

    @Inject
    UserRepository(UserDao userDao, WebServiceApi gitHubService, AppExecutors appExecutors) {
        this.userDao = userDao;
        this.gitHubService = gitHubService;
        this.appExecutors = appExecutors;
    }

    public LiveData<Resource<User>> loadUser(String login){
        return new NetworkBoundResource<User, User>(appExecutors){

            @Override
            protected boolean shouldFetchData(User data) {
                return data == null; // Si no hay datos en Room es true y lanzamos el Servicio
            }

            @Override
            protected LiveData<User> loadFromDb() {
                return userDao.findByLogin(login); // Se implementa aqui, cargamos un usario
            }

            @Override
            protected void saveCallResult(User item) {
                userDao.insert(item); // Guardamos un Usuario en Room, que esta bajado del Servicio
            }

            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                return gitHubService.getUser(login); // Es la peticion de la api
            }
        }.asLiveData(); //Devuevelo como LiveData para poder observarlo
    }

}
