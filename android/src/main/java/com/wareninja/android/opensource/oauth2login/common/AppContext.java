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

	public static final boolean DEBUG = LOGGING.DEBUG;// enable/disable logging

	public static final String WARENINJAAPPS_MARKET_URL = "market://search?q=wareninja";
	public static final String AMAZON_WARENINJAAPPS_MARKET_URL = 
			"http://www.amazon.com/gp/mas/dl/android?p=com.wareninja.android.loco&showAll=1";
	
	public static final String APPWEBSITE_URL = "http://www.WareNinja.net";
	
	public static final String GRAPH_BASE_URL = "http://graph.facebook.com/";
	public static final String GRAPH_BASE_URL_SSL = "https://graph.facebook.com/";
	
	// REGISTER to these bcast identifiers and you will get login response together with its payload as json (INTENT_EXTRA_...)!!!
	public static final String INTENT_EXTRA_USERLOGIN_FSQ = "USERLOGIN_FSQ";
    public static final String BCAST_USERLOGIN_FSQ = "com.wareninja.android.opensource.oauth2login.BCAST_USERLOGIN_FSQ";
    public static final String INTENT_EXTRA_USERLOGIN_FACEBOOK = "USERLOGIN_FACEBOOK";
    public static final String BCAST_USERLOGIN_FACEBOOK = "com.wareninja.android.opensource.oauth2login.BCAST_USERLOGIN_FACEBOOK";
    public static final String INTENT_EXTRA_USERLOGIN_GOWALLA = "USERLOGIN_GOWALLA";
    public static final String BCAST_USERLOGIN_GOWALLA = "com.wareninja.android.opensource.oauth2login.BCAST_USERLOGIN_GOWALLA";
    public static final String INTENT_EXTRA_USERLOGIN_TWILIO = "USERLOGIN_TWILIO";
    public static final String BCAST_USERLOGIN_TWILIO = "com.wareninja.android.opensource.oauth2login.BCAST_USERLOGIN_TWILIO";
    
    public static final String INTENT_EXTRA_USERLOGOUT_FSQ = "USERLOGOUT_FSQ";
    public static final String BCAST_USERLOGOUT_FSQ = "com.wareninja.android.opensource.oauth2login.BCAST_USERLOGOUT_FSQ";
    public static final String INTENT_EXTRA_USERLOGOUT_FACEBOOK = "USERLOGOUT_FACEBOOK";
    public static final String BCAST_USERLOGOUT_FACEBOOK = "com.wareninja.android.opensource.oauth2login.BCAST_USERLOGOUT_FACEBOOK";
    public static final String INTENT_EXTRA_USERLOGOUT_GOWALLA = "USERLOGOUT_GOWALLA";
    public static final String BCAST_USERLOGOUT_GOWALLA = "com.wareninja.android.opensource.oauth2login.BCAST_USERLOGOUT_GOWALLA";
	
	//public static final String APP_CACHEDIR = ".WareNinja_OpenSource_appCache";
	
	// -->> REPLACE THESE VALUES WITH YOUR OWN APP!!!! <<--
	// NOTE: below values are using Test App for WareNinja.net ONLY!
	
	// Gowalla App params - NOTE: GOWALLA is DEAD! 
	public static final String GOWALLA_APP_KEY = "<YOURAPP_atGOWALLA_API_KEY>";  
	public static final String GOWALLA_APP_SECRET = "<YOURAPP_atGOWALLA_API_SECRET>";
	public static final String GOWALLA_APP_CALLBACK_OAUTHCALLBACK = "http://WareNinja_OpenSource";// YOURAPP_REDIRECT_URI
	public static final String GOWALLA_APP_REDIRECT_SIGNIN = "https://gowalla.com/signin";
	public static final String GOWALLA_APP_OAUTH_BASEURL = "https://api.gowalla.com";
	public static final String GOWALLA_APP_TOKEN_URL = "/api/oauth/token";

	// Foursquare App params
    // TODO: these need to be loaded from a Pref
    // As it is, they're tied to a throwaway fake foursquare account
	public static final String FSQ_APP_KEY = "DD1VQFVZ35YXLTT2LJKAPWDKPSOK1DFRHSRFXEGFTDKWLIOA";
	public static final String FSQ_APP_SECRET = "OVXUEZGBEMH4IU2QLBN0SR1UQJJ3EHN34HTHPOIT31XTIN3B";
	public static final String FSQ_APP_CALLBACK_OAUTHCALLBACK = "https://www.foursquare.com";
	public static final String FSQ_APP_REDIRECT_SIGNIN = "https://m.foursquare.com/mobile/login";
	public static final String FSQ_APP_OAUTH_BASEURL = "https://foursquare.com";
	public static final String FSQ_APP_OAUTH_URL = "/oauth2/authenticate";
	public static final String FSQ_APP_TOKEN_URL = "/oauth2/access_token";
	
	// Facebook App Params
	public static final int FACEBOOK_SSO_ACTIVITY_CODE = 8844;
	public static final String FB_APP_ID = "<YOURAPP_atFACEBOOK_APP_ID>";  
	public static final String[] FB_PERMISSIONS = new String[] {
		// NOTE: remember to extend these permissions as per your need!!!!
		"publish_stream", "read_stream"
		}; 
	public static final String FB_APP_CALLBACK_OAUTHCALLBACK = "fbconnect://success";// YOURAPP_REDIRECT_URI
	public static final String FB_APP_REDIRECT_SIGNIN = "https://m.facebook.com";
	public static final String FB_APP_OAUTH_BASEURL = "https://m.facebook.com";
	public static final String FB_APP_OAUTH_URL = "/dialog/oauth/";
	
	// Instagram App Params
	public static final String INSTAGRAM_APP_ID = "<YOURAPP_atINSTAGRAM_CLIENT_ID>";  
	public static final String[] INSTAGRAM_PERMISSIONS = new String[] {
	/* see: http://instagram.com/developer/authentication/#scope
	 * basic - to read any and all data related to a user (e.g. following/followed-by lists, photos, etc.) (granted by default)
	 * comments - to create or delete comments on a user's behalf
	 * relationships - to follow and unfollow users on a user's behalf
	 * likes - to like and unlike items on a user's behalf
	 */
		// NOTE: remember to extend these permissions as per your need!!!!  
		"likes", "comments"
		}; 
	public static final String INSTAGRAM_APP_CALLBACK_OAUTHCALLBACK = "http://WareNinja_OpenSource";// YOURAPP_REDIRECT_URI; must be as defined in INSTAGRAM_APP definition!
	public static final String INSTAGRAM_APP_OAUTH_BASEURL = "https://instagram.com";
	public static final String INSTAGRAM_APP_OAUTH_URL = "/oauth/authorize/";
	
	
	// -> Twilio Connect params
	public static final String TWILIO_APP_ID = "<YOURAPP_atTWILIO_APP_SID>";// Connect App SID
	public static final String TWILIO_APP_CALLBACK_OAUTHCALLBACK = "http://WareNinja_OpenSource";// YOURAPP_REDIRECT_URI
	public static final String TWILIO_APP_REDIRECT_SIGNIN = "https://www.twilio.com/authorize/";
	public static final String TWILIO_APP_OAUTH_BASEURL = "https://www.twilio.com/authorize/";
		
	
	public static final String FACEBOOK_USER_ME = "_FACEBOOK_USER_ME";
	public static final String FOURSQUARE_USER_ME = "_FOURSQUARE_USER_ME";
	public static final String GOWALLA_USER_ME = "_GOWALLA_USER_ME";
	public static final String TWILIO_USER_ME = "_TWILIO_USER_ME";

	public enum COMMUNITY {
		FACEBOOK, FOURSQUARE, GOWALLA
		, TWILIO
		, INSTAGRAM
	}

}
