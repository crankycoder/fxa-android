/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa.tasks;

import android.content.Context;

import org.mozilla.accounts.fxa.tasks.base.AbstractVerifyOAuthTask;

public class VerifyOAuthTask extends AbstractVerifyOAuthTask {

    private final String oauth2_endpoint;

    public VerifyOAuthTask(Context ctx, String oauth2Endpoint) {
        super(ctx);
        oauth2_endpoint = oauth2Endpoint;
    }

    @Override
    public String getOauth2Endpoint() {
        return oauth2_endpoint;
    }
}
