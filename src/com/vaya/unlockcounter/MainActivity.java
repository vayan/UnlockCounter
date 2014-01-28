package com.vaya.unlockcounter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {

    public static final String LOG_TAG = "UC_MAIN";
    private static final String activity_title = "Unlock Counter";

    public void updateTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean("use_dark_theme", false)) setTheme(android.R.style.Theme_Holo);
        else setTheme(android.R.style.Theme_Holo_Light);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(MainActivity.this, LockerService.class));
        super.onCreate(savedInstanceState);

        updateTheme();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "MainActivity created");
    }

    @Override
    protected void onResume() {
        super.onResume();
        DB db = new DB(this);
        List<String> d = db.get_log();
        List<String> lock =  db.get_lock_log();
        List<String> unlock =  db.get_unlock_log();

        Log.d(LOG_TAG, "log : " + d.toString() +
                " \n || Number of unlock :" + Integer.toString(unlock.size()) +
                " \n || Number of lock :" + Integer.toString(lock.size())
        );
        TextView counter = (TextView) findViewById(R.id.CounterUnlock);
        counter.setText(Integer.toString(unlock.size()));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
