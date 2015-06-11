/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa;

import android.content.IntentFilter;

public class Intents {

    public static final String ORG_MOZILLA_ACCOUNTS_FXA_BEARER_TOKEN = "org.mozilla.accounts.fxa.token";

    public static final String PROFILE_UPDATE_FAILURE = "org.mozilla.accounts.fxa.profile:write.fail";
    public static final String PROFILE_UPDATE = "org.mozilla.accounts.fxa.profile:write";

    public static final String PROFILE_READ = "org.mozilla.accounts.fxa.profile:read";
    public static final String PROFILE_READ_FAILURE = "org.mozilla.accounts.fxa.profile:read.fail";

    public static final String OAUTH_VERIFY = "org.mozilla.accounts.fxa.oauth:verify";
    public static final String OAUTH_VERIFY_FAIL = "org.mozilla.accounts.fxa.oauth:verify.fail";

    public static final String OAUTH_DESTROY = "org.mozilla.accounts.fxa.oauth:destroy";
    public static final String OAUTH_DESTROY_FAIL = "org.mozilla.accounts.fxa.oauth:destroy.fail";


    public static void registerFxaIntents(IntentFilter intentFilter) {
        intentFilter.addAction(ORG_MOZILLA_ACCOUNTS_FXA_BEARER_TOKEN);

        intentFilter.addAction(PROFILE_READ);
        intentFilter.addAction(PROFILE_READ_FAILURE);

        intentFilter.addAction(OAUTH_VERIFY);
        intentFilter.addAction(OAUTH_VERIFY_FAIL);

        intentFilter.addAction(OAUTH_DESTROY);
        intentFilter.addAction(OAUTH_DESTROY_FAIL);

        intentFilter.addAction(PROFILE_UPDATE);
        intentFilter.addAction(PROFILE_UPDATE_FAILURE);
    }
}
