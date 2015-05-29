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
import android.webkit.CookieSyncManager;

import org.mozilla.accounts.fxa.FxAOAuthDialog;
import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;

public class AppMainExample extends Activity {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(AppMainExample.class);


    // These secrets are provisioned from the FxA dashboard
    private final String FXA_APP_KEY = "d0f6d2ed3c5fcc3b";
    private final String FXA_APP_SECRET = "3015f44423df9a5f08d0b5cd43e0cbb6f82c56e37f09a3909db293e17a9e64af";

    // This is the public facing URL for FxA login
    public final String FXA_SIGNIN_URL = "https://stable.dev.lcip.org/oauth/signin";

    // The Authorization server
    public static final String FXA_OAUTH_BASEURL = "https://oauth-stable.dev.lcip.org/v1";

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
            new RetrieveProfileTask().execute(jsonBlob);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        CookieSyncManager.createInstance(this);

        new FxAOAuthDialog(this,
                FXA_SIGNIN_URL,
                FXA_OAUTH_BASEURL,
                FXA_APP_CALLBACK,
                FXA_APP_KEY,
                FXA_APP_SECRET).show();
    }
}