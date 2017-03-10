package com.goalsmadeattainable.goalsmadeattainable;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.HashMap;

import utils.GMAUrlConnection;
import utils.HttpURLConnectionHandler;


public class RegisterActivity extends AppCompatActivity {

    private RelativeLayout rootLayout;
    private TextInputLayout inputLayoutFirstName, inputLayoutLastName, inputLayoutEmail,
            inputLayoutUsername, inputLayoutPassword, inputLayoutReenterPassword;
    private EditText firstNameEditText, lastNameEditText, emailEditText, usernameEditText,
            passwordEditText, reenterPasswordEditText;
    private Button submitButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeWidgets();

        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (RelativeLayout) findViewById(R.id.activity_register);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        inputLayoutFirstName = (TextInputLayout) findViewById(R.id.inputLayoutFirstName);
        inputLayoutLastName = (TextInputLayout) findViewById(R.id.inputLayoutLastName);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        inputLayoutReenterPassword = (TextInputLayout) findViewById(R.id.inputLayoutReenterPassword);

        firstNameEditText = (EditText) findViewById(R.id.firstNameField);
        lastNameEditText = (EditText) findViewById(R.id.lastNameField);
        emailEditText = (EditText) findViewById(R.id.emailField);
        usernameEditText = (EditText) findViewById(R.id.usernameField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        reenterPasswordEditText = (EditText) findViewById(R.id.reenterPasswordField);

        submitButton = (Button) findViewById(R.id.submit_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
    }

    private void initializeListeners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    /** Called when user clicks submit button **/
    public void register() {
        // Get the String values to create the user
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String reenterPassword = reenterPasswordEditText.getText().toString();
        // Check to make sure everything is valid
        boolean isValid = true;

        if (firstName.isEmpty()) {
            inputLayoutFirstName.setError(getString(R.string.empty_first_name));
            isValid = false;
        } else {
            inputLayoutFirstName.setErrorEnabled(false);
        }

        if (lastName.isEmpty()) {
            inputLayoutLastName.setError(getString(R.string.empty_last_name));
            isValid = false;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }

        if (email.isEmpty()) {
            inputLayoutEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        if (username.isEmpty()) {
            inputLayoutUsername.setError(getString(R.string.empty_username));
            isValid = false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.empty_password));
            isValid = false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        if (reenterPassword.isEmpty()) {
            inputLayoutReenterPassword.setError(getString(R.string.empty_reenter_password));
            isValid = false;
        } else {
            inputLayoutReenterPassword.setErrorEnabled(false);
        }

        if (!password.equals(reenterPassword)) {
            inputLayoutPassword.setError(getString(R.string.invalid_password));
            inputLayoutReenterPassword.setError(getString(R.string.invalid_password));
            isValid = false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
            inputLayoutReenterPassword.setErrorEnabled(false);
        }

        if(isValid) {
            // Set up our handler
            HashMap<String, String> params = new HashMap<>();
            params.put(getString(R.string.first_name), firstName);
            params.put(getString(R.string.last_name), lastName);
            params.put(getString(R.string.email), email);
            params.put(getString((R.string.username)), username);
            params.put(getString(R.string.password), password);
            params.put(getString(R.string.confirm_password), reenterPassword);
            Intent intent = new Intent(this, LoginActivity.class);
            GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                    getString(R.string.register_url), GMAUrlConnection.Method.POST,
                    params, this, "");
            HttpURLConnectionHandler handler = new HttpURLConnectionHandler(
                    getString(R.string.registration_successful), getString(R.string.failed_to_register), intent,
                    gmaUrlConnection, true);
            // Execute the task and forward to the next activity if successful
            handler.execute((Void) null);
        }
    }

    /**
     * If cancelled, return to login page
     */
    public void cancel() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}