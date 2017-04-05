package com.goalsmadeattainable.goalsmadeattainable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.goalsmadeattainable.goalsmadeattainable.CreateGoal.EditOrCreateGoalActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import utils.DBTools;
import utils.Goal;
import utils.handlers.GMAUrlConnection;
import utils.handlers.GoalDetailsURLConnectionHandler;
import utils.handlers.GoalsURLConnectionHandler;

public class GoalDetailsActivity extends AppCompatActivity {
    private int goalID;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView goalDetailsRecyclerView;
    private RecyclerView.Adapter goalDetailsAdapter;
    private RecyclerView.LayoutManager goalDetailsLayoutManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);

        initializeWidgets();
        initializeListeners();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getGoalDetails();
        getSubGoals();
    }

    private void initializeWidgets() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.activity_goal_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getGoalDetails();
        toolbar.inflateMenu(R.menu.menu_goal_details);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getGoalDetails();
                getSubGoals();
            }
        });

        goalDetailsRecyclerView = (RecyclerView) findViewById(R.id.goal_details_recycler_view);
        goalDetailsRecyclerView.setHasFixedSize(true);

        // user a linear layout manager
        goalDetailsLayoutManager = new LinearLayoutManager(this);
        goalDetailsRecyclerView.setLayoutManager(goalDetailsLayoutManager);

        // specify an adapter
        goalDetailsAdapter = new GoalsAdapter(new ArrayList<Goal>());
        goalDetailsRecyclerView.setAdapter(goalDetailsAdapter);

        // initialize goal data
        getSubGoals();
    }

    private void initializeListeners() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.view_goal:
                        viewGoal();
                        break;
                    case R.id.edit_goal:
                        editGoal();
                        break;
                    case R.id.finish_goal:
                        break;
                    case R.id.delete_goal:
                        break;
                    case R.id.logout:
                        logout();
                        break;
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSubGoal();
            }
        });
    }

    private void getGoalDetails() {
        goalID = getIntent().getExtras().getInt(getString(R.string.goal_id));
        DBTools dbTools = new DBTools(this);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.goals_url) + goalID + "/", GMAUrlConnection.Method.GET,
                null, this, dbTools.getToken());
        dbTools.close();
        GoalDetailsURLConnectionHandler handler = new GoalDetailsURLConnectionHandler(
                "", getString(R.string.failed_goal_details_retrieval),
                null, gmaUrlConnection, toolbar);
        handler.execute((Void) null);
    }

    private void getSubGoals() {
        DBTools dbTools = new DBTools(this);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.goals_url) + goalID + "/" + getString(R.string.get_sub_goals_url),
                GMAUrlConnection.Method.GET, null, this, dbTools.getToken());
        dbTools.close();
        GoalsURLConnectionHandler handler = new GoalsURLConnectionHandler(
                "", getString(R.string.failed_goal_retrieval),
                null, gmaUrlConnection, goalDetailsRecyclerView, goalDetailsAdapter,
                swipeRefreshLayout);
        handler.execute((Void) null);
    }

    private void createSubGoal() {
        Intent intent = new Intent(this, EditOrCreateGoalActivity.class);
        intent.putExtra(getString(R.string.future_goal_id), goalID);
        startActivity(intent);
    }

    // Remove active users from the database and redirect to login page
    private void logout() {
        Activity activity = (Activity) rootLayout.getContext();
        DBTools dbTools = new DBTools(rootLayout.getContext());
        dbTools.removeActiveUsers();
        dbTools.close();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    // Great alert dialog to view goal details
    private void viewGoal() {
        final Activity activity = this;
        final int goalID = this.goalID;
        DBTools dbTools = new DBTools(this);
        Goal goal = dbTools.getGoal(this.goalID);
        dbTools.close();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(goal.title + " Details");
        alertDialogBuilder.setMessage(
                "Title: \n\n" + goal.title + "\n\n"
                        + "Description: \n\n" + goal.description + "\n\n"
                        + "Expected Completion: \n\n" + formatDate(goal.expectedCompletion) + "\n\n");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });
        alertDialogBuilder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Forward to edit activity
                Intent intent = new Intent(activity, EditOrCreateGoalActivity.class);
                intent.putExtra(activity.getString(R.string.edit_goal_id), goalID);
                startActivity(intent);
            }
        });

        // create the box
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    // Edit given goal
    private void editGoal() {
        // Forward to edit activity
        Intent intent = new Intent(this, EditOrCreateGoalActivity.class);
        intent.putExtra(this.getString(R.string.edit_goal_id), goalID);
        startActivity(intent);
    }

    // Format dates from database
    private String formatDate(String date) {
        DateFormat finalDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        DateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        currentDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(currentDateFormat.parse(date));
            return finalDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            return date;
        }
    }
}
