/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

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

import java.util.HashMap;
import java.util.Map;

import static org.mozilla.accounts.fxa.Intents.OAUTH_VERIFY;
import static org.mozilla.accounts.fxa.Intents.OAUTH_VERIFY_FAILURE;

public class VerifyOAuthTask extends AsyncTask<String, Void, JSONObject> {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(VerifyOAuthTask.class);
    private final String oauth2_endpoint;
    private final Context mContext;

    public VerifyOAuthTask(Context ctx, String oauth2Endpoint) {
        mContext = ctx;
        oauth2_endpoint = oauth2Endpoint;
    }

    public String getOauth2Endpoint() {
        return oauth2_endpoint;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        if (strings.length != 1 || TextUtils.isEmpty(strings[0])) {
            Log.i(LOG_TAG, "Missing a bearer token.");
            return null;
        }

        return verify(strings[0]);
    }

    public AsyncTask<String, Void, JSONObject> execute(String bearerToken) {
        return super.execute(bearerToken);
    }

    public JSONObject verify(String bearerToken) {
        if (TextUtils.isEmpty(bearerToken)) {
            Log.w(LOG_TAG, "Bearer token must be set.");
            return null;
        }

        HttpUtil httpUtil = new HttpUtil(System.getProperty("http.agent") + " " +
                FxAGlobals.appName + "/" + FxAGlobals.appVersionName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        String verifyTokenURL = getOauth2Endpoint() + "/v1/verify";

        JSONObject blob = new JSONObject();
        try {
            blob.put("token", bearerToken);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error setting bearer token.");
            return null;
        }

        HTTPResponse resp = httpUtil.post(verifyTokenURL, blob.toString().getBytes(), headers);
        if (resp.isSuccessCode2XX()) {
            try {
                return new JSONObject(resp.body());
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON couldn't be decoded from verification response.");
                return null;
            }
        } else {
            Log.w(LOG_TAG, "HTTP Status: " + resp.httpStatusCode());
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (result == null) {
            Intent intent = new Intent(OAUTH_VERIFY_FAILURE);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        } else {
            Intent intent = new Intent(OAUTH_VERIFY);
            intent.putExtra("json", result.toString());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }
}
