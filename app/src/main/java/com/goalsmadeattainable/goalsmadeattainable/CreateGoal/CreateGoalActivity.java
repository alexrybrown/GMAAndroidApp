package com.goalsmadeattainable.goalsmadeattainable.CreateGoal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.goalsmadeattainable.goalsmadeattainable.GoalDetailsActivity;
import com.goalsmadeattainable.goalsmadeattainable.LoginActivity;
import com.goalsmadeattainable.goalsmadeattainable.R;
import com.goalsmadeattainable.goalsmadeattainable.FutureGoalsActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import utils.DBTools;
import utils.handlers.GMAUrlConnection;
import utils.Goal;
import utils.handlers.GoalDetailsURLConnectionHandler;
import utils.handlers.HttpURLConnectionHandler;

public class CreateGoalActivity extends AppCompatActivity {
    public static ContentValues values = new ContentValues();
    public static Button pickDateButton, pickTimeButton;
    private RelativeLayout rootLayout;
    private Toolbar toolbar;
    private TextInputLayout inputLayoutTitle, inputLayoutDescription;
    private EditText titleEditText, descriptionEditText;
    private Button createGoalButton, cancelCreateGoalButton;
    private CreateGoalDatePickerFragment datePickerFragment;
    private CreateGoalTimePickerFragment timePickerFragment;
    private int goalID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        getGoalDetails();
        initializeWidgets();
        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (RelativeLayout) findViewById(R.id.activity_create_goal);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // If we have a goal then set title different
        toolbar.setTitle("Create Goal");
        toolbar.inflateMenu(R.menu.menu_main);

        inputLayoutTitle = (TextInputLayout) findViewById(R.id.inputLayoutTitle);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.inputLayoutDescription);

        titleEditText = (EditText) findViewById(R.id.titleField);
        descriptionEditText = (EditText) findViewById(R.id.descriptionField);

        // Get needed times for time picker buttons
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat prettyDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        SimpleDateFormat prettyTimeFormat = new SimpleDateFormat("hh:mm a");
        prettyDateFormat.setCalendar(calendar);
        prettyTimeFormat.setCalendar(calendar);
        Date date = calendar.getTime();

        // Setup initial text of times for picker buttons
        pickDateButton = (Button) findViewById(R.id.pick_date_button);
        pickDateButton.setText(prettyDateFormat.format(date));
        pickTimeButton = (Button) findViewById(R.id.pick_time_button);
        pickTimeButton.setText(prettyTimeFormat.format(date));

        // Set utc time
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = calendar.getTime();
        CreateGoalActivity.values.put("date", dateFormat.format(date));
        CreateGoalActivity.values.put("time", timeFormat.format(date));

        createGoalButton = (Button) findViewById(R.id.create_goal_button);
        cancelCreateGoalButton = (Button) findViewById(R.id.cancel_submit_goal_button);

        datePickerFragment = new CreateGoalDatePickerFragment();
        timePickerFragment = new CreateGoalTimePickerFragment();
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
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void createGoal() {
        Goal goal = new Goal();
        goal.title = titleEditText.getText().toString();
        goal.description = descriptionEditText.getText().toString();
        goal.expectedCompletion = getExpectedCompletionTime();

        boolean isValid = true;

        if (goal.title.isEmpty()) {
            inputLayoutTitle.setError(getString(R.string.empty_title));
            isValid = false;
        } else {
            inputLayoutTitle.setErrorEnabled(false);
        }

        if (goal.description.isEmpty()) {
            inputLayoutDescription.setError(getString(R.string.empty_description));
            isValid = false;
        } else {
            inputLayoutDescription.setErrorEnabled(false);
        }

        if(isValid) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(getString(R.string.goal_title), goal.title);
            params.put(getString(R.string.goal_description), goal.description);
            params.put(getString(R.string.goal_expected_completion), goal.expectedCompletion);
            GMAUrlConnection gmaUrlConnection;
            Intent intent;
            DBTools dbTools = new DBTools(this);
            // If we have a future goal use different url
            if (goalID != 0) {
                intent = new Intent(this, GoalDetailsActivity.class);
                intent.putExtra(getString(R.string.goal_id), goalID);
                gmaUrlConnection = new GMAUrlConnection(
                        getString(R.string.goals_url) + goalID + "/" + getString(R.string.add_sub_goal_url),
                        GMAUrlConnection.Method.POST, params, this, dbTools.getToken());
            } else {
                intent = new Intent(this, FutureGoalsActivity.class);
                gmaUrlConnection = new GMAUrlConnection(
                        getString(R.string.goals_url), GMAUrlConnection.Method.POST,
                        params, this, dbTools.getToken());
            }
            dbTools.close();
            HttpURLConnectionHandler handler = new HttpURLConnectionHandler(
                    getString(R.string.goal_created), getString(R.string.failed_goal_creation),
                    intent, gmaUrlConnection, true);
            handler.execute((Void) null);
        }
    }

    public void cancelCreateGoal() {
        startActivity(new Intent(this, FutureGoalsActivity.class));
    }

    public String getExpectedCompletionTime() {
        return values.get("date") + "T" + values.get("time") + "Z";
    }

    private void getGoalDetails() {
        goalID = getIntent().getIntExtra(getString(R.string.goal_id), 0);
        if (goalID != 0) {
            DBTools dbTools = new DBTools(this);
            GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                    getString(R.string.goals_url) + goalID + "/", GMAUrlConnection.Method.GET,
                    null, this, dbTools.getToken());
            dbTools.close();
            GoalDetailsURLConnectionHandler handler = new GoalDetailsURLConnectionHandler(
                    "", getString(R.string.failed_goal_details_retrieval),
                    null, gmaUrlConnection, false, null);
            handler.execute((Void) null);
        }
    }
}
