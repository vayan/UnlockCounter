package com.vaya.unlockcounter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;


public class SettingsActivity extends Activity {
    public static final String LOG_TAG = "UC_SETTINGS";
    private static final String activity_title = "Settings";
    public static final Integer DARK_THEME = android.R.style.Theme_Holo;
    public static final Integer LIGHT_THEME = android.R.style.Theme_Holo_Light_DarkActionBar;

    private void updateTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean("use_dark_theme", false)) setTheme(DARK_THEME);
        else setTheme(LIGHT_THEME);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(LOG_TAG, "Create frag settings");
            addPreferencesFromResource(R.xml.settings_activity);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
            Log.d(LOG_TAG, "Settings changed ");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Create activity settings");

        updateTheme();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(activity_title);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}