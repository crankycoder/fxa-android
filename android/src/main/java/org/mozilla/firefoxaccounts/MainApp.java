package org.mozilla.firefoxaccounts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONObject;
import org.mozilla.accounts.fxa.FxAGlobals;
import org.mozilla.accounts.fxa.IFxACallbacks;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.mozilla.accounts.fxa.dialog.OAuthDialog;
import org.mozilla.accounts.fxa.tasks.DestroyOAuthTask;
import org.mozilla.accounts.fxa.tasks.RequestRefreshedToken;
import org.mozilla.accounts.fxa.tasks.RetrieveProfileTask;
import org.mozilla.accounts.fxa.tasks.SetDisplayNameTask;
import org.mozilla.accounts.fxa.tasks.VerifyOAuthTask;
import org.mozilla.accounts.fxa.tasks.FxAConstants;

import static org.mozilla.accounts.fxa.tasks.FxAConstants.STABLE_DEV_OAUTH2_SERVER;

public class MainApp extends Activity implements IFxACallbacks {
    private static final String LOG_TAG = LoggerUtil.makeLogTag(MainApp.class);

    // These secrets are provisioned from the FxA dashboard
    private static final String FXA_APP_KEY = "52ba0364d1629ade";

    // And finally the callback endpoint on our web application
    // Example server endpoint code is available under the `sample_endpoint` subdirectory.
    public static final String FXA_DEMO_CALLBACK = "http://ec2-52-1-93-147.compute-1.amazonaws.com/fxa/callback";
    public static final String FXA_APP_CALLBACK = FXA_DEMO_CALLBACK; // "http://leaderboard-dev.jaredkerim.com/";
    public static final String FXA_OAUTH_BASE = "https://oauth-stable.dev.lcip.org";

    static String BEARER_TOKEN = null;
    static String REFRESH_TOKEN = null;

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
        String[] scopes = new String[]{"profile:email",
                "profile:display_name",
                "profile:display_name:write"};
        new OAuthDialog(this,
                FXA_OAUTH_BASE,
                FXA_APP_CALLBACK,
                scopes,
                FXA_APP_KEY).show();
    }

    // FxA callbacks here
    @Override
    public void processReceiveBearerToken(String bearerToken) {
        BEARER_TOKEN = bearerToken;
        RetrieveProfileTask task = new RetrieveProfileTask(getApplicationContext(), FxAConstants.STABLE_DEV_PROFILE_SERVER);
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void processRawResponse(JSONObject authJSON) {

        // TODO: process the authJSON to extract the refresh_token
        REFRESH_TOKEN  = authJSON.optString("refresh_token", "");
        Log.i(LOG_TAG, "MainApp received refresh token: ["+REFRESH_TOKEN+"]");
        Log.i(LOG_TAG, "MainApp Received JSON response: " + authJSON.toString());
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
        VerifyOAuthTask task = new VerifyOAuthTask(getApplicationContext(), FxAConstants.STABLE_DEV_OAUTH2_SERVER);
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void processDisplayNameWrite() {
        Log.i(LOG_TAG, "Display name was updated!");

        DestroyOAuthTask task = new DestroyOAuthTask(getApplicationContext(), STABLE_DEV_OAUTH2_SERVER);
        task.execute(BEARER_TOKEN);
    }

    @Override
    public void processOauthDestroy() {
        Log.i(LOG_TAG, "OAuth destruction of bearer token succeeded!");
    }

    @Override
    public void processOauthVerify() {
        Log.i(LOG_TAG, "OAuth verification success!");
        RequestRefreshedToken task = new RequestRefreshedToken(getApplicationContext(),
                FxAConstants.STABLE_DEV_REFRESH_ENDPOINT);
        task.execute(BEARER_TOKEN, REFRESH_TOKEN);
    }

    @Override
    synchronized public void processRefreshToken(JSONObject jObj) {
        Log.i(LOG_TAG, "Refresh token success!");
        String new_access_token = jObj.optString("access_token");
        Log.i(LOG_TAG, "A new access token should be visible in this JSON blob: " + new_access_token);
        if (!TextUtils.isEmpty(new_access_token)) {
            BEARER_TOKEN = new_access_token;
            Log.i(LOG_TAG, "New access token is set! [" + new_access_token + "]");

            SetDisplayNameTask task = new SetDisplayNameTask(getApplicationContext(),
                    FxAConstants.STABLE_DEV_PROFILE_SERVER);
            task.execute(BEARER_TOKEN, "FxA_testing");
        } else {
            Log.i(LOG_TAG, "No refreshed access token was found");
        }
    }
}