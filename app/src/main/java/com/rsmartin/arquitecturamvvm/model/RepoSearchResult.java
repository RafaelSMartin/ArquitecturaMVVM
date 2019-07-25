package com.rsmartin.arquitecturamvvm.model;

import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.rsmartin.arquitecturamvvm.db.GitHubTypeConverters;

import java.util.List;

@Entity(primaryKeys = {"query"})
@TypeConverters(GitHubTypeConverters.class)
public class RepoSearchResult {

    public final String query;
    public final List<Integer> repoIds;
    public final int totalCount;
    public final Integer next;

    public RepoSearchResult(String query, List<Integer> repoIds, int totalCount, Integer next) {
        this.query = query;
        this.repoIds = repoIds;
        this.totalCount = totalCount;
        this.next = next;
    }
}
