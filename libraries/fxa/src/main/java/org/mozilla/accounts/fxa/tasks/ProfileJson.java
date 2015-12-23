/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa.tasks;

import org.json.JSONObject;

public class ProfileJson {

    private final JSONObject profileObj;

    public ProfileJson(JSONObject jobj) {
        profileObj = jobj;
    }

    public String getUID() { return profileObj.optString("uid"); }

    public String getEmail() {
        return profileObj.optString("email");
    }

    public String getDisplayName() {
        return profileObj.optString("displayName");
    }

    public String toString() {
        if (profileObj != null) {
            return profileObj.toString();
        }
        else {
            return "{}";
        }
    }
}
