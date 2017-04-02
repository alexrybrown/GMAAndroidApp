package utils.handlers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GMAUrlConnection {
    public enum Method {GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE}
    private String apiEndpoint;
    private Method method;
    private HashMap<String, String> params;
    private Context context;
    private String token;
    private String message;
    private Boolean timedOut = false;

    public GMAUrlConnection(String apiEndpoint, Method method, HashMap<String, String> params,
                            Context context, String token) {
        this.apiEndpoint = apiEndpoint;
        this.method = method;
        this.params = params;
        this.context = context;
        this.token = token;
    }

    public HttpURLConnection run() {
        HttpURLConnection conn = null;
        try {
            String rootUrl = getRootUrl(context);
            URL url = new URL(rootUrl + apiEndpoint);

            // Set the basics of the connection up
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setDoInput(true);
            conn.setRequestMethod(method.name());
            // Check to see if we have a token
            if (!token.isEmpty()) {
                conn.setRequestProperty("Authorization", "Token " + token);
            }

            // If we are posting, send params to server
            if (this.method == Method.POST) {
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getParamsString(params));
                writer.flush();
                writer.close();
                os.close();
            }

        } catch (SocketTimeoutException e) {
            this.message = "Connection failed!";
            this.timedOut = true;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public Context getContext() {
        return context;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getTimedOut() {
        return timedOut;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Changes the hash map of params into a string representation
     * @return string representation of the params
     */
    private String getParamsString(HashMap<String, String> params) throws UnsupportedEncodingException {
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

    private String getRootUrl(Context context) {
        String rootUrl;
        try {
            // Get local url when developing
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.getResources().getAssets().open("url.txt")));
            rootUrl = br.readLine();
        } catch (Exception e) {
            // Get production url on server
            rootUrl = "https://arcane-fjord-64904.herokuapp.com/";
        }
        return rootUrl;
    }
}
