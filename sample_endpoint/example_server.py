#!/usr/bin/env python

import falcon
import json
import requests

CLIENT_ID = "52ba0364d1629ade"
SECRET = "c65e2e22415d9ed83a777be498fb6f6dcb3d4378785a19741f531d13347eb20a"


def crossdomain(req, resp):
    resp.set_header('Access-Control-Allow-Origin', '*')


class MainResource(object):
    def on_get(self, req, resp):
        """Handles GET requests"""

        # Note that these secrets are provisioned from the FxA dashboard.
        # The FxA login is split between the handset and the server
        # side.
        # This code expects that a new refresh token is submitted
        # each time.
        payload = {"client_id": CLIENT_ID,
                   "client_secret": SECRET}


        # Copy out all values from the request
        for k, v in req.params.items():
            payload[k] = v

        print "JSON Payload being posted: " + str(payload)
        fxa_resp = requests.post("https://oauth-stable.dev.lcip.org/v1/token",
                                 data=json.dumps(payload))

        resp.content_type = "application/json"
        print "Response JSON: " + str(fxa_resp.text)
        resp.body = fxa_resp.text
        resp.status = falcon.HTTP_200


class RefreshTokenResource(object):
    def on_post(self, req, resp):
        """
        Use the refresh token to generate a new access token
        """
        # Note that these secrets are provisioned from the FxA dashboard.
        # The FxA login is split between the handset and the server
        # side.
        # This code expects that a new refresh token is submitted
        # each time.

        payload = {"client_id": CLIENT_ID, "client_secret": SECRET}
        payload["grant_type"] = "refresh_token"

        auth_header = req.get_header('Authorization')
        if auth_header:
            auth_header = auth_header.strip()

            if not auth_header or 'Bearer:' not in auth_header:
                err_blob = {'error': 'Invalid or missing bearer token'}
                resp.body = json.dumps(err_blob)
                resp.status = falcon.HTTP_405
                return

            access_token = auth_header.split("Bearer:")[-1].strip()
            fxa_resp = requests.post("https://oauth-stable.dev.lcip.org/v1/verify",
                    data=json.dumps({"token": access_token}))

            if fxa_resp.status_code != 200:
                print "Invalid access token. Verify response: " + str(fxa_resp.status)
                return
            else:
                print "Existing access token is valid!"
        else:
            print "No auth header found!" 

        # Copy out all values from the request
        body = req.stream.read()
        print "POST data received: " + body
        jobj = json.loads(body)
        for k, v in jobj.items():
            payload[k] = v

        print "JSON Payload being posted: " + str(payload)
        fxa_resp = requests.post("https://oauth-stable.dev.lcip.org/v1/token",
                                 data=json.dumps(payload))

        resp.content_type = "application/json"
        print "Refresh Response JSON: " + str(fxa_resp.text)
        resp.status = falcon.HTTP_200
        resp.body = fxa_resp.text


app = falcon.API(after=[crossdomain])
main = MainResource()
refresh = RefreshTokenResource()

app.add_route('/fxa/callback', main)
app.add_route('/fxa/refresh_access_token', refresh)
