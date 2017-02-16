package utils;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

public class RegisterURLConnectionHandler extends HttpURLConnectionHandler {
    public RegisterURLConnectionHandler(String apiEndpoint, String success, String failure, Method method,
                                        HashMap<String, String> params, Context context, Intent intent) {
        super(apiEndpoint, success, failure, method, params, context, intent);
    }
}
