package com.rsmartin.arquitecturamvvm.model;


import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;

/**
 * https://api.github.com/repos/jakewharton/ActionBarSherlock/contributors
 */

@Entity(primaryKeys = {"repoName", "repoOwner", "login"},
        foreignKeys = @ForeignKey(entity = Repo.class,
        parentColumns = {"name", "owner_login"},
        childColumns = {"repoName", "repoOwner"},
        onUpdate = ForeignKey.CASCADE))
public class Contributor {

    @SerializedName("login")
    public final String login;
    @SerializedName("contributions")
    public final String contributions;
    @SerializedName("avatar_url")
    public final String avatarUrl;

    public String repoName;
    public String repoOwner;

    public Contributor(String login, String contributions, String avatarUrl) {
        this.login = login;
        this.contributions = contributions;
        this.avatarUrl = avatarUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getContributions() {
        return contributions;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoOwner() {
        return repoOwner;
    }

    public void setRepoOwner(String repoOwner) {
        this.repoOwner = repoOwner;
    }
}
