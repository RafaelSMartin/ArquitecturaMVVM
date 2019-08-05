package com.rsmartin.arquitecturamvvm.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rsmartin.arquitecturamvvm.binding.FragmentBindingAdapters;

public class AutoClearValue<T> {

    private T value;
    public AutoClearValue(Fragment fragment, T value){
        FragmentManager fragmentManager = fragment.getFragmentManager();

        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if(f == fragment){
                    AutoClearValue.this.value = null;
                    fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                }
            }
        }, false);
        this.value = value;
    }

    public T get(){
        return value;
    }
}
