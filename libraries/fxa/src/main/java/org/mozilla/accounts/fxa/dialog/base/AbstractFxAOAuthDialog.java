package org.mozilla.accounts.fxa.dialog.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import org.mozilla.accounts.fxa.Intents;
import org.mozilla.accounts.fxa.LoggerUtil;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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
        mUrl = signinUrl + "/v1/authorization?action=signin"
                + "&client_id=" + AppKey
                + "&state=99" // Someone else can implement this state thing
                + "&scope="+scopeText
                + "&access_type=offline"   // This is for refresh tokens
                + "&redirect_uri=" + mAppCallback;

        Log.i(LOG_TAG, mUrl);
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
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            InputSource source = new InputSource(new StringReader(html));

            String jsonBlob = "";
            try {
                jsonBlob = (String) xpath.evaluate("//pre", source, XPathConstants.STRING);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error extracting <pre> tag: [" + e.toString()+ "] Content: ["+html+"]");
                return;
            }

            Intent fxaOauthIntent = new Intent(Intents.RECEIVE_BEARER_TOKEN);
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
