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

import org.json.JSONObject;
import org.mozilla.accounts.fxa.FxAGlobals;
import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.accounts.fxa.net.HTTPResponse;
import org.mozilla.accounts.fxa.net.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class RetrieveProfileTask extends AsyncTask<String, Void, ProfileJson> {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(RetrieveProfileTask.class);
    final String fxa_profile_endpoint;
    final Context mContext;

    public RetrieveProfileTask(Context ctx, String profile_endpoint) {
        mContext = ctx;
        fxa_profile_endpoint = profile_endpoint;
    }

    // Profile endpoint
    protected  String getFxaProfileEndpoint() {
        return fxa_profile_endpoint;
    }

    HttpUtil getHttpUtil() {
        return new HttpUtil(System.getProperty("http.agent") + " " +
                FxAGlobals.appName + "/" + FxAGlobals.appVersionName);
    }

    protected ProfileJson doInBackground(String... strings) {
        if (strings.length != 1 || TextUtils.isEmpty(strings[0])) {
            return null;
        }
        return getUserProfile(strings[0]);
    }

    public AsyncTask<String, Void, ProfileJson> execute(String bearerToken) {
        return super.execute(bearerToken);
    }

    ProfileJson getUserProfile(String bearerToken) {

        try {
            HttpUtil httpUtil = getHttpUtil();

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + bearerToken);
            String profileUrl = getFxaProfileEndpoint() + "/v1/profile";

            HTTPResponse resp = httpUtil.get(profileUrl, headers);
            ProfileJson profileJson = new ProfileJson(new JSONObject(resp.body()));

            return profileJson;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Wut", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(ProfileJson result) {
        if (result == null) {
            Intent intent = new Intent(Intents.PROFILE_READ_FAILURE);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }
        Intent intent = new Intent(Intents.PROFILE_READ);
        intent.putExtra("json", result.toString());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
