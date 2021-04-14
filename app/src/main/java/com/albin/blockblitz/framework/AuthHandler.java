package com.albin.blockblitz.framework;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.albin.blockblitz.enums.ResponseCode;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class AuthHandler {
    private static final String TAG = AuthHandler.class.getSimpleName();
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    public static MutableLiveData<ResponseCode> createUserWithEmailAndPassword(String email, String password) {
        MutableLiveData<ResponseCode> status = new MutableLiveData<>();
        status.setValue(ResponseCode.WAITING);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        status.setValue(ResponseCode.SUCCESS);
                    }
                    else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        status.setValue(ResponseCode.FirebaseAuthUserCollisionException);
                    }
                    else {
                        Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                        status.setValue(ResponseCode.FAILURE);
                    }
                });

        return status;
    }

    public static MutableLiveData<ResponseCode> signInUserWithEmailAndPassword(String email, String password) {
        MutableLiveData<ResponseCode> status = new MutableLiveData<>();
        status.setValue(ResponseCode.WAITING);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        status.setValue(ResponseCode.SUCCESS);
                    }
                    else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        status.setValue(ResponseCode.FirebaseAuthInvalidUserException);
                    }
                    else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        status.setValue(ResponseCode.FirebaseAuthInvalidCredentialsException);
                    }
                    else {
                        Log.w(TAG, "signInUserWithEmailAndPassword:failure", task.getException());
                        status.setValue(ResponseCode.FAILURE);
                    }
                });

        return status;
    }

    public static MutableLiveData<ResponseCode> SignInWithCredential(AuthCredential credential) {
        MutableLiveData<ResponseCode> status = new MutableLiveData<>();
        status.setValue(ResponseCode.WAITING);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        status.setValue(ResponseCode.SUCCESS);
                    }
                    else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        status.setValue(ResponseCode.FAILURE);
                    }
                });

        return status;
    }
}