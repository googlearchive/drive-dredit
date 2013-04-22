# DrEdit for .NET

A walkthrough and more details are available on
[Google Drive SDK docs](https://developers.google.com/drive/examples/dotnet).

## Setup Instructions

1. [Download and install ASP.NET MVC 3](http://www.asp.net/mvc).
1. Configure IIS (or another web server) to publish your ASP.NET application to a publicly-accessible URI (e.g. `http://www.example.com`).
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
    1. List the hostname of the web server configured in step 2 in the **Authorized Request URIs** and **JavaScript Origins** fields.
    1. Click **Create Client ID**.
1. Register DrEdit as described in [Enable the Drive SDK](https://developers.google.com/drive/enable-sdk) using the following values:
    1. Set the default MIME types `text/plain` and `text/html`, and the default extensions `txt` and `html`.
    1. Ensure that the set of redirect URIs includes the URI of your web server. The same URL must be provided for the `Open URL` and `Create URL` fields.
    1. Add the `Google API Scopes` of `https://www.googleapis.com/auth/userinfo.email`, `https://www.googleapis.com/auth/userinfo.profile` and `https://www.googleapis.com/auth/drive.install`.
    1. For icons, use the example icons  provided in the `chromewebstore` directory.
1. Open the `dotnet\DrEdit.sln` solution in Visual Studio.
1. Edit `Models\ClientCredentials.cs` and replace `CLIENT_ID`, `CLIENT_SECRET` and `REDIRECT_URI` with the values from the [Google APIs Console](https://code.google.com/apis/console/) under the **API Access** tab for the project.
1. Edit `Scripts\app.js` and replace `YOUR_APP_ID` with the value of the `CLIENT_ID` from the [Google APIs Console](https://code.google.com/apis/console/) under the **API Access** tab for the project.
1. Build the solution and deploy it on the web server configured in step 2.
1. Test the app by browsing to the web server URI (e.g. `http://www.example.com`).
1. Continue reading to find out how DrEdit is constructed, and how to modify it to work for your own application's needs.
