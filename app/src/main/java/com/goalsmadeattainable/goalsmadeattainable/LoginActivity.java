package com.goalsmadeattainable.goalsmadeattainable;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.HashMap;

import utils.DBTools;
import utils.handlers.GMAUrlConnection;
import utils.handlers.LoginURLConnectionHandler;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    private RelativeLayout rootLayout;
    private EditText usernameEditText, passwordEditText;
    private TextInputLayout inputLayoutUsername, inputLayoutPassword;
    private Button signInButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If we have an active user or the user is already in our
        // database then skip making the request.
        DBTools dbTools = new DBTools(this);
        if (dbTools.checkActiveUserExists()) {
            dbTools.close();
            Intent intent = new Intent(this, FutureGoalsActivity.class);
            startActivity(intent);
            finish();
        }
        dbTools.close();

        initializeWidgets();

        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (RelativeLayout) findViewById(R.id.activity_login);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        inputLayoutUsername = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);

        usernameEditText = (EditText) findViewById(R.id.usernameField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);

        signInButton = (Button) findViewById(R.id.user_sign_in_button);
        registerButton = (Button) findViewById(R.id.register_button);
    }

    private void initializeListeners() {
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        boolean isValid = true;

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty()) {
            inputLayoutUsername.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.error_invalid_password));
            isValid = false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        if (isValid) {
            // Setup our params for login
            HashMap<String, String> params = new HashMap<>();
            params.put(getString(R.string.username), username);
            params.put(getString(R.string.password), password);
            Intent intent = new Intent(this, FutureGoalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                    getString(R.string.login_url), GMAUrlConnection.Method.POST,
                    params, this, "");
            LoginURLConnectionHandler handler = new LoginURLConnectionHandler(
                    getString(R.string.login_successful), getString(R.string.failed_to_login),
                    intent, gmaUrlConnection);
            handler.execute((Void) null);
        }
    }

    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

