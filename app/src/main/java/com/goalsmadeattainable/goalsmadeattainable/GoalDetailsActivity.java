package com.goalsmadeattainable.goalsmadeattainable;

import android.app.Activity;
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

import com.goalsmadeattainable.goalsmadeattainable.CreateGoal.CreateGoalActivity;

import java.util.ArrayList;

import utils.DBTools;
import utils.Goal;
import utils.handlers.GMAUrlConnection;
import utils.handlers.GoalDetailsURLConnectionHandler;
import utils.handlers.GoalsURLConnectionHandler;

public class GoalDetailsActivity extends AppCompatActivity {
    private int goalID;
    private Goal goal;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView goalDetailsRecyclerView;
    private RecyclerView.Adapter goalDetailsAdapter;
    private RecyclerView.LayoutManager goalDetailsLayoutManager;
    private FloatingActionButton fab;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);

        initializeWidgets();
        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.activity_goal_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getGoalDetails();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
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
                    case R.id.edit_goal:
                        break;
                    case R.id.delete_goal:
                        break;
                    case R.id.logout:
                        // Remove active users from the database and redirect to login page
                        Activity activity = (Activity) rootLayout.getContext();
                        DBTools dbTools = new DBTools(rootLayout.getContext());
                        dbTools.removeActiveUsers();
                        dbTools.close();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
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
                null, gmaUrlConnection, false, toolbar);
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
                null, gmaUrlConnection, false, goalDetailsRecyclerView, goalDetailsAdapter,
                swipeRefreshLayout);
        handler.execute((Void) null);
    }

    private void createSubGoal() {
        Intent intent = new Intent(this, CreateGoalActivity.class);
        intent.putExtra(getString(R.string.goal_id), goalID);
        startActivity(intent);
    }
}
