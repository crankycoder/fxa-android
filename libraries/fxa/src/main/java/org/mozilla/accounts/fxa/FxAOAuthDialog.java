package org.mozilla.accounts.fxa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FxAOAuthDialog extends Dialog {

    // FxA App params
    // TODO: these need to be loaded from a Pref
    // As it is, they're tied to a dev account
    public static final String FXA_APP_KEY = "d0f6d2ed3c5fcc3b";
    public static final String FXA_APP_SECRET = "3015f44423df9a5f08d0b5cd43e0cbb6f82c56e37f09a3909db293e17a9e64af";
    public static final String FXA_APP_CALLBACK_OAUTHCALLBACK = "http://ec2-52-1-93-147.compute-1.amazonaws.com/fxa/callback";
    public static final String FXA_APP_OAUTH_BASEURL = "https://oauth-stable.dev.lcip.org/v1";
    public static final String FXA_APP_TOKEN_URL = "/token";


    private static final String TAG = LoggerUtil.makeLogTag(FxAOAuthDialog.class);

    static final int BG_COLOR = Color.LTGRAY;//0xFF6D84B4;
    static final float[] DIMENSIONS_LANDSCAPE = {460, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    static final FrameLayout.LayoutParams FILL =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                         ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;

    private String mUrl;

    @SuppressWarnings("unused")
    private DialogListener mListener;

    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;

    public FxAOAuthDialog(Context context, String url, DialogListener listener) {
        super(context);
        mUrl = url;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        setUpTitle();
        setUpWebView();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale = getContext().getResources().getDisplayMetrics().density;

        // TODO: Check orientation some other way than width < height
        float[] dimensions = display.getWidth() < display.getHeight() ?
        		DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
        addContentView(mContent, new FrameLayout.LayoutParams(
        		(int) (dimensions[0] * scale + 0.5f),
        		(int) (dimensions[1] * scale + 0.5f)));
    }

    private void setUpTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTitle = new TextView(getContext());
        mTitle.setText("Firefox Accounts");
        mTitle.setTextColor(Color.WHITE);
        mTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mTitle.setBackgroundColor(BG_COLOR);
        mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        mContent.addView(mTitle);
    }

    private void setUpWebView() {
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new FxAOAuthDialog.OAuthWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new ContentViewer(this), "HTMLOUT");
        mWebView.getSettings().setDomStorageEnabled(true);

        /* WebViewClient must be set BEFORE calling loadUrl! */
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
             /* This call inject JavaScript into the page which just finished loading. */
                mWebView.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });

        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(FILL);
        mContent.addView(mWebView);
    }


    /*
     This is the final callback
     */
    public void contentCallback(String html) {
        if (html.contains("access_token")) {
            int start = html.indexOf("<body>") + "<body>".length();
            int end = html.indexOf("</body>");
            String jsonBlob = html.substring(start, end);

            Intent fxaOauthIntent = new Intent(Intents.ORG_MOZILLA_ACCOUNTS_FXA_SIGNIN_TOKEN);
            fxaOauthIntent.putExtra("json", jsonBlob);
            dismiss();
            LocalBroadcastManager
                    .getInstance(getContext())
                    .sendBroadcast(fxaOauthIntent);
        }
    }

    private class OAuthWebViewClient extends WebViewClient {
    
        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            FxAOAuthDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted->Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        	Log.d(TAG, "onPageFinished->Webview URL: " + url);
        	super.onPageFinished(view, url);
        	
            String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
            }
          
            try {// to avoid crashing the app add try-catch block, avoid this stupid crash!
	            if (mSpinner!=null && mSpinner.isShowing())// by YG
	            	mSpinner.dismiss();
            }
            catch (Exception ex) {
            	Log.w(TAG, "wtf exception onPageFinished! " + ex.toString());
            }

            Log.i(TAG, "Redirect to URL: " + url);

        }   
        
    }

    private class ContentViewer {

        private final FxAOAuthDialog myCallback;

        ContentViewer(FxAOAuthDialog callback) {
            myCallback = callback;
        }
        @SuppressWarnings("unused")
        @android.webkit.JavascriptInterface
        public void showHTML(String html)
        {
            myCallback.contentCallback(html);
        }
    }
}
