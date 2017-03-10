package com.goalsmadeattainable.goalsmadeattainable;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.goalsmadeattainable.goalsmadeattainable.CreateGoal.CreateGoalActivity;

import utils.DBTools;

public class UpcomingGoalsActivity extends AppCompatActivity {

    CoordinatorLayout rootLayout;
    Toolbar toolbar;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_goals);

        initializeWidgets();

        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.activity_upcoming_goals);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Goals");
        toolbar.inflateMenu(R.menu.menu_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
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

    private void createGoal() {
        Intent intent = new Intent(this, CreateGoalActivity.class);
        startActivity(intent);
    }
}
