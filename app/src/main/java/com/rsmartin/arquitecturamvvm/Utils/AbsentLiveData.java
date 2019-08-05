package com.rsmartin.arquitecturamvvm.Utils;

import androidx.lifecycle.LiveData;

/**
 * LiveData que nos devuelve siempre null
 */

public class AbsentLiveData extends LiveData {

    public AbsentLiveData() {
        postValue(null);
    }

    public static  <T> LiveData<T> create(){
        return new AbsentLiveData();
    }
}
