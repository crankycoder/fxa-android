/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa.dialog;

import android.content.Context;

import org.mozilla.accounts.fxa.dialog.base.AbstractFxAOAuthDialog;

@SuppressWarnings("unused")
public class OAuthDialog extends AbstractFxAOAuthDialog {
    public OAuthDialog(Context context, String sign_in_url, String appCallback, String[] scopes, String AppKey) {
        super(context, sign_in_url, appCallback, scopes, AppKey);
    }

}
