Gasp! GCM for Android Demo Client
=================================

Android demo client for the [gasp-gcm-server](https://github.com/mqprichard/gasp-gcm-server) application, which uses CloudBees PaaS and Foxweave to provide automatic data sync between the Gasp! server database and Android SQLite on-device data stores.  This demo application uses [Google Cloud Messaging for Android](http://developer.android.com/google/gcm/index.html) and was built with the latest preview of Android Studio with the Android gradle build system.

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

