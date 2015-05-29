package org.mozilla.accounts.fxa;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;

import java.util.HashMap;

public class DialogListener {
    private static final String TAG = LoggerUtil.makeLogTag(DialogListener.class);
    private final String mAppSecret;
    private final String mAppKey;
    private final String mOauthBaseUrl;
    WebService webService = new WebService();

    public DialogListener(String oauthBaseUrl, String appKey, String appSecret) {
        mAppKey = appKey;
        mAppSecret = appSecret;
        mOauthBaseUrl = oauthBaseUrl;
    }

    /**
     * Called when a dialog completes.
     * Executed by the thread that initiated the dialog.
     * @param values
     *            Key-value string pairs extracted from the response.
     */
    @SuppressWarnings("unused")
    public void onComplete(Bundle values) {
        Log.i(TAG, "onComplete->" + values);

        // ensure any cookies set by the dialog are saved
        CookieSyncManager.getInstance().sync();

        try {
            webService.setWebServiceUrl(mOauthBaseUrl);
            // Call FxA again to get the access token
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("client_id", mAppKey);
            params.put("client_secret", mAppSecret);
            params.put("code", values.getString("code"));
            Log.i(TAG, "values: " + values.keySet());
            Log.i(TAG, "params->" + params);
            Log.i(TAG, "Invoking code verification");
            String tokenResponse = webService.webInvoke(FxAOAuthDialog.FXA_APP_TOKEN_URL, params);
            Log.i(TAG, "tokenResponse->" + tokenResponse);

            // TODO: add a hook here so that people can add their own behavior
            // on login
        } catch (Exception ex1) {
            Log.w(TAG, ex1.toString());
        }
    }

}
