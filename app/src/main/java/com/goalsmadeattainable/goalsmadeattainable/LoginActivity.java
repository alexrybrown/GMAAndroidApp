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

import java.util.HashMap;

import utils.DBTools;
import utils.LoginURLConnectionHandler;
import utils.HttpURLConnectionHandler;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextInputLayout inputLayoutUsername, inputLayoutPassword;
    private Button signInButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeWidgets();

        initializeListeners();
    }

    private void initializeWidgets() {
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
        // If we have a token we don't need to login
        DBTools dbTools = new DBTools(this);
        if (!dbTools.getToken().isEmpty()) {
            dbTools.close();
            Intent intent = new Intent(this, UpcomingGoals.class);
            startActivity(intent);
        }
        dbTools.close();

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
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(getString(R.string.username), username);
            params.put(getString(R.string.password), password);
            Intent intent = new Intent(this, UpcomingGoals.class);
            LoginURLConnectionHandler handler = new LoginURLConnectionHandler(
                    getString(R.string.login_url), getString(R.string.login_successful),
                    getString(R.string.failed_to_login), HttpURLConnectionHandler.Method.POST,
                    params, this, intent);
            handler.execute((Void) null);
        }
    }

    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

