package utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
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
 * Created by alex on 11/24/16.
 * Handles communication with the server
 */

public class HttpURLConnectionHandler {
    public enum Method {GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE}
    public static final String ROOT_URL = "https://arcane-fjord-64904.herokuapp.com/";
    private String apiEndpoint;
    private Method method;
    private HashMap<String, String> params;
    private String response;

    /**
     * Sets up the needed information to use the handler
     * @param apiEndpoint represents the endpoint on the server this connection
     *                    needs to connect to
     * @param method represents whether this is a 'GET', 'POST', 'HEAD', 'OPTIONS',
     *               'PUT', 'DELETE', or 'TRACE'
     * @param params required parameters to be put in the url for the given method
     */
    public HttpURLConnectionHandler(String apiEndpoint, Method method,
                                    HashMap<String, String> params) {
        this.apiEndpoint = apiEndpoint;
        this.method = method;
        this.params = params;
        this.response = "";
    }

    /**
     * Accessor to get the api endpoint
     * @return the api endpoint of the class
     */
    public String getApiEndpoint() {
        return apiEndpoint;
    }

    /**
     * Accessor to get the method
     * @return given method of the class
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Accessor to get the params
     * @return given params of the class
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * Mutator to set the api endpoint
     * @param apiEndpoint endpoint on the server this connection needs to connect to
     */
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    /**
     * Mutator to set the method
     * @param method given method to communicate to server with
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Sets the given params if it is a POST method.
     * @param params represents the params to be used given a post method
     */
    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public String execute() {
        new Task().execute();
        return response;
    }

    /**
     * Task will run communications to server through async task
     */
    class Task extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            this.run();
            return null;
        }

        /**
         * Start the communication process with the server
         * @return Returns the response as a string
         */
        public void run() {
            URL url;
            try {
                url = new URL(ROOT_URL + apiEndpoint);

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
                int responseCode = conn.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while((line=br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
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
    }
}
