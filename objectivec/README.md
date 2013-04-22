# DrEdit for iOS


A walkthrough and more details are available on
[Google Drive SDK docs](https://developers.google.com/drive/examples/objectivec).

## Setup Instructions

1. Clone DrEdit's git repo and init submodules:

        git clone git@github.com:googledrive/dredit.git && cd dredit
        git submodule init
        git submodule update --recursive
1. Enable the Drive API for your API Project as described in
[Enable the Drive API](https://developers.google.com/drive/enable-sdk).
1. The contents of the `objectivec/` directory contains the project. Open the `DrEdit/DrEdit.xcodeproj` project in Xcode.
1. Edit `DrEdit/DrEdit/DrEditFilesListViewController.m` and replace
`<CLIENT_ID>` and `<CLIENT_SECRET>` with the values from the
[Google APIs Console](https://code.google.com/apis/console/) under the
*API Access* tab for the project.   **Note:** Make sure you use a
valid **Client ID for installed applications**.
1. Build the project and run it on the iOS simulator.
1. Continue reading to find out how DrEdit is constructed, and how to modify it to work for your own application's needs.
