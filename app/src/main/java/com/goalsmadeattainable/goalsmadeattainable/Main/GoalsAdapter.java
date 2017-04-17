package com.goalsmadeattainable.goalsmadeattainable.Main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goalsmadeattainable.goalsmadeattainable.GoalDetailsActivity;
import com.goalsmadeattainable.goalsmadeattainable.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import utils.Goal;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder> {
    private ArrayList<Goal> goals;
    private int fragmentNumber;

    // Provide a reference to the views for each data item
    public static class GoalsViewHolder extends RecyclerView.ViewHolder {
        CardView goalsCV;
        TextView goalTitle;
        TextView goalDescription;
        TextView goalComment;
        TextView goalExpectedCompletion;
        Goal goal;
        Context context;
        int fragmentNumber;

        public GoalsViewHolder(View itemView, int fragmentNumber) {
            super(itemView);
            goalsCV = (CardView) itemView.findViewById(R.id.goals_cv);
            goalTitle = (TextView) itemView.findViewById(R.id.goal_title);
            goalDescription = (TextView) itemView.findViewById(R.id.goal_description);
            goalComment = (TextView) itemView.findViewById(R.id.goal_comment);
            goalExpectedCompletion = (TextView) itemView.findViewById(R.id.goal_expected_completion);
            context = itemView.getContext();
            this.fragmentNumber = fragmentNumber;
            // Set the on click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    forwardToGoalDetails(v);
                }
            });
        }

        // Forward to goal details activity
        private void forwardToGoalDetails(View v) {
            Intent intent = new Intent(v.getContext(), GoalDetailsActivity.class);
            intent.putExtra(v.getContext().getString(R.string.goal_id), goal.goalID);
            intent.putExtra(v.getContext().getString(R.string.fragment_number), fragmentNumber);
            v.getContext().startActivity(intent);
        }
    }

    // Constructor with array list of our goals
    public GoalsAdapter(ArrayList<Goal> goals, int fragmentNumber) {
        this.goals = goals;
        this.fragmentNumber = fragmentNumber;
    }

    // Get the goals
    public ArrayList<Goal> getGoals() {
        return goals;
    }

    // Get the fragment number of this adapter
    public int getFragmentNumber() {
        return fragmentNumber;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.goals_cards,
                parent, false);
        return new GoalsViewHolder(v, fragmentNumber);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GoalsViewHolder GoalsViewHolder, int i) {
        GoalsViewHolder.goalTitle.setText(goals.get(i).title);
        GoalsViewHolder.goalDescription.setText(goals.get(i).description);
        GoalsViewHolder.goalComment.setText(goals.get(i).comment);
        // Format the date
        String formattedDate = formatDate(goals.get(i).expectedCompletion);
        GoalsViewHolder.goalExpectedCompletion.setText("Expected Completion: " + formattedDate);
        // Set the goal
        GoalsViewHolder.goal = goals.get(i);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Returns the size of our data set
    @Override
    public int getItemCount() {
        return goals.size();
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
