# DrEdit for Python

A walkthrough and more details are available on
[Google Drive SDK docs](https://developers.google.com/drive/examples/python).

## Setup Instructions

1. [Create a new Google App Engine application](https://appengine.google.com/)
1. Clone DrEdit's git repo and init submodules:

        git clone git@github.com:googledrive/dredit.git && cd dredit
        git submodule init
        git submodule update --recursive
        
1. Register DrEdit as described in [Enable the Drive SDK](https://developers.google.com/drive/enable-sdk) using the following values
    * Set the default MIME types `text/plain` and `text/html`, and the default extensions `txt` and `html`.
    * Ensure that the set of redirect URIs includes the URI of the Google App Engine application created in step 1 (i.e. `https://your_app_id.appspot.com`). The same URL must be provided for the `Open URL` and `Create URL` fields.
    * Add the `Google API Scopes` of `https://www.googleapis.com/auth/userinfo.email` and `https://www.googleapis.com/auth/userinfo.profile`.
    * For icons, use the example icons  provided in the `chromewebstore` directory.
1. Copy the contents of the `python/` directory to the root of the Google App Engine project
1. Create a session secret, which should be at least 64 bytes of random characters, for example with `python -c "import os; print os.urandom(64)" > session.secret`
1. Modify `client_secrets.json` to replace the `client_id`, `client_secret`, and `redirect_uris` with the values from the [Google APIs Console](https://code.google.com/apis/console/) under the *API Access* tab for the project. **Note:** Make sure you use a valid **Client ID for web applications**.  Do *not* use the **Client ID for Drive SDK**, which is reserved for future feature development.
1. Edit `app.yaml` and replace the value for the `application` setting with the identifier of the new Google App Engine application created in step 1.
1. Deploy the App to App Engine as described in the [Google App Engine documentation](https://developers.google.com/appengine/docs/python/tools/uploadinganapp#Python_Uploading_the_app).
1. Test the application.
1. Continue reading to find out how DrEdit is constructed, and how to modify it to work for your own application's needs.
