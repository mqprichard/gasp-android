/*
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.gasp.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.cloudbees.gasp.R;

public class PreferencesFragment extends PreferenceFragment {
    private static final String TAG = PreferencesFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from res/xml/preferences.xml
        addPreferencesFromResource(R.xml.preferences);

        Preference radiusPreference =
                getPreferenceScreen().findPreference(getString(R.string.places_search_radius_preferences));
        Preference gaspServerPreference =
                getPreferenceScreen().findPreference(getString(R.string.gasp_server_uri_preferences));
        Preference gaspPushPreference =
                getPreferenceScreen().findPreference(getString(R.string.gasp_push_uri_preferences));

        radiusPreference.setOnPreferenceChangeListener(changeListener);
        gaspServerPreference.setOnPreferenceChangeListener(changeListener);
        gaspPushPreference.setOnPreferenceChangeListener(changeListener);
    }

    Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d(TAG, "Preference changed: " + preference.getKey());
            return true;
        }
    };
}
