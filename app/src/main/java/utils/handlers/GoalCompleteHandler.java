package utils.handlers;

import android.content.Intent;

import com.goalsmadeattainable.goalsmadeattainable.GoalDetailsActivity;

import java.io.IOException;
import java.net.HttpURLConnection;

import utils.DBTools;

public class GoalCompleteHandler extends HttpHandler {
    private int goalID;
    private Integer futureGoalID;

    public GoalCompleteHandler(String success, String failure, Intent intent,
                              GMAUrlConnection gmaUrlConnection, int goalID,
                              Integer futureGoalID) {
        super(success, failure, intent, gmaUrlConnection);
        this.goalID = goalID;
        this.futureGoalID = futureGoalID;
    }

    /**
     * Default method of handling a response.
     * @param conn is the http connection
     * @return a string that is used in the post execute.
     * @throws IOException
     */
    protected String handleResponse(HttpURLConnection conn) throws IOException {
        if(responseCode == HttpURLConnection.HTTP_OK) {
            DBTools dbTools = new DBTools(gmaUrlConnection.getContext());
            dbTools.completeGoal(goalID, dbTools);
            final GoalDetailsActivity activity = (GoalDetailsActivity) gmaUrlConnection.getContext();
            if (futureGoalID != null) {
                final Boolean hasSubGoals = dbTools.hasSubGoals(futureGoalID);
                // Update data in recycler view
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (hasSubGoals) {
                            activity.viewSubGoals();
                        } else {
                            activity.viewGoalOnly();
                        }
                    }
                });
            } else {
                // Update data in recycler view
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.viewSubGoals();
                    }
                });
            }
            dbTools.close();
            return success;
        } else if(responseCode >= 200 && responseCode < 300) {
            return success;
        } else {
            return failure;
        }
    }
}
