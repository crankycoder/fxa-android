/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa.tasks.base;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.accounts.fxa.FxAGlobals;
import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.accounts.fxa.net.HTTPResponse;
import org.mozilla.accounts.fxa.net.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/*
 Use this task to set the display name in the profile server
 by passing in the bearer token and the new display name.

 The bearer token must have the scope profile:display_name:write attached to it.
 */
public abstract class AbstractSetDisplayNameTask extends AsyncTask<String, Void, String> {
    private static final String LOG_TAG = LoggerUtil.makeLogTag(AbstractSetDisplayNameTask.class);

    Context mContext;


    public AbstractSetDisplayNameTask(Context ctx) {
        mContext = ctx;
    }


    // Subclasses should override this to define where the profile server base URL is
    protected abstract String getFxaProfileEndpoint();


    protected String doInBackground(String... strings) {
        if (strings.length != 2 ||
                TextUtils.isEmpty(strings[0]) ||
                TextUtils.isEmpty(strings[1])) {
            return null;
        }
        String bearerToken = strings[0];
        String displayName = strings[1];

        return setDisplayName(bearerToken, displayName);
    }

    public AsyncTask<String, Void, String> execute(String bearerToken, String displayName) {
        return super.execute(bearerToken, displayName);
    }

    /*
     Set the display name (nickname) for a user on the FxA profile server
     */
    public String setDisplayName(String bearerToken, String displayName) {
        if (TextUtils.isEmpty(bearerToken) || TextUtils.isEmpty(displayName)) {
            Log.w(LOG_TAG, "Display name and bearer token must be set: [" + bearerToken +
                          ", " + displayName + "]");
            return null;
        }


        HttpUtil httpUtil = new HttpUtil(System.getProperty("http.agent")  + " " +
                FxAGlobals.appName + "/" + FxAGlobals.appVersionName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + bearerToken);
        headers.put("Content-Type", "application/json");
        String displayNameUrl =  getFxaProfileEndpoint() + "/display_name";


        JSONObject blob = new JSONObject();
        try {
            blob.put("displayName", displayName);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error setting display_name: ["+displayName+"]", e);
            return null;
        }

        HTTPResponse resp = httpUtil.post(displayNameUrl,
                blob.toString().getBytes(),
                headers);

        return resp.body();
    }

    @Override
    protected void onPostExecute (String result) {
        if (result == null) {
            Intent intent = new Intent(org.mozilla.accounts.fxa.Intents.DISPLAY_NAME_WRITE_FAILURE);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }
        Intent intent = new Intent(Intents.DISPLAY_NAME_WRITE);
        if (!TextUtils.isEmpty(result)) {
            intent.putExtra("json", result);
        }
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
