/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa.tasks;

import android.content.Context;

import org.mozilla.accounts.fxa.tasks.base.AbstractSetDisplayNameTask;

public class DevSetDisplayNameTask extends AbstractSetDisplayNameTask {

    public DevSetDisplayNameTask(Context ctx) {
        super(ctx);
    }

    @Override
    protected String getFxaProfileEndpoint() {
        return "https://stable.dev.lcip.org/profile/v1";
    }
}
