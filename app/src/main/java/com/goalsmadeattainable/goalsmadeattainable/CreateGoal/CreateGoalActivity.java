package com.goalsmadeattainable.goalsmadeattainable.CreateGoal;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.goalsmadeattainable.goalsmadeattainable.LoginActivity;
import com.goalsmadeattainable.goalsmadeattainable.R;
import com.goalsmadeattainable.goalsmadeattainable.UpcomingGoalsActivity;

import utils.DBTools;

public class CreateGoalActivity extends AppCompatActivity {

    private RelativeLayout rootLayout;
    private Toolbar toolbar;
    private Spinner futureGoalSpinner;
    private TextInputLayout inputLayoutTitle, inputLayoutDescription;
    private EditText titleEditText, descriptionEditText;
    private Button pickDateButton, pickTimeButton, createGoalButton, cancelCreateGoalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        initializeWidgets();

        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (RelativeLayout) findViewById(R.id.activity_create_goal);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Goal");
        toolbar.inflateMenu(R.menu.menu_main);

        futureGoalSpinner = (Spinner) findViewById(R.id.future_goal_spinner);

        inputLayoutTitle = (TextInputLayout) findViewById(R.id.inputLayoutTitle);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.inputLayoutDescription);

        titleEditText = (EditText) findViewById(R.id.titleField);
        descriptionEditText = (EditText) findViewById(R.id.descriptionField);

        pickDateButton = (Button) findViewById(R.id.pick_date_button);
        pickTimeButton = (Button) findViewById(R.id.pick_time_button);
        createGoalButton = (Button) findViewById(R.id.create_goal_button);
        cancelCreateGoalButton = (Button) findViewById(R.id.cancel_submit_goal_button);
    }

    private void initializeListeners() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        // Remove active users from the database and redirect to login page
                        Activity activity = (Activity) rootLayout.getContext();
                        DBTools dbTools = new DBTools(rootLayout.getContext());
                        dbTools.removeActiveUsers();
                        dbTools.close();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                }
                return false;
            }
        });

        futureGoalSpinner.setOnItemSelectedListener(new CreateGoalsSpinnerActivity());

        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        pickTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        createGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGoal();
            }
        });

        cancelCreateGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCreateGoal();
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new CreateGoalDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new CreateGoalTimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void createGoal() {

    }

    public void cancelCreateGoal() {
        startActivity(new Intent(this, UpcomingGoalsActivity.class));
    }
}
