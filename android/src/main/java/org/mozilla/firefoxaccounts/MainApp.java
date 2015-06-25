package org.mozilla.firefoxaccounts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONObject;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.accounts.fxa.IFxaCallback;

import org.mozilla.accounts.fxa.FxAGlobals;
import org.mozilla.accounts.fxa.dialog.DevOAuthDialog;


// These tasks are hardcoded to point to our dev servers.
// You probably want to look at org.mozilla.accounts.fxa.tasks
// for the regular tasks that can be repointed to any FxA server.
import org.mozilla.accounts.fxa.tasks.dev.DevDestroyOAuthTask;
import org.mozilla.accounts.fxa.tasks.dev.DevRetrieveProfileTask;
import org.mozilla.accounts.fxa.tasks.dev.DevSetDisplayNameTask;
import org.mozilla.accounts.fxa.tasks.dev.DevVerifyOAuthTask;

public class MainApp
        extends Activity
        implements IFxaCallback {
    private static final String LOG_TAG = LoggerUtil.makeLogTag(MainApp.class);

    // These secrets are provisioned from the FxA dashboard
    private final String FXA_APP_KEY = "d0f6d2ed3c5fcc3b";

    // And finally the callback endpoint on our web application
    // Example server endpoint code is available under the `sample_endpoint` subdirectory.
    public final String FXA_APP_CALLBACK = "http://ec2-52-1-93-147.compute-1.amazonaws.com/fxa/callback";
    static String BEARER_TOKEN = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String app_name = getResources().getString(R.string.app_name);
        FxAGlobals.initFxaLogin(this, app_name);

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


    /*
     * These are callbacks that the FxA library will callback on when network activity is complete.
     */
    @Override
    public void acceptFxaBearerToken(String token) {
        //Note that we have to save the token right away
        BEARER_TOKEN = token;

        DevRetrieveProfileTask task = new DevRetrieveProfileTask(getApplicationContext());
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void acceptProfile(JSONObject profileJson) {
        // Fire off the verify OAuth task
        DevVerifyOAuthTask task = new DevVerifyOAuthTask(getApplicationContext());
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void profileReadFailure() {
        // do nothing for the demo.
    }

    @Override
    public void acceptOAuthVerified() {
        Log.i(LOG_TAG, "OAuth verification success!");
        DevSetDisplayNameTask task = new DevSetDisplayNameTask(getApplicationContext());
        task.execute(BEARER_TOKEN, "FxA_testing");
    }

    @Override
    public void oauthVerificationFailure() {
        Log.i(LOG_TAG, "OAuth verification failure!");
    }

    @Override
    public void acceptOAuthDestroy() {
        Log.i(LOG_TAG, "OAuth destruction of bearer token succeeded!");
    }

    @Override
    public void oauthDestroyFailure() {
        Log.i(LOG_TAG, "OAuth destruction of bearer token failed");
    }

    @Override
    public void acceptDisplayNameWrite() {
        DevDestroyOAuthTask task = new DevDestroyOAuthTask(getApplicationContext());
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void acceptDisplayNameWriteFailure() {
        Log.i(LOG_TAG, "Display name was update failed!");
    }
}