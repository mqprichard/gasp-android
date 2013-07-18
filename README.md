Gasp! GCM for Android Demo Client
=================================

Android demo client for the [gasp-gcm-server](https://github.com/mqprichard/gasp-gcm-server) application, which uses CloudBees PaaS and Foxweave to provide automatic data sync between the Gasp! server database and Android SQLite on-device data stores.  This demo application was built with the latest preview of Android Studio and uses the Android gradle build system.

Quick Overview
--------------

1. On startup, the main Activity (ReviewSyncActivity) will connect to the Gasp! server (using the endpoint configured via Shared Preferences - see res/xml/preferences.xml) to retrieve all reviews aurrently in the database via the REST service interface; the data is loaded into the on-device SQLite database using the ReviewsDataSource wrapper class (via OpenSQLiteHelper).
2. 
