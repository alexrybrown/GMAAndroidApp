package utils;

import android.content.Intent;

import com.goalsmadeattainable.goalsmadeattainable.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class UserInfoURLConnectionHandler extends HttpURLConnectionHandler {
    public UserInfoURLConnectionHandler(String success, String failure, Intent intent,
                                        GMAUrlConnection gmaUrlConnection, Boolean clearStack) {
        super(success, failure, intent, gmaUrlConnection, clearStack);
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
                DBTools dbTools = new DBTools(gmaUrlConnection.getContext());
                // Create the user if they don't exist in the database
                if (!dbTools.checkUserExistsByToken(gmaUrlConnection.getToken())) {
                    User user = new User();
                    user.userID = json.getInt(gmaUrlConnection.getContext().getString(R.string.user_id));
                    user.firstName = json.getString(gmaUrlConnection.getContext().getString(R.string.user_first_name));
                    user.lastName = json.getString(gmaUrlConnection.getContext().getString(R.string.user_last_name));
                    user.username = json.getString(gmaUrlConnection.getContext().getString(R.string.user_username));
                    user.email = json.getString(gmaUrlConnection.getContext().getString(R.string.user_email));
                    user.token = gmaUrlConnection.getToken();
                    dbTools.createUser(user);
                }
                // Remove the active user and set the active user to the one who signed in
                dbTools.removeActiveUsers();
                dbTools.setActiveUser(gmaUrlConnection.getToken());
                dbTools.close();
                return success;
            } catch (JSONException e) {
                System.err.print(e.getMessage());
                return failure;
            }
        } else if(responseCode >= 200 && responseCode < 300) {
            return success;
        } else {
            return failure;
        }
    }
}
