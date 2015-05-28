package org.mozilla.accounts.fxa;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

// open source code from
// http://www.josecgomez.com/2010/04/30/android-accessing-restfull-web-services-using-json/

public class WebService{

    private static final String TAG = LOGGING.makeLogTag(WebService.class);

    DefaultHttpClient httpClient;
    HttpContext localContext;
    private String ret;

    HttpResponse response = null;
    HttpPost httpPost = null;
    String webServiceUrl;

    public WebService(){
        HttpParams myParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(myParams, 20000);
        HttpConnectionParams.setSoTimeout(myParams, 20000);
        httpClient = new DefaultHttpClient(myParams);
        localContext = new BasicHttpContext();
    }
    public void setWebServiceUrl(String serviceName){
        webServiceUrl = serviceName;
    }
    
    //Use this method to do a HttpPost\WebInvoke on a Web Service
    public String webInvoke(String methodName, HashMap<String, String> params) {

        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, String> param : params.entrySet()){
            try {
                jsonObject.put(param.getKey(), param.getValue());
            }
            catch (JSONException e) {
                Log.e(TAG, "JSONException : "+e);
            }
        }
        return webInvoke(methodName,  jsonObject.toString(), "application/json");
    }

    private String webInvoke(String methodName, String data, String contentType) {
        ret = null;

        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

        httpPost = new HttpPost(webServiceUrl + methodName);
        response = null;

        StringEntity tmp = null;        

        //httpPost.setHeader("User-Agent", "SET YOUR USER AGENT STRING HERE");
        httpPost.setHeader("Accept",
"text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");

        if (contentType != null) {
            httpPost.setHeader("Content-Type", contentType);
        } else {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        }

        try {
            tmp = new StringEntity(data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "HttpUtils : UnsupportedEncodingException : "+e);
        }

        httpPost.setEntity(tmp);

        Log.d(TAG, webServiceUrl + "?" + data);

        try {
            response = httpClient.execute(httpPost,localContext);

            if (response != null) {
                ret = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            Log.e(TAG, "HttpUtils: " + e);
        }

        return ret;
    }


}
