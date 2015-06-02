package org.mozilla.firefoxaccounts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.accounts.fxa.DevOAuthDialog;
import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.firefoxaccounts.tasks.DevRetrieveProfileTask;

public class MainApp extends Activity {
    private static final String LOG_TAG = LoggerUtil.makeLogTag(MainApp.class);

    // These secrets are provisioned from the FxA dashboard
    private final String FXA_APP_KEY = "d0f6d2ed3c5fcc3b";

    // And finally the callback endpoint on our web application
    // Example server endpoint code is available under the `sample_endpoint` subdirectory.
    public final String FXA_APP_CALLBACK = "http://ec2-52-1-93-147.compute-1.amazonaws.com/fxa/callback";


    private final BroadcastReceiver callbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
                Sample JSON that you might get back
                {"access_token":"fadf25f84838877d6eb03563f501abfac62c0a01aaf98b34eec1b28e888b02a2",
                "token_type":"bearer",
                "scope":"profile:email",
                "auth_at":1432917700}
            */

            String jsonBlob = intent.getStringExtra("json");
            if (jsonBlob == null) {
                Log.w(LOG_TAG, "error extracting json data");
                return;
            }
            JSONObject authJSON = null;
            try {
                authJSON = new JSONObject(jsonBlob);
                Log.i(LOG_TAG, "Login yielded this JSON blob: " + authJSON);
                String bearerToken = authJSON.getString("access_token");
                Prefs.getInstance().setBearerToken(bearerToken);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error fetching bearer token. JSON = ["+authJSON+"]", e);
            }
            new DevRetrieveProfileTask().execute(Prefs.getInstance().getBearerToken());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.createInstance(getApplicationContext());

        AppGlobals.appVersionName = BuildConfig.VERSION_NAME;
        AppGlobals.appVersionCode = BuildConfig.VERSION_CODE;
        AppGlobals.appName = this.getResources().getString(R.string.app_name);


        setContentView(R.layout.appmainexample);

        IntentFilter intentFilter = new IntentFilter(Intents.ORG_MOZILLA_ACCOUNTS_FXA_SIGNIN_TOKEN);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(callbackReceiver, intentFilter);
    }

    /*
     This initiates the FxA login user flow
     */
    public void onClick_fxaLogin(View v) {
        CookieSyncManager cookies = CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
        CookieManager.getInstance().removeSessionCookie();
        cookies.sync();

        // Only untrusted scopes can go here for now.
        // If you add an scope that is not on that list, the login screen will hang instead
        // of going to the final redirect.  No user visible error occurs. This is terrible.
        // https://github.com/mozilla/fxa-content-server/issues/2508
        String[] scopes = new String[] {"profile:email", "profile:display_name", "profile:display_name:write"};
        new DevOAuthDialog(this,
                FXA_APP_CALLBACK,
                scopes,
                FXA_APP_KEY).show();
    }
}