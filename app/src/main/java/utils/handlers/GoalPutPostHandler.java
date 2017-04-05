package utils.handlers;

import android.content.Context;
import android.content.Intent;

import com.goalsmadeattainable.goalsmadeattainable.R;

import utils.DBTools;

public class GoalPutPostHandler extends HttpURLConnectionHandler {
    private int goalID;

    public GoalPutPostHandler(String success, String failure, Intent intent,
                              GMAUrlConnection gmaUrlConnection, int goalID) {
        super(success, failure, intent, gmaUrlConnection);
        this.goalID = goalID;
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        Context context = gmaUrlConnection.getContext();
        DBTools dbTools = new DBTools(context);
        GMAUrlConnection gmaUrlConnection = new GMAUrlConnection(
                context.getString(R.string.goals_url) + goalID + "/", GMAUrlConnection.Method.GET,
                null, context, dbTools.getToken());
        dbTools.close();
        GoalDetailsURLConnectionHandler handler = new GoalDetailsURLConnectionHandler(
                "", context.getString(R.string.failed_goal_details_retrieval),
                this.intent, gmaUrlConnection, null);
        handler.execute((Void) null);
    }
}
