package com.vaya.unlockcounter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.backup.BackupManager;
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
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public static final String LOG_TAG = "UC_MAIN";
    public static final Double[] timestamp_to_reduce = new Double[] {86400000d, 604800000d, 2628000000d, 15770000000d, 31540000000000d};
    private static final String activity_title = "Unlock Counter";
    private GraphView graphView = null;

    private void updateTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean("use_dark_theme", false)) setTheme(SettingsActivity.DARK_THEME);
        else setTheme(SettingsActivity.LIGHT_THEME);
    }

    private int get_current_spinner_selection() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(LOG_TAG, "Spinner settings is : " + Integer.toString(sharedPref.getInt("spinner_selection", 0)));
        return sharedPref.getInt("spinner_selection", 0);
    }

    private void refresh_all() {
        int numberlock = 0;
        int current_selection = get_current_spinner_selection();
        Long now = Calendar.getInstance().getTimeInMillis();

        DB db = new DB(this);
        List<String[]> d = db.get_log();
        List<String[]> d_aftertime = new ArrayList<String[]>();

        if (d.size() > 0) {
            for (int i = 0; i < d.size(); i++) {
                //Log.d(LOG_TAG, "lock = " + d.get(i)[0] + " other is = " + Double.toString(now - timestamp_to_reduce[current_selection]));
                if ( Double.parseDouble(d.get(i)[0])  >= now - timestamp_to_reduce[current_selection]) {
                    numberlock++;
                    //Log.d(LOG_TAG, "this one ok");
                    d_aftertime.add(new String[] {d.get(i)[0], d.get(i)[1]});
                }
            }
            //Log.d(LOG_TAG, "log : " + d.get(0)[0] + " / " +  d.get(0)[1] +
                    //" \n || Number of unlock :" + Integer.toString(d.size()) );
            makeGraph(d_aftertime);
        }
        TextView counter = (TextView) findViewById(R.id.CounterUnlock);
        counter.setText(Integer.toString(numberlock));

        Spinner spinner = (Spinner) findViewById(R.id.DateSpinner);
        spinner.setSelection(get_current_spinner_selection());
    }

    private void makeGraph(List<String[]> data) {
        //TODO : Probably a lot of stuff to optimize here if lag
        GraphView.GraphViewData[] datagraph = new GraphView.GraphViewData[24];
        Integer[] perhour = new Integer[24];
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);

        if (this.graphView != null) {
            layout.removeView(this.graphView);
        }

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

        graphView.getGraphViewStyle().setGridColor(Color.TRANSPARENT); //remove grid
        graphView.getGraphViewStyle().useTextColorFromTheme(this);
        //graphView.getGraphViewStyle().setNumVerticalLabels(4);

        //try to remove left label
        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.TRANSPARENT);


        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                    //no float number in label
                    return Integer.toString((int) Math.round(value));
                }
        });

        layout.addView(graphView);
        this.graphView = graphView;
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
        Log.d(LOG_TAG, "MainActivity created");
        startService(new Intent(MainActivity.this, LockerService.class));
        super.onCreate(savedInstanceState);

        updateTheme(); //dark theme or not

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.DateSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.date_selection_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(get_current_spinner_selection());
        spinner.setAdapter(adapter);
        refresh_all();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "MainActivity resume");
        super.onResume();
        refresh_all();

        BackupManager bu = new BackupManager(this);
        bu.dataChanged();
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("spinner_selection", i);
        editor.commit();

        refresh_all();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
