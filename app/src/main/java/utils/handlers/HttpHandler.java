package utils.handlers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Task will run communications to server through async task
 */
public class HttpHandler extends AsyncTask<Void, Void, String> {
    protected int responseCode = 0;
    protected Intent intent;
    protected String success;
    protected String failure;
    protected GMAUrlConnection gmaUrlConnection;
    protected ProgressDialog progressDialog;


    public HttpHandler(String success, String failure, Intent intent,
                       GMAUrlConnection gmaUrlConnection) {
        this.intent = intent;
        this.success = success;
        this.failure = failure;
        this.gmaUrlConnection = gmaUrlConnection;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(gmaUrlConnection.getContext());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    // Starts the communication process with the server
    protected String doInBackground(Void... params) {
        // Run the connection handler
        HttpURLConnection conn = gmaUrlConnection.run();

        if (gmaUrlConnection.getTimedOut()) {
            return gmaUrlConnection.getMessage();
        }

        try {
            // Get the response code
            responseCode = conn.getResponseCode();

            // Handle the response
            return this.handleResponse(conn);
        } catch (IOException e) {
            e.printStackTrace();
            return "Critical Failure";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        clearProgressDialog();
        if (!result.isEmpty()) {
            Toast.makeText(gmaUrlConnection.getContext(), result, Toast.LENGTH_SHORT).show();
        }
        if (result.equals(success)) {
            if (intent != null) {
                gmaUrlConnection.getContext().startActivity(intent);
            }
        }
    }

    /**
     * Default method of handling a response.
     * @param conn is the http connection
     * @return a string that is used in the post execute.
     * @throws IOException
     */
    protected String handleResponse(HttpURLConnection conn) throws IOException {
        if(responseCode >= 200 && responseCode < 300) {
            return success;
        } else {
            return failure;
        }
    }

    protected void clearProgressDialog() {
        progressDialog.dismiss();
        progressDialog = null;
    }
}
