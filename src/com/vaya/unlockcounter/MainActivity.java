package com.vaya.unlockcounter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.jjoe64.graphview.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public static final String LOG_TAG = "UC_MAIN";
    private static final String activity_title = "Unlock Counter";

    public void updateTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean("use_dark_theme", false)) setTheme(android.R.style.Theme_Holo);
        else setTheme(android.R.style.Theme_Holo_Light);
    }

    private void makeGraph(List<String[]> data) {
        GraphView.GraphViewData[] datagraph = new GraphView.GraphViewData[24];
        Integer[] perhour = new Integer[24];


        for (int j = 0; j <= 23; j++) {
            perhour[j] = 0;
            for (int i = 0; i < data.size(); i++) {
                if (Integer.parseInt(data.get(i)[1]) == j) {
                    perhour[j] += 1;
                }
            }
        }

        for (int i = 0; i < perhour.length; i++) {
            datagraph[i] = new GraphView.GraphViewData(i, perhour[i]);
        }

        GraphViewSeries dataSeries = new GraphViewSeries(datagraph);

        GraphView graphView = new BarGraphView(this, "");
        graphView.addSeries(dataSeries);
        graphView.setHorizontalLabels(new String[]{
                "00h", "2h", "4h", "6h", "8h", "10h",
                "12h", "14h", "16h", "18h", "20h", "22h"});
        graphView.getGraphViewStyle().setGridColor(Color.TRANSPARENT);

        graphView.getGraphViewStyle().setVerticalLabelsWidth(0);
        graphView.getGraphViewStyle().setNumVerticalLabels(0);
        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.TRANSPARENT);


        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                    return Integer.toString((int) Math.round(value));
                }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView);
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

        Spinner spinner = (Spinner) findViewById(R.id.DateSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.date_selection_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        DB db = new DB(this);
        makeGraph(db.get_log());

        Log.d(LOG_TAG, "MainActivity created");
    }

    @Override
    protected void onResume() {
        super.onResume();
        DB db = new DB(this);
        List<String[]> d = db.get_log();

        Log.d(LOG_TAG, "log : " + d.get(0)[0] + " / " +  d.get(0)[1] +
                " \n || Number of unlock :" + Integer.toString(d.size()) );
        TextView counter = (TextView) findViewById(R.id.CounterUnlock);
        counter.setText(Integer.toString(d.size()));
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(LOG_TAG, "Spinner changed is " + Integer.toString(i));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
