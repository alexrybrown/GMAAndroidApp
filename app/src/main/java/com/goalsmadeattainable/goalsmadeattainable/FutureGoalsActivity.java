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

import com.goalsmadeattainable.goalsmadeattainable.EditOrCreateGoal.EditOrCreateGoalActivity;

import java.util.ArrayList;

import utils.DBTools;
import utils.Goal;
import utils.handlers.GMAUrlConnection;
import utils.handlers.GoalsHandler;

public class FutureGoalsActivity extends AppCompatActivity {
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView futureGoalsRecyclerView;
    private RecyclerView.Adapter futureGoalsAdapter;
    private RecyclerView.LayoutManager futureGoalsLayoutManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_goals);

        initializeWidgets();
        initializeListeners();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUpcomingGoals();
    }

    private void initializeWidgets() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.activity_future_goals);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Future Goals");
        toolbar.inflateMenu(R.menu.menu_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getUpcomingGoals();

            }
        });

        futureGoalsRecyclerView = (RecyclerView) findViewById(R.id.goals_recycler_view);
        futureGoalsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        futureGoalsLayoutManager = new LinearLayoutManager(this);
        futureGoalsRecyclerView.setLayoutManager(futureGoalsLayoutManager);

        // specify an adapter
        futureGoalsAdapter = new GoalsAdapter(new ArrayList<Goal>());
        futureGoalsRecyclerView.setAdapter(futureGoalsAdapter);

        // Initialize adapter
        getUpcomingGoals();
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGoal();
            }
        });
    }

    private void getUpcomingGoals() {
        DBTools dbTools = new DBTools(this);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.future_goals_url), GMAUrlConnection.Method.GET,
                null, this, dbTools.getToken());
        dbTools.close();
        GoalsHandler handler = new GoalsHandler(
                "", getString(R.string.failed_goal_retrieval),
                null, gmaUrlConnection, futureGoalsRecyclerView, futureGoalsAdapter,
                swipeRefreshLayout, null);
        handler.execute((Void) null);
    }

    private void createGoal() {
        Intent intent = new Intent(this, EditOrCreateGoalActivity.class);
        startActivity(intent);
    }
}
