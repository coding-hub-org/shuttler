package com.psucoders.shuttler.data.firebase;

public class FirebaseSingleton {

    // static variable single_instance of type Singleton
    private static FirebaseSingleton instance = null;

    private FirebaseSingleton() {
        // Exists only to defeat instantiation.
        // Constructor here
    }

    public static FirebaseSingleton getInstance() {
        if (instance == null) {
            instance = new FirebaseSingleton();
        }
        return instance;
    }
}
