package org.mozilla.firefoxaccounts;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieSyncManager;

import org.mozilla.accounts.fxa.DialogListener;
import org.mozilla.accounts.fxa.FxAOAuthDialog;

public class AppMainExample extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appmainexample);
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