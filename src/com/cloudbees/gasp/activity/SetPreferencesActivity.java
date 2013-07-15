package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.os.Bundle;

import com.cloudbees.gasp.fragment.PreferencesFragment;

public class SetPreferencesActivity extends Activity {
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 getFragmentManager().beginTransaction().replace(android.R.id.content,
	                new PreferencesFragment()).commit();
	 }
}
