package utils.handlers;

import android.content.Intent;

import java.io.IOException;
import java.net.HttpURLConnection;

import utils.DBTools;

public class GoalArchiveHandler extends HttpHandler {
    private int goalID;

    public GoalArchiveHandler(String success, String failure, Intent intent,
                              GMAUrlConnection gmaUrlConnection, int goalID) {
        super(success, failure, intent, gmaUrlConnection);
        this.goalID = goalID;
    }

    protected String handleResponse(HttpURLConnection conn) throws IOException {
        if(responseCode >= 200 && responseCode < 300) {
            // Archive goal
            DBTools dbTools = new DBTools(gmaUrlConnection.getContext());
            dbTools.archiveGoal(this.goalID, dbTools);
            dbTools.close();
            return success;
        } else {
            return failure;
        }
    }
}
