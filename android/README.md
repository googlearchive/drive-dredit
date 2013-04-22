# DrEdit for Android

A walkthrough and more details are available on
[Google Drive SDK docs](https://developers.google.com/drive/examples/android).
## Setup Instructions

1. Make sure you have [Eclipse](http://www.eclipse.org) and the
   [Android plugin for Eclipse](http://developer.android.com/sdk/installing/installing-adt.html) installed.
1. Clone DrEdit's git repo and init submodules:

        git clone git@github.com:googledrive/dredit.git && cd dredit
        git submodule init
        git submodule update --recursive

1. Enable the Drive API for your API Project as described in
[Enable the Drive API](https://developers.google.com/drive/enable-sdk).
1. The contents of the `android/` directory contains the project. Create an Android Application Project with the existing source.
1. Generate and note the signing certificate's SHA-1 fingerprint as described in [the Android quickstart's step 1](https://developers.google.com/drive/quickstart-android#step_1_generate_the_signing_certificate_fingerprint_sha1)
1. Enable the Drive API for your API Project and setup OAuth 2.0 credentials for your Android application as described in [the Android quickstart's step 2](https://developers.google.com/drive/quickstart-android#step_2_enable_the_drive_api). You'll need your application's package name `com.example.android.notepad` and your SHA-1 signature from step 3.
1. In the `AndroidManifest.xml`, replace the `APPLICATION_ID` with the ID of the APIs Console's application.
1. Build the project and run it on a physical device or on the Android Emulator.
1. Once you are signed in, test the app by creating and editing text files.
1. Continue reading to find out how DrEdit is constructed, and how to modify it to work for your own application's needs.
