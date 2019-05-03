package com.psucoders.shuttler.data.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseSingleton {

    // static variable single_instance of type Singleton
    private static FirebaseSingleton instance = null;
    private static FirebaseAuth mAuth = null;

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

    public void logOut() {
        if(getAuthInstance().getCurrentUser() != null)
            Log.d("Current user", " is " + getAuthInstance().getCurrentUser().getEmail());

        getAuthInstance().signOut();

        Log.d("After logout", "Life2");

        if(getAuthInstance().getCurrentUser() != null)
            Log.d("User now", "Life3" + getAuthInstance().getCurrentUser().getEmail());
        else{
            Log.d("No", "current User");
        }
        //        System.out.println("USER TEST ===========================================" + FirebaseAuth.getInstance().getCurrentUser());

    }
}
