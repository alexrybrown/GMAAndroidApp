package utils;

import android.content.Intent;
import android.widget.Toast;

import com.goalsmadeattainable.goalsmadeattainable.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class LoginURLConnectionHandler extends HttpURLConnectionHandler {
    public LoginURLConnectionHandler(String success, String failure, Intent intent,
                                     GMAUrlConnection gmaUrlConnection) {
        super(success, failure, intent, gmaUrlConnection);
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
                gmaUrlConnection.setToken(
                        json.getString(gmaUrlConnection.getContext().getString(R.string.user_token)));
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

    @Override
    protected void onPostExecute(String result) {
        if(!result.equals(failure)) {
            gmaUrlConnection.setApiEndpoint(gmaUrlConnection.getContext().getString(R.string.user_info_url)
                    + gmaUrlConnection.getToken()
                    + gmaUrlConnection.getContext().getString(R.string.user_token_info));
            gmaUrlConnection.setMethod(GMAUrlConnection.Method.GET);
            UserInfoURLConnectionHandler handler = new UserInfoURLConnectionHandler(
                    success, failure, intent, gmaUrlConnection);
            handler.execute((Void) null);
        } else {
            Toast.makeText(gmaUrlConnection.getContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
