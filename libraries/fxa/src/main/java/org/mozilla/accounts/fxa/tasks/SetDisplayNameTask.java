/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa.tasks;

import android.content.Context;

import org.mozilla.accounts.fxa.tasks.base.AbstractSetDisplayNameTask;


public class SetDisplayNameTask extends AbstractSetDisplayNameTask {

    private final String fxa_profile_endpoint;

    public SetDisplayNameTask(Context ctx, String profile_endpoint) {
        super(ctx);
        fxa_profile_endpoint = profile_endpoint;
    }

    // Profile endpoint
    @Override
    protected  String getFxaProfileEndpoint() {
        return fxa_profile_endpoint;
    }

}
