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

import org.json.JSONObject;
import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.accounts.fxa.Prefs;
import org.mozilla.accounts.fxa.net.AppGlobals;
import org.mozilla.accounts.fxa.net.HTTPResponse;
import org.mozilla.accounts.fxa.net.HttpUtil;
import org.mozilla.accounts.fxa.tasks.ProfileJson;

import java.util.HashMap;
import java.util.Map;

/*
   Instantiate this task with the bearer token.

   On successful completion, an intent of PROFILE_READ or PROFILE_READ_FAILURE will be emitted.
 */
public abstract class AbstractRetrieveProfileTask extends AsyncTask<String, Void, ProfileJson> {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(AbstractRetrieveProfileTask.class);


    final Context mContext;


    public AbstractRetrieveProfileTask(Context ctx) {
        mContext = ctx;
    }

    HttpUtil getHttpUtil() {
        return new HttpUtil(System.getProperty("http.agent")  + " " +
                AppGlobals.appName + "/" + AppGlobals.appVersionName);
    }

    protected ProfileJson doInBackground(String... strings) {
        if (strings.length != 1 || TextUtils.isEmpty(strings[0])) {
            return null;
        }
        return getUserProfile(strings[0]);
    }

    // This must be overloaded by subclasses
    protected abstract String getFxaProfileEndpoint();

    ProfileJson getUserProfile(String bearerToken) {
        Prefs prefs = Prefs.getInstance();

        try {
            prefs.setBearerToken(bearerToken);

            HttpUtil httpUtil = getHttpUtil();

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + bearerToken);
            String profileUrl=  getFxaProfileEndpoint() + "/profile";


            HTTPResponse resp = httpUtil.get(profileUrl, headers);
            ProfileJson profileJson = new ProfileJson(new JSONObject(resp.body()));

            prefs.setEmail(profileJson.getEmail());
            prefs.setEmail(profileJson.getDisplayName());


            Log.i(LOG_TAG, "Profile response body: " +profileJson);
            return profileJson;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Wut", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute (ProfileJson result) {
        if (result == null) {
            Intent intent = new Intent(Intents.PROFILE_READ_FAILURE);
            LocalBroadcastManager.getInstance(mContext).sendBroadcastSync(intent);
            return;
        }
        Intent intent = new Intent(Intents.PROFILE_READ);
        intent.putExtra("json", result.toString());
        LocalBroadcastManager.getInstance(mContext).sendBroadcastSync(intent);
    }

}
