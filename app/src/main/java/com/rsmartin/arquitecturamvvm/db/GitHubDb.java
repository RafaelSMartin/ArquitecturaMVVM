package com.rsmartin.arquitecturamvvm.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rsmartin.arquitecturamvvm.model.Contributor;
import com.rsmartin.arquitecturamvvm.model.Repo;
import com.rsmartin.arquitecturamvvm.model.RepoSearchResult;
import com.rsmartin.arquitecturamvvm.model.User;

@Database(entities = {User.class, Repo.class, Contributor.class, RepoSearchResult.class}, version = 1)
public abstract class GitHubDb extends RoomDatabase {

    abstract public UserDao userDao();

    abstract public RepoDao repoDao();

}
