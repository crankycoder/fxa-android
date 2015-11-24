package org.mozilla.firefoxaccounts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.accounts.fxa.FxAGlobals;
import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.accounts.fxa.dialog.DevOAuthDialog;
import org.mozilla.accounts.fxa.IFxACallbacks;
import org.mozilla.accounts.fxa.tasks.dev.DevDestroyOAuthTask;
import org.mozilla.accounts.fxa.tasks.dev.DevRetrieveProfileTask;
import org.mozilla.accounts.fxa.tasks.dev.DevSetDisplayNameTask;
import org.mozilla.accounts.fxa.tasks.dev.DevVerifyOAuthTask;

public class MainApp extends Activity implements IFxACallbacks {
    private static final String LOG_TAG = LoggerUtil.makeLogTag(MainApp.class);

    // These secrets are provisioned from the FxA dashboard
    private static final String FXA_APP_KEY = "86cd25bed2c63936";

    // And finally the callback endpoint on our web application
    // Example server endpoint code is available under the `sample_endpoint` subdirectory.
    public static final String FXA_APP_CALLBACK = "http://leaderboard-dev.jaredkerim.com/";

    static String BEARER_TOKEN = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String app_name = getResources().getString(R.string.app_name);
        FxAGlobals fxa = new FxAGlobals();
        fxa.startIntentListening((Context)this, (IFxACallbacks) this, app_name);

        setContentView(R.layout.appmainexample);
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
        String[] scopes = new String[]{"profile:email", "profile:display_name", "profile:display_name:write"};
        new DevOAuthDialog(this,
                FXA_APP_CALLBACK,
                scopes,
                FXA_APP_KEY).show();
    }

    // FxA callbacks here
    @Override
    public void processReceiveBearerToken(String bearerToken) {
        BEARER_TOKEN = bearerToken;
        DevRetrieveProfileTask task = new DevRetrieveProfileTask(getApplicationContext());
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void failCallback(String intent_name) {
        // TODO: handle callback failures here
        Log.i(LOG_TAG, "A callback failed: ["+intent_name+"]");
    }

    @Override
    public void processProfileRead(JSONObject jsonBlob) {
        Log.i(LOG_TAG, "Read a profile: " + jsonBlob.toString());

        // Fire off the verify OAuth task
        DevVerifyOAuthTask task = new DevVerifyOAuthTask(getApplicationContext());
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void processDisplayNameWrite() {
        Log.i(LOG_TAG, "Display name was updated!");

        DevDestroyOAuthTask task = new DevDestroyOAuthTask(getApplicationContext());
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void processOauthDestroy() {
        Log.i(LOG_TAG, "OAuth destruction of bearer token succeeded!");
    }

    @Override
    public void processOauthVerify() {
        Log.i(LOG_TAG, "OAuth verification success!");
        DevSetDisplayNameTask task = new DevSetDisplayNameTask(getApplicationContext());
        task.execute(BEARER_TOKEN, "FxA_testing");
    }
}