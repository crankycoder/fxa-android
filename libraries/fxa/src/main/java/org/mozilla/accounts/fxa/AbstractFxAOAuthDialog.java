package org.mozilla.accounts.fxa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class AbstractFxAOAuthDialog extends Dialog {
    private static final String LOG_TAG = LoggerUtil.makeLogTag(AbstractFxAOAuthDialog.class);

    static final int BG_COLOR = Color.LTGRAY;//0xFF6D84B4;
    static final float[] DIMENSIONS_LANDSCAPE = {460, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    static final FrameLayout.LayoutParams FILL =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                         ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;

    private final String mAppCallback;

    private String mUrl;


    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;

    public AbstractFxAOAuthDialog(Context context,
                                  String signinUrl,
                                  String appCallback,
                                  String[] scopes,
                                  String AppKey) {
        super(context);
        mAppCallback = appCallback;

        String scopeText = TextUtils.join(" ", scopes);
        mUrl = signinUrl
                + "?client_id=" + AppKey
                + "&state=99" // I don't care about state
                + "&scope="+scopeText
                + "&redirect_uri=" + mAppCallback;
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
        final float scale = getContext().getResources().getDisplayMetrics().density;

        int orientation = getContext().getResources().getConfiguration().orientation;

        float[] dimensions;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dimensions = DIMENSIONS_LANDSCAPE;
        } else {
            dimensions = DIMENSIONS_PORTRAIT;
        }
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
        mWebView.setWebViewClient(new AbstractFxAOAuthDialog.OAuthWebViewClient());
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

        Log.i(LOG_TAG, "mUrl = ["+mUrl+"]");
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
            AbstractFxAOAuthDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(LOG_TAG, "onPageStarted->Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        	Log.d(LOG_TAG, "onPageFinished->Webview URL: " + url);
        	super.onPageFinished(view, url);
        	
            String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
            }
          
            try {
	            if (mSpinner!=null && mSpinner.isShowing())
	            	mSpinner.dismiss();
            }
            catch (Exception ex) {
            	Log.w(LOG_TAG, "wtf exception onPageFinished! " + ex.toString());
            }
        }
    }

    private class ContentViewer {

        private final AbstractFxAOAuthDialog myCallback;

        ContentViewer(AbstractFxAOAuthDialog callback) {
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
