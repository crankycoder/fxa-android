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

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.accounts.fxa.DialogListener;
import org.mozilla.accounts.fxa.FxAOAuthDialog;
import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;

public class AppMainExample extends Activity {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(AppMainExample.class);

    private final BroadcastReceiver callbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Just capture the intent for testing
            String jsonBlob = intent.getStringExtra("json");
            if (jsonBlob == null) {
                Log.w(LOG_TAG, "error extracting json data");
                return;
            }
            Log.i(LOG_TAG, "Received: " + jsonBlob);
            try {
                JSONObject jsonObj = new JSONObject(jsonBlob);
                Log.i(LOG_TAG, "Validated JSON: " + jsonObj.toString());
            } catch (JSONException jse) {
                Log.i(LOG_TAG, "Invalid JSON: " + jsonBlob);
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appmainexample);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(callbackReceiver,
                new IntentFilter(Intents.ORG_MOZILLA_ACCOUNTS_FXA_SIGNIN_TOKEN));
    }

    public void onClick_fxaLogin(View v) {
        String authRequestRedirect = "https://stable.dev.lcip.org/oauth/signin"
                + "?client_id=" + FxAOAuthDialog.FXA_APP_KEY
                + "&state=99" // I don't care about state
                + "&scope=profile:email"
                + "&redirect_uri=" + FxAOAuthDialog.FXA_APP_CALLBACK_OAUTHCALLBACK;

        CookieSyncManager.createInstance(this);

        DialogListener listener = new DialogListener();
        new FxAOAuthDialog(this, authRequestRedirect, listener).show();
    }
}