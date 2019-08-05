package com.rsmartin.arquitecturamvvm.ui.user;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.rsmartin.arquitecturamvvm.Utils.AbsentLiveData;
import com.rsmartin.arquitecturamvvm.model.Repo;
import com.rsmartin.arquitecturamvvm.model.User;
import com.rsmartin.arquitecturamvvm.repository.RepoRepository;
import com.rsmartin.arquitecturamvvm.repository.Resource;
import com.rsmartin.arquitecturamvvm.repository.UserRepository;

import java.util.List;
import java.util.Objects;

public class UserViewModel extends ViewModel {

    final MutableLiveData<String> login = new MutableLiveData<>();

    private final LiveData<Resource<List<Repo>>> repositories;
    private final LiveData<Resource<User>> user;

    public UserViewModel(UserRepository userRepository, RepoRepository repoRepository) {
        user = Transformations.switchMap(login, new Function<String, LiveData<Resource<User>>>() {
            @Override
            public LiveData<Resource<User>> apply(String login) {
                if(login == null){
                    return AbsentLiveData.create();
                } else {
                    return userRepository.loadUser(login);
                }
            }
        });

        repositories = Transformations.switchMap(login, login ->{
            if(login == null){
                return AbsentLiveData.create();
            } else {
                return repoRepository.loadRepos(login);
            }
        });
    }

    public void setLogin(String login){
        if(Objects.equals(this.login.getValue(), login)){ //Comparamos los logins, si son iguales nada
            return;
        }
        this.login.setValue(login); // si los login son distintos actualizo mi variable login
    }

    public LiveData<Resource<List<Repo>>> getRepositories(){
        return repositories;
    }

    public void retry(){
        if(this.login.getValue() != null){
            this.login.setValue(this.login.getValue()); //reintentamos
        }
    }
}
