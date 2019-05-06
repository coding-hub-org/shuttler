package com.psucoders.shuttler.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.psucoders.shuttler.MyFirebaseMessagingService;
import com.psucoders.shuttler.data.model.NotificationsModel;
import com.psucoders.shuttler.data.model.UserModel;

import java.util.HashMap;

public class FirebaseSingleton {

    // static variable single_instance of type Singleton
    private static FirebaseSingleton instance = null;
    private static FirebaseAuth mAuth = null;
    private final String logTag = "Firebase Singleton";

    //Firebase Database
    private FirebaseDatabase db;
    private DatabaseReference users;

    private MutableLiveData<Boolean> _loginSuccess;
    private MutableLiveData<Boolean> _registrationSuccess;
    private MutableLiveData<Boolean> _addedToDatabase;
    private MutableLiveData<Boolean> _logout;

    private MutableLiveData<String> _fcmToken;

    public MutableLiveData<String> getFcmToken() {
        if (_fcmToken == null) {
            _fcmToken = new MutableLiveData<>();
        }
        return _fcmToken;
    }

    public MutableLiveData<Boolean> loginSuccess() {
        if (_loginSuccess == null) {
            _loginSuccess = new MutableLiveData<>();
        }
        return _loginSuccess;
    }

    public MutableLiveData<Boolean> getRegistrationSuccess() {
        if (_registrationSuccess == null) {
            _registrationSuccess = new MutableLiveData<>();
        }
        return _registrationSuccess;
    }

    public MutableLiveData<Boolean> getAddedToDatabase() {
        if (_addedToDatabase == null) {
            _addedToDatabase = new MutableLiveData<>();
        }
        return _addedToDatabase;
    }

    public MutableLiveData<Boolean> getLogoutStatus() {
        if (_logout == null) {
            _logout = new MutableLiveData<>();
        }
        return _logout;
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

    public void register(final String email, final String password, final String username) {
        getAuthInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    getNotificationToken(email, password, username);
                } else {
                    _registrationSuccess.setValue(false);
                }
            }
        });
    }

    private void getNotificationToken(final String email, final String password, final String username) {
        final FirebaseUser currUser = getAuthInstance().getCurrentUser();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.d("Token", "Error!!");
                } else {

                    if (task.getResult() != null) {
                        String token = task.getResult().getToken();
                        Log.d("Token", "FCM: " + token);
                        _fcmToken.setValue(token);
                    }

                    HashMap<String, Boolean> notificationTokens = new HashMap<>();
                    notificationTokens.put(_fcmToken.getValue(), true);
                    NotificationsModel notificationsModel = new NotificationsModel(notificationTokens, "Walmart", "5");
                    UserModel newUser = new UserModel(email, password, username, notificationsModel);

                    db = FirebaseDatabase.getInstance();
                    users = db.getReference("Users");

                    if (currUser != null) {
                        users.child(currUser.getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(logTag, "Added new user to db");
                                } else {
                                    Log.d(logTag, "Add new user to db failed");
                                }
                            }
                        });
                    }

                    if (currUser != null) {
                        currUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                _registrationSuccess.setValue(true);
                            }
                        });
                    }

                }
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
        _logout.setValue(true);
    }

    public void updateSettings(NotificationsModel newNotificationModel) {

        FirebaseUser currUser = getAuthInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        if (currUser != null) {
            users.child(currUser.getUid()).child("notifications").setValue(newNotificationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(logTag, "Updated the notifications");
                    } else {
                        Log.d(logTag, "Failed");
                    }
                }
            });
        }

    }
}
