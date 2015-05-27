/**
 * Copyright (c) 2011-2012 WareNinja.com
 * http://www.WareNinja.com - https://github.com/WareNinja
 * <p/>
 * Author: yg@wareninja.com / twitter: @WareNinja
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * >> Summary of the license:
 * You are allowed to re-use this code as you like, no kittens should be harmed though!
 */


package com.wareninja.android.opensource.oauth2login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;

import com.wareninja.android.opensource.oauth2login.common.AppContext;
import com.wareninja.android.opensource.oauth2login.common.DialogListener;
import com.wareninja.android.opensource.oauth2login.common.WebService;
import com.wareninja.android.opensource.oauth2login.firefox.FxAOAuthDialog;

import java.util.HashMap;

public class AppMainExample extends Activity {

    protected static final String TAG = "FxA.AppMainExample";

    public Context mContext;
    public Activity mActivity;
    public WebService webService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mActivity = this;
        setContentView(R.layout.appmainexample);
    }


    public void onClick_fxaLogin(View v) {
        webService = new WebService();

        String authRequestRedirect = "https://stable.dev.lcip.org/oauth/signin"
                + "?client_id=" + AppContext.FXA_APP_KEY
                + "&state=99" // I don't care about state
                + "&scope=profile:email"
                + "&redirect_uri=" + AppContext.FXA_APP_CALLBACK_OAUTHCALLBACK;

        Log.d(TAG, "authRequestRedirect->" + authRequestRedirect);

        CookieSyncManager.createInstance(this);
        new FxAOAuthDialog(mContext, authRequestRedirect
                , new DialogListener() {
            public void onComplete(Bundle values) {
                Log.d(TAG, "onComplete->" + values);

                // ensure any cookies set by the dialog are saved
                CookieSyncManager.getInstance().sync();

                try {
                    webService.setWebServiceUrl(AppContext.FXA_APP_OAUTH_BASEURL);

                    // Call FxA again to get the access token
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("client_id", AppContext.FXA_APP_KEY);
                    params.put("client_secret", AppContext.FXA_APP_SECRET);
                    params.put("code", values.getString("code"));
                    Log.i(TAG, "values: " + values.keySet());
                    Log.i(TAG, "params->" + params);

                    Log.i(TAG, "Invoking code verification");
                    String tokenResponse = webService.webInvoke(AppContext.FXA_APP_TOKEN_URL, params);
                    Log.i(TAG, "tokenResponse->" + tokenResponse);

                    // TODO: add a hook here so that people can add their own behavior
                    // on login
                } catch (Exception ex1) {
                    Log.w(TAG, ex1.toString());
                }
            }

            public void onError(String e) {
                Log.d(TAG, "onError->" + e);
            }

            public void onCancel() {
                Log.d(TAG, "onCancel()");
            }
        }).show();
    }



}
