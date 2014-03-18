package com.cloudbees.demo.gasp.utils;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.cloudbees.demo.gasp.fragment.LocationFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class LocationServices {
    private static final String TAG = LocationServices.class.getName();

    /**
     * Enable location checking (via LocationFragment)
     * @param context the application context
     */
    public static void enableLocationChecking(Context context) {
        try {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {

                // Check current location
                Location location = LocationFragment.getLocation(context);
                if (location != null) {
                    Log.d(TAG, "Location: " + String.format("%.6f", location.getLatitude())
                            + ", " + String.format("%.6f", location.getLongitude())
                            + " (via " + location.getProvider() + ")" + '\n');
                }

                if (! context.getClass().isInstance(FragmentActivity.class))
                    throw new UnsupportedOperationException();
                FragmentActivity activity = (FragmentActivity) context;

                // Add LocationFragment to enable location updates
                FragmentManager fm = activity.getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                LocationFragment locationFragment = new LocationFragment();
                ft.add(locationFragment, "LocationFragment");
                ft.commit();
            }
            else
                Log.e(TAG, "Google Play Services not available");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set Location Services and get current location
     */
    public static Location getLocation(Context context) {
        Location location = null;
        try {
            String svcName = Context.LOCATION_SERVICE;
            LocationManager locationManager = (LocationManager) context.getSystemService(svcName);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            String provider = locationManager.getBestProvider(criteria, true);
            location = locationManager.getLastKnownLocation(provider);

            Log.i(TAG, "Current Latitude = " + location.getLatitude());
            Log.i(TAG, "Current Longitude = " + location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
}
