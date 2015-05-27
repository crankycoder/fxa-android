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


package com.wareninja.android.opensource.oauth2login.common;


public class AppContext {
	// FxA App params
	// TODO: these need to be loaded from a Pref
	// As it is, they're tied to a dev account
	public static final String FXA_APP_KEY = "d0f6d2ed3c5fcc3b";
	public static final String FXA_APP_SECRET = "3015f44423df9a5f08d0b5cd43e0cbb6f82c56e37f09a3909db293e17a9e64af";
	public static final String FXA_APP_CALLBACK_OAUTHCALLBACK = "http://ec2-52-1-93-147.compute-1.amazonaws.com/fxa/callback";

	public static final String FXA_APP_OAUTH_BASEURL = "https://oauth-stable.dev.lcip.org/v1";
	public static final String FXA_APP_TOKEN_URL = "/token";
}
