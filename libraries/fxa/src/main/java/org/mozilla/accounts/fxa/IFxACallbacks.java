/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa;

import android.content.Intent;

import org.json.JSONObject;

public interface IFxACallbacks {
    void processReceiveBearerToken(String bearerToken);
    void processRawResponse(JSONObject authJSON);

    void failCallback(String profileRead);

    void processProfileRead(JSONObject jsonBlob);

    void processDisplayNameWrite();

    void processOauthDestroy();

    void processOauthVerify();
}
