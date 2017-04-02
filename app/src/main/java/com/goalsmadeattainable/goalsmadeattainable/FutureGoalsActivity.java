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
import utils.handlers.GoalsURLConnectionHandler;

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
                refreshItems();
            }
        });

        futureGoalsRecyclerView = (RecyclerView) findViewById(R.id.goals_recycler_view);
        futureGoalsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        futureGoalsLayoutManager = new LinearLayoutManager(this);
        futureGoalsRecyclerView.setLayoutManager(futureGoalsLayoutManager);

        // specify an adapter
        ArrayList<Goal> goals = getUpcomingGoals();
        futureGoalsAdapter = new GoalsAdapter(goals);
        futureGoalsRecyclerView.setAdapter(futureGoalsAdapter);
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

    // Retrieve new goal data set
    private void refreshItems() {
        ArrayList<Goal> goals = getUpcomingGoals();
        futureGoalsRecyclerView.setAdapter(new GoalsAdapter(goals));
        onItemsLoadComplete();
    }

    // Finish recycler and adapter notification
    private void onItemsLoadComplete() {
        futureGoalsRecyclerView.removeAllViews();
        futureGoalsAdapter.notifyDataSetChanged();
        // Stop refresh animation
        swipeRefreshLayout.setRefreshing(false);
    }

    private ArrayList<Goal> getUpcomingGoals() {
        DBTools dbTools = new DBTools(this);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.future_goals_url), GMAUrlConnection.Method.GET,
                null, this, dbTools.getToken());
        dbTools.close();
        ArrayList<Goal> goals = new ArrayList<>();
        GoalsURLConnectionHandler handler = new GoalsURLConnectionHandler(
                "", getString(R.string.failed_goal_retrieval),
                null, gmaUrlConnection, false, goals);
        handler.execute((Void) null);
        // Sleep while we wait for the data to populate
        try {
            synchronized (Thread.currentThread()) {
                Thread.currentThread().wait(1000);
            }
        } catch (InterruptedException e) {}
        return goals;
    }

    private void createGoal() {
        Intent intent = new Intent(this, CreateGoalActivity.class);
        startActivity(intent);
    }
}
