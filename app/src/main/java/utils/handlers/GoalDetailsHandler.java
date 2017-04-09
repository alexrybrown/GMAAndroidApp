package utils.handlers;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.goalsmadeattainable.goalsmadeattainable.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import utils.DBTools;
import utils.Goal;

public class GoalDetailsHandler extends HttpHandler {
    private Toolbar toolbar;
    private TextView title, description, expectedCompletion;

    public GoalDetailsHandler(String success, String failure, Intent intent,
                              GMAUrlConnection gmaUrlConnection, Toolbar toolbar, TextView title,
                              TextView description, TextView expectedCompletion) {
        super(success, failure, intent, gmaUrlConnection);
        this.toolbar = toolbar;
        this.title = title;
        this.description = description;
        this.expectedCompletion = expectedCompletion;
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
                final Goal goal = new Goal();
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
                Activity activity = (Activity) gmaUrlConnection.getContext();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toolbar != null) {
                            toolbar.setTitle(goal.title);
                        }
                        if (title != null) {
                            title.setText(goal.title);
                        }
                        if (description != null) {
                            description.setText(goal.description);
                        }
                        if (expectedCompletion != null) {
                            expectedCompletion.setText(formatDate(goal.expectedCompletion));
                        }
                    }
                });
                DBTools dbTools = new DBTools(gmaUrlConnection.getContext());
                dbTools.createOrUpdateGoal(goal);
                dbTools.close();
                return success;
            } catch (JSONException e) {
                System.err.print(e.getMessage());
                return failure;
            } finally {
            }
        } else if(responseCode >= 200 && responseCode < 300) {
            return success;
        } else {
            return failure;
        }
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
