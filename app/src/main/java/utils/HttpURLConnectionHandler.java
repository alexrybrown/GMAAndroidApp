package utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Task will run communications to server through async task
 */
public abstract class HttpURLConnectionHandler extends AsyncTask<Void, Void, String> {
    public enum Method {GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE}
    protected static final String ROOT_URL = "https://arcane-fjord-64904.herokuapp.com/";
    protected String apiEndpoint;
    protected Method method;
    protected HashMap<String, String> params;
    protected int responseCode;
    protected Context context;
    protected Intent intent;
    protected String success;
    protected String failure;

    /**
     * Sets up the needed information to use the handler
     * @param apiEndpoint represents the endpoint on the server this connection
     *                    needs to connect to
     * @param method represents whether this is a 'GET', 'POST', 'HEAD', 'OPTIONS',
     *               'PUT', 'DELETE', or 'TRACE'
     * @param params required parameters to be put in the url for the given method
     */
    public HttpURLConnectionHandler(String apiEndpoint, String success, String failure, Method method,
                                    HashMap<String, String> params, Context context, Intent intent) {
        this.apiEndpoint = apiEndpoint;
        this.method = method;
        this.params = params;
        this.responseCode = 0;
        this.context = context;
        this.intent = intent;
        this.success = success;
        this.failure = failure;
    }

    // Starts the communication process with the server
    protected String doInBackground(Void... params) {
        try {
            URL url = new URL(ROOT_URL + apiEndpoint);

            // Set the basics of the connection up
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method.name());

            // If we have params send them to the server
            if(params != null) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getParamsString());
                writer.flush();
                writer.close();
                os.close();
            }

            // Get the response code
            responseCode = conn.getResponseCode();

            // Handle the response
            return this.handleResponse(conn);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        if(!result.equals(failure)) {
            context.startActivity(intent);
        }
    }

    /**
     * Changes the hash map of params into a string representation
     * @return string representation of the params
     */
    private String getParamsString() throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()) {
            if(first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
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
