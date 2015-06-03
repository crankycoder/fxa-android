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
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.accounts.fxa.net.AppGlobals;
import org.mozilla.accounts.fxa.net.HTTPResponse;
import org.mozilla.accounts.fxa.net.HttpUtil;

import java.util.HashMap;
import java.util.Map;

import static org.mozilla.accounts.fxa.Intents.OAUTH_VERIFY;
import static org.mozilla.accounts.fxa.Intents.OAUTH_VERIFY_FAIL;

@SuppressWarnings("unused")
public class DevVerifyOAuthTask extends AsyncTask<String, Void, JSONObject> {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(DevVerifyOAuthTask.class);
    private final Context mContext;

    public String getOauth2Endpoint() {
        return "https://oauth-stable.dev.lcip.org/v1";
    }

    public DevVerifyOAuthTask(Context ctx) {
        mContext = ctx;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        if (strings.length != 1 || TextUtils.isEmpty(strings[0])) {
            return null;
        }

        return verify(strings[0]);
    }

    public JSONObject verify(String bearerToken) {
        if (TextUtils.isEmpty(bearerToken)) {
            Log.w(LOG_TAG, "Bearer token must be set: [" + bearerToken + "]");
            return null;
        }


        HttpUtil httpUtil = new HttpUtil(System.getProperty("http.agent")  + " " +
                AppGlobals.appName + "/" + AppGlobals.appVersionName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        String displayNameUrl =  getOauth2Endpoint() + "/verify";


        JSONObject blob = new JSONObject();
        try {
            blob.put("token", bearerToken);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error setting token: ["+bearerToken+"]", e);
            return null;
        }

        HTTPResponse resp = httpUtil.post(displayNameUrl, blob.toString().getBytes(), headers);
        if (resp.isSuccessCode2XX()) {
            try {
                return new JSONObject(resp.body());
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error fetching verification response.", e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute (JSONObject result) {
        if (result == null) {
            Intent intent = new Intent(OAUTH_VERIFY_FAIL);
            LocalBroadcastManager.getInstance(mContext).sendBroadcastSync(intent);
        } else {
            Intent intent = new Intent(OAUTH_VERIFY);
            intent.putExtra("json", result.toString());
            LocalBroadcastManager.getInstance(mContext).sendBroadcastSync(intent);
        }
    }

}
