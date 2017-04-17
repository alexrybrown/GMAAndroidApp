package com.goalsmadeattainable.goalsmadeattainable.Main;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goalsmadeattainable.goalsmadeattainable.R;

import java.util.ArrayList;

import utils.DBTools;
import utils.Goal;
import utils.handlers.GMAUrlConnection;
import utils.handlers.GoalsHandler;

public class UpcomingGoalsFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView upcomingGoalsRecyclerView;
    private RecyclerView.Adapter upcomingGoalsAdapter;
    private RecyclerView.LayoutManager upcomingGoalsLayoutManager;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_goals, container, false);

        initializeWidgets(view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            getUpcomingGoals();
        }
    }

    private void initializeWidgets(View view) {
        emptyView = (TextView) view.findViewById(R.id.empty_view);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getUpcomingGoals();
            }
        });

        upcomingGoalsRecyclerView = (RecyclerView) view.findViewById(R.id.goals_recycler_view);
        upcomingGoalsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        upcomingGoalsLayoutManager = new LinearLayoutManager(getContext());
        upcomingGoalsRecyclerView.setLayoutManager(upcomingGoalsLayoutManager);

        // specify an adapter
        upcomingGoalsAdapter = new GoalsAdapter(new ArrayList<Goal>(), 1);
        upcomingGoalsRecyclerView.setAdapter(upcomingGoalsAdapter);

        // Initialize adapter
        getUpcomingGoals();
    }

    public void getUpcomingGoals() {
        DBTools dbTools = new DBTools(getContext());
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.upcoming_goals_url), GMAUrlConnection.Method.GET,
                null, getContext(), dbTools.getToken());
        dbTools.close();
        GoalsHandler handler = new GoalsHandler(
                "", getString(R.string.failed_goal_retrieval),
                null, gmaUrlConnection, upcomingGoalsRecyclerView, upcomingGoalsAdapter,
                swipeRefreshLayout, null, emptyView);
        handler.execute((Void) null);
    }
}
