package com.psucoders.shuttler.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseSingleton {

    // static variable single_instance of type Singleton
    private static FirebaseSingleton instance = null;
    private static FirebaseAuth mAuth = null;
    private final String logTag = "Firebase Singleton";

    private MutableLiveData<Boolean> _loginSuccess;

    public MutableLiveData<Boolean> loginSuccess() {
        if (_loginSuccess == null) {
            _loginSuccess = new MutableLiveData<>();
        }
        return _loginSuccess;
    }

    private FirebaseSingleton() {
        // Exists only to defeat instantiation.
        // Constructor here
    }

    public FirebaseAuth getAuthInstance() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

    public static FirebaseSingleton getInstance() {
        if (instance == null) {
            synchronized (FirebaseSingleton.class) {
                if (instance == null) {
                    instance = new FirebaseSingleton();
                }
            }
        }
        return instance;
    }

    public void register(String email, String password){
        getAuthInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            }
        });
    }

    public void login(String email, String password) {
        getAuthInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Here is life", "FUCK");
                if (task.isSuccessful()) {
                    Log.d(logTag, "Logged In Successfully");
                    _loginSuccess.setValue(true);
                } else {
                    Log.d(logTag, "Login failed");
                    _loginSuccess.setValue(false);
                }
            }
        });
    }

    public void logout() {
        if (getAuthInstance().getCurrentUser() != null)
            getAuthInstance().signOut();
    }
}
