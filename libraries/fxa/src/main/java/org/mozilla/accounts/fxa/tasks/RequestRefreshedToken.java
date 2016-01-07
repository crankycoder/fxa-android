package org.mozilla.accounts.fxa.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.accounts.fxa.FxAGlobals;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.accounts.fxa.net.HTTPResponse;
import org.mozilla.accounts.fxa.net.HttpUtil;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.mozilla.accounts.fxa.Intents.ACCESS_TOKEN_REFRESH;
import static org.mozilla.accounts.fxa.Intents.ACCESS_TOKEN_REFRESH_FAILURE;

/**
 * Created by victorng on 2015-12-31.
 *
 *
 * This class jsut calls GET on a URL, passes in a refresh token, an existing access token
 * and the server processes
 *
 */
public class RequestRefreshedToken extends AsyncTask<String, Void, JSONObject> {

    // Most applications should use a refreshed access token on application startup.
    // This will minimize the lifetime of any access token.

    private static final String LOG_TAG = LoggerUtil.makeLogTag(RequestRefreshedToken.class);
    private final String refresh_endpoint;
    private final Context mContext;

    public RequestRefreshedToken(Context ctx, String refresh_token_endpoint) {
        mContext = ctx;
        refresh_endpoint = refresh_token_endpoint;
    }

    public String getRefreshTokenEndpoint() {
        return refresh_endpoint;
    }

    /*
     This task requires :

     1. an access token
     2. a refresh token
     */
    @Override
    protected JSONObject doInBackground(String... strings) {
        if (strings.length != 2) {
            Log.i(LOG_TAG, "Invalid number of arguments.");
            return null;
        }


        return verify(strings);
    }

    public AsyncTask<String, Void, JSONObject> execute(String bearerToken) {
        return super.execute(bearerToken);
    }

    public JSONObject verify(String... strings) {
        String bearerToken = strings[0];
        String refreshToken = strings[1];


        if (TextUtils.isEmpty(bearerToken)) {
            Log.w(LOG_TAG, "Missing an access token.");
            return null;
        }

        if (TextUtils.isEmpty(refreshToken)) {
            Log.w(LOG_TAG, "Missing a refresh token.");
            return null;
        }

        HttpUtil httpUtil = new HttpUtil(System.getProperty("http.agent") + " " +
                FxAGlobals.appName + "/" + FxAGlobals.appVersionName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer: " + bearerToken);

        JSONObject blob = new JSONObject();
        try {
            blob.put("refresh_token", refreshToken);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "Error setting refresh token to get the new access token.");
            return null;
        }

        HTTPResponse resp = httpUtil.post_nozip(getRefreshTokenEndpoint(),
                blob.toString().getBytes(),
                headers);

        if (resp.isSuccessCode2XX()) {
            try {
                return new JSONObject(resp.body());
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error marshalling the refreshed access token.");
                return null;
            }
        } else {
            Log.w(LOG_TAG, "Refresh token HTTP Status: " + resp.httpStatusCode());
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (result == null) {
            Intent intent = new Intent(ACCESS_TOKEN_REFRESH_FAILURE);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        } else {
            Intent intent = new Intent(ACCESS_TOKEN_REFRESH);
            intent.putExtra("json", result.toString());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }
}
