/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

public class FxAGlobals {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(FxAGlobals.class);

    public static String appVersionName;
    public static int appVersionCode;
    public static String appName;


    // Collapse these into one interface
    static IFxaCallback callback;

    public final static BroadcastReceiver fxaCallbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intents.ORG_MOZILLA_ACCOUNTS_FXA_BEARER_TOKEN)) {
                processBearerToken(intent);
            } else if (intent.getAction().equals(Intents.PROFILE_READ)) {
                processProfile(context, intent);
            } else if (intent.getAction().equals(Intents.PROFILE_READ_FAILURE)) {
                // bad profile reads automatically logout the user
                processProfileReadFail();
            } else if (intent.getAction().equals(Intents.OAUTH_DESTROY)) {
                processOAuthDestroy();
            } else if (intent.getAction().equals(Intents.OAUTH_DESTROY_FAIL)) {
                // I don't care.  Clear the login state even if fxa logout 'fails'
                processOAuthDestroyFailure();
            } else if (intent.getAction().equals(Intents.DISPLAY_NAME_WRITE)) {
                processDisplayNameWrite();
            } else if (intent.getAction().equals(Intents.DISPLAY_NAME_WRITE_FAILURE)) {
                processDisplayNameWriteFailure();
            } else if (intent.getAction().equals(Intents.OAUTH_VERIFY)) {
                // Do nothing
                processOAuthVerify();
            } else if (intent.getAction().equals(Intents.OAUTH_VERIFY_FAIL)) {
                processOAuthVerifyFailure();
            }
        }
    };


    public static void initFxaLogin(Context ctx, String app_name) {
        // Clobber the FxAGlobals so that the user-agent for the FxA client will be sensible
        FxAGlobals.appVersionName = BuildConfig.VERSION_NAME;
        FxAGlobals.appVersionCode = BuildConfig.VERSION_CODE;
        FxAGlobals.appName = app_name;


        IntentFilter intentFilter = new IntentFilter();
        Intents.registerFxaIntents(intentFilter);


        LocalBroadcastManager
                .getInstance(ctx.getApplicationContext())
                .registerReceiver(fxaCallbackReceiver, intentFilter);
    }

    public static void register(IFxaCallback client) {
        callback = client;
    }


    private static void processProfile(Context ctx, Intent intent) {
        if (callback == null) {
            return;
        }

        try {
            String jsonBlob = intent.getStringExtra("json");
            callback.acceptProfile(new JSONObject(jsonBlob));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error processing JSON", e);
        }
    }


    private static void processProfileReadFail() {
        if (callback == null) {
            return;
        }
        callback.profileReadFailure();
    }

    private static void processBearerToken(Intent intent) {
        if (callback == null) {
            return;
        }

        String jsonBlob = intent.getStringExtra("json");
        if (TextUtils.isEmpty(jsonBlob)) {
            Log.w(LOG_TAG, "error extracting json data");
            return;
        }
        JSONObject authJSON = null;
        try {
            authJSON = new JSONObject(jsonBlob);
            callback.acceptFxaBearerToken(authJSON.getString("access_token"));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error fetching bearer token.", e);
            return;
        }
    }

    private static void processOAuthVerifyFailure() {
        if (callback == null) {
            return;
        }
        callback.oauthVerificationFailure();
    }

    private static void processOAuthVerify() {
        if (callback == null) {
            return;
        }
        callback.acceptOAuthVerified();
    }

    private static void processOAuthDestroyFailure() {
        if (callback == null) {
            return;
        }
        callback.acceptOAuthDestroy();
    }

    private static void processOAuthDestroy() {
        if (callback == null) {
            return;
        }
        callback.oauthDestroyFailure();
    }

    private static void processDisplayNameWriteFailure() {
        if (callback == null) {
            return;
        }
        callback.acceptDisplayNameWrite();
    }

    private static void processDisplayNameWrite() {
        if (callback == null) {
            return;
        }
        callback.acceptDisplayNameWriteFailure();
    }
}

