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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import utils.DBTools;
import utils.handlers.GMAUrlConnection;
import utils.Goal;
import utils.handlers.HttpURLConnectionHandler;

public class EditOrCreateGoalActivity extends AppCompatActivity {
    public static ContentValues values = new ContentValues();
    public static Button pickDateButton, pickTimeButton;
    private RelativeLayout rootLayout;
    private Toolbar toolbar;
    private TextInputLayout inputLayoutTitle, inputLayoutDescription;
    private EditText titleEditText, descriptionEditText;
    private Button createGoalButton, cancelCreateGoalButton;
    private EditOrCreateGoalDatePickerFragment datePickerFragment;
    private EditOrCreateGoalTimePickerFragment timePickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_or_create_goal);

        initializeWidgets();
        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (RelativeLayout) findViewById(R.id.activity_create_goal);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        inputLayoutTitle = (TextInputLayout) findViewById(R.id.inputLayoutTitle);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.inputLayoutDescription);

        titleEditText = (EditText) findViewById(R.id.titleField);
        descriptionEditText = (EditText) findViewById(R.id.descriptionField);

        createGoalButton = (Button) findViewById(R.id.create_goal_button);
        cancelCreateGoalButton = (Button) findViewById(R.id.cancel_submit_goal_button);

        if (!populateWidgets()) {
            setTimes(null);
        }

        datePickerFragment = new EditOrCreateGoalDatePickerFragment();
        timePickerFragment = new EditOrCreateGoalTimePickerFragment();

        // Change text depending on the creation or editing of a goal
        // If we are editing a goal, set title differently
        if (getIntent().getIntExtra(getString(R.string.edit_goal_id), 0) != 0) {
            toolbar.setTitle("Edit Goal");
            createGoalButton.setText(getString(R.string.edit_goal_button));
        } else {
            toolbar.setTitle("Create Goal");
            createGoalButton.setText(getString(R.string.create_goal));
        }
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

    // If we are editing a goal, populate the widgets
    private Boolean populateWidgets() {
        if (getIntent().getIntExtra(getString(R.string.edit_goal_id), 0) != 0) {
            DBTools dbTools = new DBTools(this);
            Goal goal = dbTools.getGoal(getIntent().getIntExtra(getString(R.string.edit_goal_id), 0));
            titleEditText.setText(goal.title);
            descriptionEditText.setText(goal.description);
            setTimes(goal.expectedCompletion);
            return true;
        }
        return false;
    }

    private void setTimes(String time) {
        // Get needed times for time picker buttons
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat prettyDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        SimpleDateFormat prettyTimeFormat = new SimpleDateFormat("hh:mm a");
        Calendar calendar = Calendar.getInstance();

        if (time != null) {
            DateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            currentDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                calendar.setTime(currentDateFormat.parse(time));
            } catch (ParseException e) {}
        }
        prettyDateFormat.setCalendar(calendar);
        prettyTimeFormat.setCalendar(calendar);
        Date date = calendar.getTime();

        // Setup initial text of times for picker buttons
        pickDateButton = (Button) findViewById(R.id.pick_date_button);
        pickDateButton.setText(prettyDateFormat.format(date));
        pickTimeButton = (Button) findViewById(R.id.pick_time_button);
        pickTimeButton.setText(prettyTimeFormat.format(date));

        // Set utc time
        date = calendar.getTime();
        EditOrCreateGoalActivity.values.put("date", dateFormat.format(date));
        EditOrCreateGoalActivity.values.put("time", timeFormat.format(date));
        EditOrCreateGoalActivity.values.put("edit_goal_time", time);
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
            HashMap<String, String> params = new HashMap<>();
            params.put(getString(R.string.goal_title), goal.title);
            params.put(getString(R.string.goal_description), goal.description);
            params.put(getString(R.string.goal_expected_completion), goal.expectedCompletion);
            GMAUrlConnection gmaUrlConnection;
            Intent intent;
            DBTools dbTools = new DBTools(this);
            // If we have a future or edit goal use different url
            if (getIntent().getIntExtra(getString(R.string.goal_id), 0) != 0) {
                intent = new Intent(this, GoalDetailsActivity.class);
                intent.putExtra(getString(R.string.future_goal_id), getIntent().getIntExtra(getString(R.string.future_goal_id), 0));
                gmaUrlConnection = new GMAUrlConnection(
                        getString(R.string.goals_url) + getIntent().getIntExtra(getString(R.string.future_goal_id), 0)
                                + "/" + getString(R.string.add_sub_goal_url),
                        GMAUrlConnection.Method.POST, params, this, dbTools.getToken());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dbTools.close();
                HttpURLConnectionHandler handler = new HttpURLConnectionHandler(
                        getString(R.string.goal_created), getString(R.string.failed_goal_creation),
                        intent, gmaUrlConnection);
                handler.execute((Void) null);
            } else if (getIntent().getIntExtra(getString(R.string.edit_goal_id), 0) != 0) {
                intent = new Intent(this, GoalDetailsActivity.class);
                intent.putExtra(getString(R.string.goal_id), getIntent().getIntExtra(getString(R.string.edit_goal_id), 0));
                gmaUrlConnection = new GMAUrlConnection(
                        getString(R.string.goals_url) + getIntent().getIntExtra(getString(R.string.edit_goal_id), 0) + "/",
                        GMAUrlConnection.Method.PUT, params, this, dbTools.getToken());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dbTools.close();
                HttpURLConnectionHandler handler = new HttpURLConnectionHandler(
                        getString(R.string.goal_update), getString(R.string.failed_goal_update),
                        intent, gmaUrlConnection);
                handler.execute((Void) null);
            }
            else {
                intent = new Intent(this, FutureGoalsActivity.class);
                gmaUrlConnection = new GMAUrlConnection(
                        getString(R.string.goals_url), GMAUrlConnection.Method.POST,
                        params, this, dbTools.getToken());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dbTools.close();
                HttpURLConnectionHandler handler = new HttpURLConnectionHandler(
                        getString(R.string.goal_created), getString(R.string.failed_goal_creation),
                        intent, gmaUrlConnection);
                handler.execute((Void) null);
            }
        }
    }

    public void cancelCreateGoal() {
        Intent intent;
        // If we have a future or edit goal use different activity return
        if (getIntent().getIntExtra(getString(R.string.future_goal_id), 0) != 0) {
            intent = new Intent(this, GoalDetailsActivity.class);
            intent.putExtra(getString(R.string.goal_id), getIntent().getIntExtra(getString(R.string.future_goal_id), 0));
        } else if (getIntent().getIntExtra(getString(R.string.edit_goal_id), 0) != 0) {
            intent = new Intent(this, GoalDetailsActivity.class);
            intent.putExtra(getString(R.string.goal_id), getIntent().getIntExtra(getString(R.string.edit_goal_id), 0));
        }
        else {
            intent = new Intent(this, FutureGoalsActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public String getExpectedCompletionTime() {
        Calendar calendar = Calendar.getInstance();
        String localTime = values.get("date") + " " + values.get("time");
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat complexDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            calendar.setTime(simpleDateFormat.parse(localTime));
            complexDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return complexDateFormat.format(calendar.getTime());
        } catch (ParseException e) {}
        return "";
    }
}