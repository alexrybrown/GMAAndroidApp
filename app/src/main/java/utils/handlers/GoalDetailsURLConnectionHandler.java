package utils.handlers;

import android.content.Intent;

import com.goalsmadeattainable.goalsmadeattainable.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import utils.Goal;

public class GoalDetailsURLConnectionHandler extends HttpURLConnectionHandler {
    private Goal goal;

    public GoalDetailsURLConnectionHandler(String success, String failure, Intent intent,
                                           GMAUrlConnection gmaUrlConnection, Boolean clearStack,
                                           Goal goal) {
        super(success, failure, intent, gmaUrlConnection, clearStack);
        this.goal = goal;
    }

    /**
     * Default method of handling a response.
     * @param conn is the http connection
     * @return a string that is used in the post execute.
     * @throws IOException
     */
    protected String handleResponse(HttpURLConnection conn) throws IOException {
        if(responseCode == HttpURLConnection.HTTP_OK) {
            // Convert the stream to a string
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while((line=br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            // Create a JSONObject to get our data
            try {
                JSONObject json = new JSONObject(sb.toString());
                // Required fields need no attempts to get data
                goal.userID = json.getInt(gmaUrlConnection.getContext().getString(R.string.user_user));
                goal.goalID = json.getInt(gmaUrlConnection.getContext().getString(R.string.goal_id));
                goal.title = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_title));
                goal.description = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_description));
                goal.createdAt = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_created_at));
                goal.expectedCompletion = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_expected_completion));
                goal.lastModified = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_last_modified));
                goal.archived = json.getBoolean(gmaUrlConnection.getContext().getString(R.string.goal_archived));
                // Attempt to get the future goal id
                if (!json.isNull(gmaUrlConnection.getContext().getString(R.string.goal_future_goal))) {
                    goal.futureGoalID = json.getInt(gmaUrlConnection.getContext().getString(R.string.goal_future_goal));
                } else {
                    goal.futureGoalID = null;
                }
                // Attempt to get comment
                if (!json.isNull(gmaUrlConnection.getContext().getString(R.string.goal_comment))) {
                    goal.comment = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_comment));
                } else {
                    goal.comment = null;
                }
                // Attempt to get finished at time
                if (!json.isNull(gmaUrlConnection.getContext().getString(R.string.goal_finished_at))) {
                    goal.finishedAt = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_finished_at));
                } else {
                    goal.finishedAt = null;
                }
                return success;
            } catch (JSONException e) {
                System.err.print(e.getMessage());
                return failure;
            } finally {
                // Wake up the thread waiting for data
                synchronized (Thread.currentThread()) {
                    Thread.currentThread().notify();
                }
            }
        } else if(responseCode >= 200 && responseCode < 300) {
            // Wake up the thread waiting for data
            synchronized (Thread.currentThread()) {
                Thread.currentThread().notify();
            }
            return success;
        } else {
            // Wake up the thread waiting for data
            synchronized (Thread.currentThread()) {
                Thread.currentThread().notify();
            }
            return failure;
        }
    }
}
