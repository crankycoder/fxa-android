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

import static org.mozilla.accounts.fxa.Intents.OAUTH_DESTROY;
import static org.mozilla.accounts.fxa.Intents.OAUTH_DESTROY_FAILURE;

public class DestroyOAuthTask extends AsyncTask<String, Void, Boolean> {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(DestroyOAuthTask.class);
    final String oauth2_endpoint;
    private final Context mContext;

    public DestroyOAuthTask(Context ctx, String oauth2Endpoint) {
        mContext = ctx;
        oauth2_endpoint = oauth2Endpoint;
    }

    public String getOauth2Endpoint() {
        return oauth2_endpoint;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        if (strings.length != 1 || TextUtils.isEmpty(strings[0])) {
            Log.i(LOG_TAG, "Missing bearer token for DestroyOAuth");
            return false;
        }

        return verify(strings[0]);
    }

    public AsyncTask<String, Void, Boolean> execute(String bearerToken) {
        return super.execute(bearerToken);
    }

    public boolean verify(String bearerToken) {
        if (TextUtils.isEmpty(bearerToken)) {
            Log.w(LOG_TAG, "Bearer token must be set: [" + bearerToken + "]");
            return false;
        }

        HttpUtil httpUtil = new HttpUtil(System.getProperty("http.agent")  + " " +
                FxAGlobals.appName + "/" + FxAGlobals.appVersionName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        String displayNameUrl =  getOauth2Endpoint() + "/v1/destroy";


        JSONObject blob = new JSONObject();
        try {
            blob.put("token", bearerToken);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error setting token: ["+bearerToken+"]", e);
            return false;
        }

        HTTPResponse resp = httpUtil.post(displayNameUrl,
                blob.toString().getBytes(),
                headers);
        return resp.isSuccessCode2XX();

    }

    @Override
    protected void onPostExecute (Boolean result) {
        if (result) {
            Intent intent = new Intent(OAUTH_DESTROY);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        } else {
            Intent intent = new Intent(OAUTH_DESTROY_FAILURE);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }
}
