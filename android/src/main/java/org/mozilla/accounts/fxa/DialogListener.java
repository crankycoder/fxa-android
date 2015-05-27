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

package org.mozilla.accounts.fxa;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;

import java.util.HashMap;


public class DialogListener {
    private static final String TAG = LOGGING.makeLogTag(DialogListener.class);
    WebService webService = new WebService();

    /**
     * Called when a dialog completes.
     * Executed by the thread that initiated the dialog.
     * @param values
     *            Key-value string pairs extracted from the response.
     */
    public void onComplete(Bundle values) {
        Log.i(TAG, "onComplete->" + values);

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

}
