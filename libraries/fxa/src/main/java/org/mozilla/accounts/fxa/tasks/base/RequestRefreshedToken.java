package org.mozilla.accounts.fxa.tasks.base;

/**
 * Created by victorng on 2015-12-31.
 *
 *
 * This class jsut calls GET on a URL, passes in a refresh token, an existing access token
 * and the server processes
 *
 */
public class RequestRefreshedToken {

    // Pass in an access token and a refresh token to obtain a new access token
    // This method will trigger an asynchronous task which will run and eventually invoke a callback
    // site to provide a new access token
    //
    // Most applications should use a refreshed access token on application startup.
    // This will minimize the lifetime of any access token.
    public String refreshAccessToken(String access_token, String refresh_token) {
        // TODO
        return null;
    };

}
