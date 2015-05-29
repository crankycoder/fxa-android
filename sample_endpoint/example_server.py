#!/usr/bin/env python

import falcon
import json
import requests
import sys


def crossdomain(req, resp):
    resp.set_header('Access-Control-Allow-Origin', '*')


class MainResource(object):
    def on_get(self, req, resp):
        """Handles GET requests"""

        // Note that these secrets are provisioned from the FxA dashboard.
        payload = {"client_id": "d0f6d2ed3c5fcc3b",
                   "client_secret": "3015f44423df9a5f08d0b5cd43e0cbb6f82c56e37f09a3909db293e17a9e64af",
                   "code": req.params['code']}

        fxa_resp = requests.post("https://oauth-stable.dev.lcip.org/v1/token",
                             data=json.dumps(payload))

        resp.content_type = "text/html"
        json_text = fxa_resp.text
        tmpl = """<html><body><h1>Logged in!</h1>
        <!--START_FXA_DATA
        %s
        END_FXA_DATA -->
        %s
        </body></html>"""
        resp.body = json_text # tmpl % (json_text, json_text)


app = falcon.API(after=[crossdomain])
main = MainResource()
app.add_route('/fxa/callback', main)
