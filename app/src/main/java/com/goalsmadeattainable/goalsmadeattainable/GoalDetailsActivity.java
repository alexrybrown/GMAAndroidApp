package com.goalsmadeattainable.goalsmadeattainable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.goalsmadeattainable.goalsmadeattainable.CreateGoal.EditOrCreateGoalActivity;

import java.util.ArrayList;

import utils.DBTools;
import utils.Goal;
import utils.handlers.GMAUrlConnection;
import utils.handlers.GoalArchiveHandler;
import utils.handlers.GoalCompleteHandler;
import utils.handlers.GoalDetailsHandler;
import utils.handlers.GoalsHandler;
import utils.handlers.HttpHandler;

public class GoalDetailsActivity extends AppCompatActivity {
    private int goalID;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView detailLayout;
    private TextView goalTitleTextView, goalDescriptionTextView, goalExpectedCompletionTextView;
    private RecyclerView goalDetailsRecyclerView;
    private RecyclerView.Adapter goalDetailsAdapter;
    private RecyclerView.LayoutManager goalDetailsLayoutManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);

        goalID = getIntent().getIntExtra(getString(R.string.goal_id), 0);
        initializeWidgets();
        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.activity_goal_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        detailLayout = (NestedScrollView) findViewById(R.id.goal_details_nested_layout);

        goalTitleTextView = (TextView) findViewById(R.id.goal_details_title);
        goalDescriptionTextView = (TextView) findViewById(R.id.goal_details_description);
        goalExpectedCompletionTextView = (TextView) findViewById(R.id.goal_details_expected_completion);

        goalDetailsRecyclerView = (RecyclerView) findViewById(R.id.goal_details_recycler_view);
        goalDetailsRecyclerView.setHasFixedSize(true);

        // user a linear layout manager
        goalDetailsLayoutManager = new LinearLayoutManager(this);
        goalDetailsRecyclerView.setLayoutManager(goalDetailsLayoutManager);

        // specify an adapter
        goalDetailsAdapter = new GoalsAdapter(new ArrayList<Goal>());
        goalDetailsRecyclerView.setAdapter(goalDetailsAdapter);

        // initialize goal data
        getGoalDetails();
        getSubGoals();

        // If we have sub goals set detail layout invisible and recycler view visible or vice versa
        DBTools dbTools = new DBTools(this);
        if (dbTools.hasSubGoals(goalID)) {
            viewSubGoals();
        } else {
            viewGoalOnly();
        }
        dbTools.close();
    }

    private void initializeListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSubGoal();
            }
        });
    }

    private void getGoalDetails() {
        DBTools dbTools = new DBTools(this);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.goals_url) + goalID + "/", GMAUrlConnection.Method.GET,
                null, this, dbTools.getToken());
        dbTools.close();
        GoalDetailsHandler handler = new GoalDetailsHandler(
                "", getString(R.string.failed_goal_details_retrieval),
                null, gmaUrlConnection, toolbar, goalTitleTextView, goalDescriptionTextView,
                goalExpectedCompletionTextView);
        handler.execute((Void) null);
    }

    private void getSubGoals() {
        DBTools dbTools = new DBTools(this);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.goals_url) + goalID + "/" + getString(R.string.sub_goals_url),
                GMAUrlConnection.Method.GET, null, this, dbTools.getToken());
        dbTools.close();
        GoalsHandler handler = new GoalsHandler(
                "", getString(R.string.failed_goal_retrieval),
                null, gmaUrlConnection, goalDetailsRecyclerView, goalDetailsAdapter,
                swipeRefreshLayout, this);
        handler.execute((Void) null);
    }

    private void createSubGoal() {
        Intent intent = new Intent(this, EditOrCreateGoalActivity.class);
        intent.putExtra(getString(R.string.future_goal_id), goalID);
        startActivity(intent);
    }

    // Switch view to see sub goals
    public void viewSubGoals() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        detailLayout.setVisibility(View.GONE);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_goal_sub_goals);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_goal:
                        editGoal();
                        break;
                    case R.id.swap:
                        viewGoal();
                        break;
                    case R.id.delete_goal:
                        confirmArchiveGoal();
                        break;
                    case R.id.jump_to_future_goals:
                        jumpToFutureGoals();
                        break;
                    case R.id.logout:
                        logout();
                        break;
                }
                return false;
            }
        });
    }

    // Switch view to see goal details and allow viewing of sub goals
    private void viewGoal() {
        swipeRefreshLayout.setVisibility(View.GONE);
        detailLayout.setVisibility(View.VISIBLE);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_goal_details);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_goal:
                        editGoal();
                        break;
                    case R.id.swap:
                        viewSubGoals();
                        break;
                    case R.id.delete_goal:
                        confirmArchiveGoal();
                        break;
                    case R.id.jump_to_future_goals:
                        jumpToFutureGoals();
                        break;
                    case R.id.logout:
                        logout();
                        break;
                }
                return false;
            }
        });
    }

    // Switch view to see goal details only
    public void viewGoalOnly() {
        swipeRefreshLayout.setVisibility(View.GONE);
        detailLayout.setVisibility(View.VISIBLE);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_goal_details_only);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_goal:
                        editGoal();
                        break;
                    case R.id.complete_goal:
                        confirmCompleteGoal();
                        break;
                    case R.id.delete_goal:
                        confirmArchiveGoal();
                        break;
                    case R.id.jump_to_future_goals:
                        jumpToFutureGoals();
                        break;
                    case R.id.logout:
                        logout();
                        break;
                }
                return false;
            }
        });
    }

    // Edit given goal in activity
    private void editGoal() {
        // Forward to edit activity
        Intent intent = new Intent(this, EditOrCreateGoalActivity.class);
        intent.putExtra(this.getString(R.string.edit_goal_id), goalID);
        startActivity(intent);
    }

    // Confirm archive of given goal in activity
    private void confirmArchiveGoal() {
        // Confirm archive
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Delete");
        alertDialogBuilder.setMessage("Are you sure you want to delete this goal and any sub goals it might have?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Archive the goal
                archiveGoal();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });

        // create the box
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    // Confirm completed given goal in activity
    private void confirmCompleteGoal() {
        // Confirm complete
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Completed");
        alertDialogBuilder.setMessage("Have you completed this goal?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // complete the goal
                completeGoal();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });

        // create the box
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    // Archive the given goal
    private void archiveGoal() {
        DBTools dbTools = new DBTools(this);
        Goal goal = dbTools.getGoal(this.goalID);
        // Setup redirect to different activity
        Intent intent;
        // Check and see if our goal we archived had a future goal
        if (goal.futureGoalID != null) {
            dbTools = new DBTools(this);
            Goal futureGoal = dbTools.getGoal(goal.futureGoalID);
            intent = new Intent(this, GoalDetailsActivity.class);
            intent.putExtra(getString(R.string.goal_id), futureGoal.goalID);
        } else { // Redirect them to the future goals activity
            intent = new Intent(this, FutureGoalsActivity.class);
        }
        dbTools.close();
        // Clear the stack and remove this activity from the stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.goals_url) + goal.goalID + "/" + getString(R.string.archive_goal_url),
                GMAUrlConnection.Method.POST, null, this, dbTools.getToken());
        GoalArchiveHandler handler = new GoalArchiveHandler("",
                getString(R.string.failed_goal_archive), intent, gmaUrlConnection, goal.goalID, goal.futureGoalID);
        this.finish();
        handler.execute((Void) null);
    }

    // Complete the given goal
    private void completeGoal() {
        DBTools dbTools = new DBTools(this);
        Goal goal = dbTools.getGoal(this.goalID);
        // Setup redirect to different activity
        Intent intent;
        // Check and see if our goal we completed had a future goal
        if (goal.futureGoalID != null) {
            dbTools = new DBTools(this);
            Goal futureGoal = dbTools.getGoal(goal.futureGoalID);
            intent = new Intent(this, GoalDetailsActivity.class);
            intent.putExtra(getString(R.string.goal_id), futureGoal.goalID);
        } else { // Redirect them to the future goals activity
            intent = new Intent(this, FutureGoalsActivity.class);
        }
        dbTools.close();
        // Clear the stack and remove this activity from the stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.goals_url) + goal.goalID + "/" + getString(R.string.complete_goal_url),
                GMAUrlConnection.Method.POST, null, this, dbTools.getToken());
        GoalCompleteHandler handler = new GoalCompleteHandler("",
                getString(R.string.failed_goal_archive), intent, gmaUrlConnection, goal.goalID, goal.futureGoalID);
        this.finish();
        handler.execute((Void) null);
    }

    // Jump to future goal screen
    private void jumpToFutureGoals() {
        Intent intent = new Intent(this, FutureGoalsActivity.class);
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
}
