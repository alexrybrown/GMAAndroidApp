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

public class FutureGoalsFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView futureGoalsRecyclerView;
    private RecyclerView.Adapter futureGoalsAdapter;
    private RecyclerView.LayoutManager futureGoalsLayoutManager;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future_goals, container, false);

        initializeWidgets(view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            getFutureGoals();
        }
    }

    private void initializeWidgets(View view) {
        emptyView = (TextView) view.findViewById(R.id.empty_view);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getFutureGoals();
            }
        });

        futureGoalsRecyclerView = (RecyclerView) view.findViewById(R.id.goals_recycler_view);
        futureGoalsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        futureGoalsLayoutManager = new LinearLayoutManager(getContext());
        futureGoalsRecyclerView.setLayoutManager(futureGoalsLayoutManager);

        // specify an adapter
        futureGoalsAdapter = new GoalsAdapter(new ArrayList<Goal>(), 0);
        futureGoalsRecyclerView.setAdapter(futureGoalsAdapter);

        // Initialize adapter
        getFutureGoals();
    }

    public void getFutureGoals() {
        DBTools dbTools = new DBTools(getContext());
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                getString(R.string.future_goals_url), GMAUrlConnection.Method.GET,
                null, getContext(), dbTools.getToken());
        dbTools.close();
        GoalsHandler handler = new GoalsHandler(
                "", getString(R.string.failed_goal_retrieval),
                null, gmaUrlConnection, futureGoalsRecyclerView, futureGoalsAdapter,
                swipeRefreshLayout, null, emptyView);
        handler.execute((Void) null);
    }
}
