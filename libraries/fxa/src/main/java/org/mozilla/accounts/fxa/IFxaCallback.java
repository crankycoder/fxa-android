/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa;

import org.json.JSONObject;

public interface IFxaCallback {
    void acceptFxaBearerToken(String token);

    void acceptDisplayNameWrite();

    void acceptDisplayNameWriteFailure();

    void acceptOAuthDestroy();

    void oauthDestroyFailure();

    void acceptOAuthVerified();

    void oauthVerificationFailure();

    void acceptProfile(JSONObject profileJson);

    void profileReadFailure();
}
