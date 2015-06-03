/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa.dialog;

import android.content.Context;

import org.mozilla.accounts.fxa.dialog.base.AbstractFxAOAuthDialog;

public class DevOAuthDialog extends AbstractFxAOAuthDialog {
    // This is the public facing URL for FxA login
    public static final String FXA_SIGNIN_URL = "https://stable.dev.lcip.org/";

    public DevOAuthDialog(Context context, String appCallback, String[] scopes, String AppKey) {
        super(context, FXA_SIGNIN_URL, appCallback, scopes, AppKey);
    }
}
