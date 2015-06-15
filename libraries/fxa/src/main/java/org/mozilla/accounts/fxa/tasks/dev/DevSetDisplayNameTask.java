/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa.tasks.dev;

import android.content.Context;

import org.mozilla.accounts.fxa.tasks.SetDisplayNameTask;

public class DevSetDisplayNameTask extends SetDisplayNameTask {

    public DevSetDisplayNameTask(Context ctx) {
        super(ctx, DevConstants.STABLE_DEV_PROFILE_SERVER);
    }
}
