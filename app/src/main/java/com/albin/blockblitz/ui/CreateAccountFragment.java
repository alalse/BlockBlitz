package com.albin.blockblitz.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.albin.blockblitz.R;
import com.albin.blockblitz.enums.ResponseCode;
import com.albin.blockblitz.framework.AuthHandler;
import com.albin.blockblitz.framework.FirestoreHandler;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Arrays;
import java.util.Objects;

public class CreateAccountFragment extends BaseFragment {
    private CallbackManager fbCallbackManager;
    private GoogleSignInClient googleClient;
    private final String TAG = getClass().getSimpleName();
    private final int facebookRequestCode = 64206;
    private final int googleRequestCode = 9001;
    private boolean validEmail = false;
    private boolean validPassword = false;

    public CreateAccountFragment() {}

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        //Add email input validation
        EditText emailInput = view.findViewById(R.id.emailInput);
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                validEmail = EmailValidator.getInstance().isValid(editable.toString());
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        //Add password validation
        EditText passwordInput = view.findViewById(R.id.passwordInput);
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                validPassword = editable.length() > 5;
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        ProgressBar loginIndicator = view.findViewById(R.id.login_indicator);

        //Add functionality to the signup button
        Button btn = view.findViewById(R.id.signup_btn);
        btn.setOnClickListener(_view -> {
            if (FirebaseRemoteConfig.getInstance().getBoolean("signup_enabled")) {
                if (validEmail && validPassword) {
                    loginIndicator.setVisibility(View.VISIBLE);
                    String email = emailInput.getText().toString().trim();
                    String password = passwordInput.getText().toString().trim();

                    AuthHandler.createUserWithEmailAndPassword(email, password).observe(this, responseCode -> {
                        if (responseCode != ResponseCode.WAITING) {
                            loginIndicator.setVisibility(View.GONE);
                        }

                        if (responseCode == ResponseCode.SUCCESS) {
                            Toast.makeText(getActivity(), R.string.create_account_success, Toast.LENGTH_SHORT).show();
                            onLoginSuccess();
                        }
                        else if (responseCode == ResponseCode.FirebaseAuthUserCollisionException) {
                            Toast.makeText(getActivity(), R.string.create_account_email_taken, Toast.LENGTH_SHORT).show();
                        }
                        else if (responseCode == ResponseCode.FAILURE) {
                            Toast.makeText(getActivity(), R.string.create_account_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (!validEmail) {
                    Toast.makeText(getActivity(), R.string.create_account_invalid_email, Toast.LENGTH_SHORT).show();
                }
                else if (!validPassword) {
                    Toast.makeText(getActivity(), R.string.create_account_invalid_password, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getActivity(), R.string.signup_disabled, Toast.LENGTH_SHORT).show();
            }
        });

        //Add functionality to the login button
        btn = view.findViewById(R.id.loginBtn);
        btn.setOnClickListener(_view -> {
            if (FirebaseRemoteConfig.getInstance().getBoolean("email_login_enabled")) {
                if (validEmail && validPassword) {
                    loginIndicator.setVisibility(View.VISIBLE);
                    String email = emailInput.getText().toString().trim();
                    String password = passwordInput.getText().toString().trim();

                    AuthHandler.signInUserWithEmailAndPassword(email, password).observe(this, responseCode -> {
                        if (responseCode != ResponseCode.WAITING) {
                            loginIndicator.setVisibility(View.GONE);
                        }

                        if (responseCode == ResponseCode.SUCCESS) {
                            emailInput.setText("");
                            passwordInput.setText("");
                            Toast.makeText(getActivity(), R.string.signin_success, Toast.LENGTH_SHORT).show();
                            onLoginSuccess();
                        }
                        else if (responseCode == ResponseCode.FirebaseAuthInvalidUserException) {
                            Toast.makeText(getActivity(), R.string.signin_failure_invalid_email, Toast.LENGTH_SHORT).show();
                        }
                        else if (responseCode == ResponseCode.FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getActivity(), R.string.signin_failure_invalid_password, Toast.LENGTH_SHORT).show();
                        }
                        else if (responseCode == ResponseCode.FAILURE) {
                            Toast.makeText(getActivity(), R.string.signin_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (!validEmail) {
                    Toast.makeText(getActivity(), R.string.input_email, Toast.LENGTH_SHORT).show();
                }
                else if (!validPassword) {
                    Toast.makeText(getActivity(), R.string.input_password, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getActivity(), R.string.email_disabled, Toast.LENGTH_SHORT).show();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);
        view.findViewById(R.id.googleLoginBtn).setOnClickListener(_view -> {
            if (FirebaseRemoteConfig.getInstance().getBoolean("google_login_enabled")) {
                loginIndicator.setVisibility(View.VISIBLE);
                Intent googleSignInIntent = googleClient.getSignInIntent();
                startActivityForResult(googleSignInIntent, googleRequestCode);
            }
            else {
                Toast.makeText(getActivity(), R.string.google_disabled, Toast.LENGTH_SHORT).show();
            }
        });

        //Third-party login with Facebook
        fbCallbackManager = CallbackManager.Factory.create();
        LoginButton fbBtn = view.findViewById(R.id.fbLoginBtn);
        fbBtn.setPermissions("email", "public_profile");
        fbBtn.setFragment(this);
        fbBtn.setOnClickListener(_view -> loginIndicator.setVisibility(View.VISIBLE));
        fbBtn.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:");
                doFacebookLogin(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                loginIndicator.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.signin_cancelled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                loginIndicator.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.facebook_signin_failure, Toast.LENGTH_SHORT).show();
            }
        });

        //Sign out of third-parties (if signed in) on auth-state change
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                googleClient.signOut();
                LoginManager.getInstance().logOut();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == facebookRequestCode) {
            if (FirebaseRemoteConfig.getInstance().getBoolean("facebook_login_enabled")) {
                fbCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
            else {
                Toast.makeText(getActivity(), R.string.facebook_disabled, Toast.LENGTH_SHORT).show();
                getActivity().findViewById(R.id.login_indicator).setVisibility(View.GONE);
            }
        }
        else if (requestCode == googleRequestCode) {
            doGoogleLogin(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doGoogleLogin(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            AuthHandler.SignInWithCredential(credential).observe(this, responseCode -> {
                if (responseCode == ResponseCode.SUCCESS) {
                    Toast.makeText(getActivity(), R.string.signin_success, Toast.LENGTH_SHORT).show();
                    onLoginSuccess();
                }
                else if (responseCode == ResponseCode.FAILURE) {
                    Toast.makeText(getActivity(), R.string.firebase_auth_failure, Toast.LENGTH_SHORT).show();
                    googleClient.signOut();
                }
            });
        }
        catch (ApiException e) {
            getActivity().findViewById(R.id.login_indicator).setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.google_cancel, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Google login cancelled", e);
        }
    }

    private void doFacebookLogin(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        AuthHandler.SignInWithCredential(credential).observe(this, responseCode -> {
            if (responseCode == ResponseCode.SUCCESS) {
                Toast.makeText(getActivity(), R.string.signin_success, Toast.LENGTH_SHORT).show();
                onLoginSuccess();
            }
            else if (responseCode == ResponseCode.FAILURE) {
                Toast.makeText(getActivity(), R.string.firebase_auth_failure, Toast.LENGTH_SHORT).show();
                getActivity().findViewById(R.id.login_indicator).setVisibility(View.GONE);
                LoginManager.getInstance().logOut();
            }
        });
    }

    private void onLoginSuccess() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .popBackStack("account", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}