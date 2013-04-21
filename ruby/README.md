# DrEdit for Ruby

A walkthrough and more details are available on
[Google Drive SDK docs](https://developers.google.com/drive/examples/ruby).

## Setup Instructions

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
    1. List the URI of your application's URL in the **Authorized Request URIs** and **JavaScript Origins** fields.
    1. Click **Create Client ID**.
1. Register DrEdit as described in [Enable the Drive SDK](https://developers.google.com/drive/enable-sdk) using the following values:
    1. Set the default MIME types `text/plain` and `text/html`, and the default extensions `txt` and `html`.
    1. Ensure that the set of redirect URIs includes the URI of your application. The same URL must be provided for the `Open URL` and `Create URL` fields.
    1. Add the `Google API Scopes` of `https://www.googleapis.com/auth/userinfo.email`, `https://www.googleapis.com/auth/userinfo.profile` and `https://www.googleapis.com/auth/drive.install`.
    1. For icons, use the example icons  provided in the `chromewebstore` directory.
1. Contents is on the `ruby` directory.
1. Copy `client_secrets.json.example` to `client_secrets.json`, and edit it to replace the `client_id`, `client_secret`, and `redirect_uris` with the values from the [Google APIs Console](https://code.google.com/apis/console/) under the *API Access* tab for the project. **Note:** Make sure you use a valid **Client ID for web applications**.  Do *not* use the **Client ID for Drive SDK**, which is reserved for future feature development.
1. Run `bundle install` to ensure the requisite gems are installed.
1. Run `bundle exec ruby main.rb` to start the app. Add `-p <port>` to override the default port if necessary.
1. Test the application.
1. Continue reading to find out how DrEdit is constructed, and how to modify it to work for your own application's needs.