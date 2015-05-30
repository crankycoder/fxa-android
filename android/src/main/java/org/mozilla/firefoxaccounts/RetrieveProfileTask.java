/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.firefoxaccounts;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.mozilla.accounts.fxa.LoggerUtil;

import java.util.HashMap;
import java.util.Map;

class RetrieveProfileTask extends AsyncTask<String, Void, JSONObject> {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(RetrieveProfileTask.class);

    protected JSONObject doInBackground(String... jsonBlobs) {
        try {
            String jsonBlob = jsonBlobs[0];
            Log.i(LOG_TAG, "Raw JSON blog: ["+jsonBlob+"]");
            JSONObject jsonObj = new JSONObject(jsonBlob);
            String bearerToken = jsonObj.getString("access_token");
            HttpUtil httpUtil = new HttpUtil("some-user-agent");

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + bearerToken);


            for (String key: headers.keySet()) {
                Log.i(LOG_TAG, "Header ["+key+": "+headers.get(key)+"]");
            }
            String profileUrl=  AppMainExample.FXA_PROFILE_ENDPOINT + "/profile";
            Log.i(LOG_TAG, "Fetching profile from : ["+profileUrl+"]");
            HTTPResponse resp = httpUtil.get(profileUrl, headers);
            JSONObject profileJson = new JSONObject(resp.body());

            Log.i(LOG_TAG, "Profile response body: " +profileJson);
            return profileJson;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Wut", e);
            return null;
        }
    }

    protected void onPostExecute(JSONObject profileJson) {

        // TODO: check this.exception
        // TODO: do something with the feed
    }
}