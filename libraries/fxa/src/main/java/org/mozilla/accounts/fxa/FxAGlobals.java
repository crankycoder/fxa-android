/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa;

import android.content.Context;

public class FxAGlobals {

    public static String appVersionName;
    public static int appVersionCode;
    public static String appName;

    public static void initFxaLogin(Context ctx, String app_name) {
        // Clobber the FxAGlobals so that the user-agent for the FxA client will be sensible
        FxAGlobals.appVersionName = BuildConfig.VERSION_NAME;
        FxAGlobals.appVersionCode = BuildConfig.VERSION_CODE;
        FxAGlobals.appName = app_name;
    }
}

