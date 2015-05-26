/***
	Copyright (c) 2011-2012 WareNinja.com 
	http://www.WareNinja.com - https://github.com/WareNinja
	
	Author: yg@wareninja.com / twitter: @WareNinja

  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  
  >> Summary of the license:
  	You are allowed to re-use this code as you like, no kittens should be harmed though! 
 */


package com.wareninja.android.opensource.oauth2login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;

import com.wareninja.android.opensource.oauth2login.common.AppContext;
import com.wareninja.android.opensource.oauth2login.common.DialogListener;
import com.wareninja.android.opensource.oauth2login.common.LOGGING;
import com.wareninja.android.opensource.oauth2login.common.WebService;
import com.wareninja.android.opensource.oauth2login.firefox.FxAOAuthDialog;
import com.wareninja.android.opensource.oauth2login.foursquare.FsqOAuthDialog;

import java.util.HashMap;

public class AppMainExample extends Activity {
    
	protected static final String TAG = "AppMainExample";
	
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
    
    
    
    public void onClick_fsqLogin(View v) {

    	webService = new WebService();

    	String authRequestRedirect = AppContext.FSQ_APP_OAUTH_BASEURL+AppContext.FSQ_APP_OAUTH_URL
		        + "?client_id="+AppContext.FSQ_APP_KEY
		        + "&response_type=code"
				+ "&display=touch"
				+ "&redirect_uri=" + AppContext.FSQ_APP_CALLBACK_OAUTHCALLBACK;

		if (LOGGING.DEBUG) {
			Log.d(TAG, "authRequestRedirect->" + authRequestRedirect);
		}
		
		CookieSyncManager.createInstance(this);
		new FsqOAuthDialog(mContext, authRequestRedirect
				, new DialogListener() {
			public void onComplete(Bundle values) {
				if(LOGGING.DEBUG)Log.d(TAG, "onComplete->"+values);
				// https://YOUR_REGISTERED_REDIRECT_URI/?code=CODE
				// onComplete->Bundle[{state= , code=....}]
				   
				// ensure any cookies set by the dialog are saved
                CookieSyncManager.getInstance().sync();
				
				try{
					webService.setWebServiceUrl(AppContext.FSQ_APP_OAUTH_BASEURL);
					// Call Foursquare again to get the access token
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("client_id", AppContext.FSQ_APP_KEY);
					params.put("client_secret", AppContext.FSQ_APP_SECRET);
					params.put("grant_type", "authorization_code");
					params.put("redirect_uri", AppContext.FSQ_APP_CALLBACK_OAUTHCALLBACK);
					params.put("code", values.getString("code"));
					if(LOGGING.DEBUG)Log.d(TAG, "params->" + params);

					String tokenResponse = webService.webGet(AppContext.FSQ_APP_TOKEN_URL, params);
					if(LOGGING.DEBUG)Log.d(TAG, "tokenResponse->" + tokenResponse);
					
					broadcastLoginResult(AppContext.COMMUNITY.FOURSQUARE, tokenResponse);
					//JSONObject tokenJson = new JSONObject(tokenResponse);
					//if(LOGGING.DEBUG)Log.d(TAG, "tokenJson->" + tokenJson);

				}
				catch (Exception ex1){
					Log.w(TAG, ex1.toString());
				}
		    }
			public void onError(String e) {
				if(LOGGING.DEBUG)Log.d(TAG, "onError->"+e);
		    }
			public void onCancel() {
				if(LOGGING.DEBUG)Log.d(TAG, "onCancel()");
		    }
		}).show();
    }

    
	public void onClick_facebookLogin(View v) {
		webService = new WebService();

		String authRequestRedirect = AppContext.FXA_APP_OAUTH_BASEURL+AppContext.FXA_APP_OAUTH_URL
				+ "?client_id="+AppContext.FXA_APP_KEY
				+ "&response_type=code"
				+ "&display=touch"
				+ "&redirect_uri=" + AppContext.FXA_APP_CALLBACK_OAUTHCALLBACK;

		if (LOGGING.DEBUG) {
			Log.d(TAG, "authRequestRedirect->" + authRequestRedirect);
		}

		CookieSyncManager.createInstance(this);
		new FxAOAuthDialog(mContext, authRequestRedirect
				, new DialogListener() {
			public void onComplete(Bundle values) {
				if(LOGGING.DEBUG)Log.d(TAG, "onComplete->"+values);
				// https://YOUR_REGISTERED_REDIRECT_URI/?code=CODE
				// onComplete->Bundle[{state= , code=....}]

				// ensure any cookies set by the dialog are saved
				CookieSyncManager.getInstance().sync();

				try{
					webService.setWebServiceUrl(AppContext.FXA_APP_OAUTH_BASEURL);
					// Call Foursquare again to get the access token
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("client_id", AppContext.FXA_APP_KEY);
					params.put("client_secret", AppContext.FXA_APP_SECRET);
					params.put("grant_type", "authorization_code");
					params.put("redirect_uri", AppContext.FXA_APP_CALLBACK_OAUTHCALLBACK);
					params.put("code", values.getString("code"));
					if(LOGGING.DEBUG) {
						Log.d(TAG, "params->" + params);
					}

					String tokenResponse = webService.webGet(AppContext.FXA_APP_TOKEN_URL, params);
					if(LOGGING.DEBUG) {
						Log.d(TAG, "tokenResponse->" + tokenResponse);
					}
					broadcastLoginResult(AppContext.COMMUNITY.FXA, tokenResponse);
					//JSONObject tokenJson = new JSONObject(tokenResponse);
					//if(LOGGING.DEBUG)Log.d(TAG, "tokenJson->" + tokenJson);

				}
				catch (Exception ex1){
					Log.w(TAG, ex1.toString());
				}
			}
			public void onError(String e) {
				if(LOGGING.DEBUG)Log.d(TAG, "onError->"+e);
			}
			public void onCancel() {
				if(LOGGING.DEBUG)Log.d(TAG, "onCancel()");
			}
		}).show();	}

	
	private void broadcastLoginResult(AppContext.COMMUNITY community, String token) {
		
		String intentAction = "";
		try {
			
			if (AppContext.COMMUNITY.FOURSQUARE.equals(community)) {
				intentAction = AppContext.BCAST_USERLOGIN_FSQ;
			} else if (AppContext.COMMUNITY.FXA.equals(community)) {
				intentAction = AppContext.BCAST_USERLOGIN_FXA;
			} else {
				throw new RuntimeException("Unrecognized community!: ["+community+"]");
			}

			if(LOGGING.DEBUG) {
				Log.d(TAG, "sending Broadcast! "
								+ "|intentAction->" + intentAction
								+ "|token->" + token
				);
			}
			
			Intent mIntent = new Intent();
			mIntent.setAction(intentAction);
        	mIntent.putExtra("token", token);
        	this.sendBroadcast(mIntent);
		}
    	catch (Exception ex) {
    		Log.e(TAG, ex.toString(), ex);
    	}
    	
	}
	




	
	
}
