# DrEdit for Java

A walkthrough and more details are available on
[Google Drive SDK docs](https://developers.google.com/drive/examples/java).

## Setup Instructions

1. [Create a new Google App Engine application](https://appengine.google.com/)
1.  Clone DrEdit's git repo and init submodules:

        git clone git@github.com:googledrive/dredit.git && cd dredit
        git submodule init
        git submodule update --recursive
1. Create an API project in the [Google APIs Console](https://code.google.com/apis/console/).
1. Select the **Services** tab in your API Project, and enable the Drive API and Drive SDK.
1. Select the **API Access** tab in your API Project, click **Create an OAuth 2.0 client ID**.
1. In the **Branding Information** section, provide a name for your application (e.g. "DrEdit"), and click Next. Providing a product logo or a homepage URL is optional.
1. In the **Client ID Settings** section, do the following:
    1. Select **Web application** for the **Application type**
    1. Click the **more options** link next to the heading, **Your site or hostname**.
    1. List the URI of the Google App Engine application created in step 1 (i.e. `https://your_app_id.appspot.com`) in the **Authorized Request URIs** and **JavaScript Origins** fields.
    1. Click **Create Client ID**.
1. Register DrEdit as described in [Enable the Drive SDK](https://developers.google.com/drive/enable-sdk) using the following values:
    1. Set the default MIME types `text/plain` and `text/html`, and the default extensions `txt` and `html`.
    1. Ensure that the set of redirect URIs includes the URI of the Google App Engine application created in step 1 (i.e. `https://your_app_id.appspot.com`). The same URL must be provided for the `Open URL` and `Create URL` fields.
    1. Add the `Google API Scopes` of `https://www.googleapis.com/auth/userinfo.email`, `https://www.googleapis.com/auth/userinfo.profile` and `https://www.googleapis.com/auth/drive.install`.
    1. For icons, use the example icons  provided in the `chromewebstore` directory.
1. [Download and install the Google Plugin for Eclipse](https://developers.google.com//appengine/docs/java/tools/eclipse)
1. In Eclipse, [create a new Google App Engine project](https://developers.google.com//appengine/docs/java/tools/eclipse#Creating_a_Project), unchecking **Use Google Web Toolkit** and **Generate project sample code**.
1. Copy the contents of the `java/` directory to the root of the Google App Engine project.
1. Using the Google Plugin for Eclipse, [add the **Drive API v2** and the **OAuth API v2** to the project](https://developers.google.com//appengine/docs/java/tools/eclipse).
1. Edit `war/WEB-INF/client_secrets.json` to replace the `client_id`, `client_secret`, and `redirect_uris` with the values from the [Google APIs Console](https://code.google.com/apis/console/) under the **API Access** tab for the project.
1. Edit `war/WEB-INF/appengine-web.xml` and replace the value for the `application` setting with the identifier of the new Google App Engine application created in step 1.
1. Edit `Scripts\app.js` and replace `YOUR_APP_ID` with the value of the `CLIENT_ID` from the [Google APIs Console](https://code.google.com/apis/console/) under the **API Access** tab for the project.
1. Click the **App Engine deploy** button in Eclipse, following the instructions described in the [Google App Engine documentation](/appengine/docs/java/tools/eclipse#Uploading_to_Google_App_Engine) to upload the application to Google App Engine.
1. Test the application.
1. Continue reading to find out how DrEdit is constructed, and how to modify it to work for your own application's needs.

