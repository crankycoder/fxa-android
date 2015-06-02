/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.firefoxaccounts.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.firefoxaccounts.AppGlobals;
import org.mozilla.firefoxaccounts.net.HTTPResponse;
import org.mozilla.firefoxaccounts.net.HttpUtil;
import org.mozilla.firefoxaccounts.Prefs;

import java.util.HashMap;
import java.util.Map;

/*
   Instantiate this task with the bearer token
 */
public abstract class AbstractRetrieveProfileTask extends AsyncTask<String, Void, JSONObject> {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(AbstractRetrieveProfileTask.class);

    protected JSONObject doInBackground(String... strings) {
        if (strings.length == 0 || TextUtils.isEmpty(strings[0])) {
            return null;
        }
        return getUserProfile(strings[0]);
    }

    // This must be overloaded by subclasses
    protected abstract String getFxaProfileEndpoint();

    private JSONObject getUserProfile(String bearerToken) {
        Prefs prefs = Prefs.getInstance();

        try {
            prefs.setBearerToken(bearerToken);

            HttpUtil httpUtil = new HttpUtil(System.getProperty("http.agent")  + " " +
                    AppGlobals.appName + "/" + AppGlobals.appVersionName);

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + bearerToken);


            for (String key: headers.keySet()) {
                Log.i(LOG_TAG, "Header ["+key+": "+headers.get(key)+"]");
            }
            String profileUrl=  getFxaProfileEndpoint() + "/profile";
            Log.i(LOG_TAG, "Fetching profile from : ["+profileUrl+"]");
            HTTPResponse resp = httpUtil.get(profileUrl, headers);
            JSONObject profileJson = new JSONObject(resp.body());
            String email = profileJson.getString("email");
            prefs.setEmail(email);

            Log.i(LOG_TAG, "Profile response body: " +profileJson);
            return profileJson;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Wut", e);
            return null;
        }
    }

}
