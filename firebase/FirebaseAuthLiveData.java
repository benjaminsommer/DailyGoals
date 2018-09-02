package com.benjaminsommer.dailygoals.firebase;

import android.arch.lifecycle.LiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthLiveData extends LiveData<Boolean> {

    private static FirebaseAuthLiveData sInstance;
    private FirebaseAuth mAuth;

    @MainThread
    public static FirebaseAuthLiveData get() {
        if (sInstance == null) {
            sInstance = new FirebaseAuthLiveData();
        }
        return sInstance;
    }

    private FirebaseAuthLiveData() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    private FirebaseAuth.AuthStateListener mListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            setValue(firebaseAuth.getCurrentUser() != null);
        }
    };

    @Override
    protected void onActive() {
        super.onActive();
        setValue(mAuth.getCurrentUser() != null);
        mAuth.addAuthStateListener(mListener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        mAuth.removeAuthStateListener(mListener);
    }
}
