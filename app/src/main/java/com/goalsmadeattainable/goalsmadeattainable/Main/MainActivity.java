package com.goalsmadeattainable.goalsmadeattainable.Main;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.goalsmadeattainable.goalsmadeattainable.EditOrCreateGoal.EditOrCreateGoalActivity;
import com.goalsmadeattainable.goalsmadeattainable.LoginActivity;
import com.goalsmadeattainable.goalsmadeattainable.R;

import utils.DBTools;

public class MainActivity extends AppCompatActivity {
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeWidgets();
        initializeListeners();
    }

    private void initializeWidgets() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Goals Made Attainable");
        toolbar.inflateMenu(R.menu.menu_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Future Goals"));
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming Goals"));
        tabLayout.addTab(tabLayout.newTab().setText("Overdue Goals"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        // If we have a fragment number set the tab to start at
        if (getIntent().getIntExtra(getString(R.string.fragment_number), -1) != -1) {
            viewPager.setCurrentItem(getIntent().getExtras().getInt(getString(R.string.fragment_number)));
            switch (getIntent().getExtras().getInt(getString(R.string.fragment_number))) {
                case 0:
                    fab.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    fab.setVisibility(View.GONE);
                    break;
                case 2:
                    fab.setVisibility(View.GONE);
                    break;
            }
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGoal();
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        fab.setVisibility(View.GONE);
                        break;
                    case 2:
                        fab.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.setScrollPosition(tab.getPosition(), 0f, true);
                switch (tab.getPosition()) {
                    case 0:
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        fab.setVisibility(View.GONE);
                        break;
                    case 2:
                        fab.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void createGoal() {
        Intent intent = new Intent(this, EditOrCreateGoalActivity.class);
        startActivity(intent);
    }
}
