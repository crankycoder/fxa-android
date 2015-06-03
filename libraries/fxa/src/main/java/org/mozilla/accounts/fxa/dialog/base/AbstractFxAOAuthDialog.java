package org.mozilla.accounts.fxa.dialog.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;

public abstract class AbstractFxAOAuthDialog extends Dialog {
    private static final String LOG_TAG = LoggerUtil.makeLogTag(AbstractFxAOAuthDialog.class);
    private final String mAppCallback;
    private String mUrl;

    private WebView mWebView;
    private LinearLayout mContent;

    public AbstractFxAOAuthDialog(Context context,
                                  String signinUrl,
                                  String appCallback,
                                  String[] scopes,
                                  String AppKey) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar);
        // Wipe down all the DOM storage
        WebStorage.getInstance().deleteAllData();

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
        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        setUpWebView();
        setContentView(mContent);
    }

    private void setUpWebView() {
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
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

            Intent fxaOauthIntent = new Intent(Intents.ORG_MOZILLA_ACCOUNTS_FXA_BEARER_TOKEN);
            fxaOauthIntent.putExtra("json", jsonBlob);
            dismiss();
            LocalBroadcastManager
                    .getInstance(getContext())
                    .sendBroadcast(fxaOauthIntent);
        }
        // Wipe down all the DOM storage
        WebStorage.getInstance().deleteAllData();
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
