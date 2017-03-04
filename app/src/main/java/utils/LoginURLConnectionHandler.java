package utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.goalsmadeattainable.goalsmadeattainable.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;

public class LoginURLConnectionHandler extends HttpURLConnectionHandler {
    public LoginURLConnectionHandler(String apiEndpoint, String success, String failure, Method method,
                                     HashMap<String, String> params, Context context, Intent intent) {
        super(apiEndpoint, success, failure, method, params, context, intent);
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
                this.token = json.getString(context.getString(R.string.user_token));
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
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        if(!result.equals(failure)) {
            UserInfoURLConnectionHandler handler = new UserInfoURLConnectionHandler(
                    context.getString(R.string.user_info_url) + token + context.getString(R.string.user_token_info),
                    success, failure,
                    HttpURLConnectionHandler.Method.GET, null, context, intent);
            handler.setToken(token);
            handler.execute((Void) null);
        }
    }
}