package utils;

import android.content.Context;
import android.content.Intent;

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
            StringBuffer sb = new StringBuffer("Success");
            String line;
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while((line=br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } else if(responseCode >= 200 && responseCode < 300) {
            return success;
        } else {
            return failure;
        }
    }
}
