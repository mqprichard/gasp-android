Gasp! GCM for Android Demo Client
=================================

Android demo client for the [gasp-gcm-server](https://github.com/mqprichard/gasp-gcm-server) application, which uses CloudBees PaaS and Foxweave to provide automatic data sync between the Gasp! server database and Android SQLite on-device data stores.  This demo application uses [Google Cloud Messaging for Android](http://developer.android.com/google/gcm/index.html) and was built with the latest preview of Android Studio with the Android gradle build system.

> <img src="http://www.cloudbees.com/sites/all/themes/custom/cloudbees_zen/css/bidesign/_ui/images/logo.png"/>
>
> <b>Note</b>: <i>This repo is part of the Gasp demo project - a showcase of <a href="https://developer.cloudbees.com/bin/view/Mobile">cloudbees mobile services</a>.
> You can see the big picture of the <a href="http://mobilepaas.cloudbees.com">showcase here</a>.
> Feel free to fork and use this repo as a template.</i>

Quick Overview
--------------

1. On startup, the main Activity (ReviewSyncActivity) will connect to the Gasp! server (using the endpoint configured via Shared Preferences - see res/xml/preferences.xml) to retrieve all reviews aurrently in the database via the REST service interface; the data is loaded into the on-device SQLite database using the ReviewsDataSource wrapper class (via OpenSQLiteHelper).
2. The application will register with both
   - The Google Cloud Messaging for Android Service;
   - The gasp-gcm-server, which will send asynchronous notifications whenever there is an update to the Gasp! database.
3. GCM Notifications are handled by the GCMIntentService, which will do the following:
   - Get the review Id from the notification Intent and fetch the review data via an async REST call to the Gasp! server;
   - Insert the new record into the on-device SQLite database to sync with the Gasp! server;
   - Generate a notification ("New Gasp! Review"), which will appear in the Android pull-down notifications panel to alert the user that new review data has been received.

ReviewSyncActivity uses a simple TextView display to show messages for registration and notification events, but the GCMIntentService will be triggered and run in the background regardless of the current activity - notifications will be visible in the pull-down panel.  

The Options Menu allows you to:
   - Clear all messages
   - Exit the application
   - Edit the Gasp! server endpoint URL
   - View the SQLite database (reviews shown in reverse order)

Pre-reqs
--------
1. Run the [Gasp! Server](https://github.com/cloudbees/gasp-server) application on CloudBees
2. Configure Google APIs for Google Cloud Messaging - see [instructions here](https://github.com/mqprichard/gasp-gcm-server/blob/master/README.md).  You will need to edit CommonUtilities to set SENDER_ID equal to your 12-digit Google API Project Number, to match the API Key configured for the gasp-gcm-server.
3. Configure and run the FoxWeave Integration Service and the gasp-gcm server application - see [instructions here](https://github.com/mqprichard/gasp-gcm-server/blob/master/README.md).

Building the Demo Client
------------------------
You can build the application directly from Android Studio or via gradle. The application needs both the Android support and GCM client libraries from the Android SDK, so build.gradle uses the [CloudBees maven-android-sdk repository](https://repository-maven-android-sdk.forge.cloudbees.com/release/) for these dependencies.  Thanks to [these folk](https://github.com/mosabua/maven-android-sdk-deployer) for enabling this!

Build using `gradle clean build` or use the gradle wrapper.

Running the Demo Client
-----------------------
The easiest way to is run the app directly from Android Studio; alternatively deploy using adb and use `am start -n "com.cloudbees.gasp.gcm/com.cloudbees.gasp.activity.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER`.  Note that if you are running on an AVD emulator, you must configure it with a Google APIs target to use the GCM service.

Resetting the Demo Client
-------------------------
To re-run the demo from a clean state, you should uninstall the app to remove the GCM registration and delete the SQLite database.  
