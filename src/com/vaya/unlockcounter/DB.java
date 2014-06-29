package com.vaya.unlockcounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    static final Object sDataLock = new Object();
    public static final String LOG_TAG = "UC_SQLITE";
    private Context ctxt;
    public static final String DB_NAME = "log.db    ";
    private static final String DB_TABLE_NAME = "log";
    private static final int DB_VERSION = 1;
    private static final String DB_CREATE =
            "CREATE TABLE log (\n" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    " timestamp TEXT NOT NULL,\n" +
                    " hour TEXT NOT NULL\n" +
                    ");";
    private String[] DB_ALL_COLUMNS = { "timestamp", "hour" };

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.ctxt = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_CREATE);
        Log.d(LOG_TAG, "DB Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    //TODO
    }

    public List<String[]> get_log() {
        try {
            synchronized (DB.sDataLock) {
                List<String[]> all_logs = new ArrayList<String[]>();
                SQLiteDatabase bdd = this.getWritableDatabase();

                Cursor cursor = bdd.query(DB_TABLE_NAME,
                        DB_ALL_COLUMNS, null, null, null, null, null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    all_logs.add(new String[]{cursor.getString(0), cursor.getString(1)});
                    cursor.moveToNext();
                }
                cursor.close();
                bdd.close();
                return all_logs;
            }
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Unable to read the db : " + e.toString());
        }
        return null;
    }

    public void add_log() {
        try {
            synchronized (DB.sDataLock) {
                SQLiteDatabase bdd = this.getWritableDatabase();
                Long c = Calendar.getInstance().getTimeInMillis();

                ContentValues values = new ContentValues();
                values.put("timestamp", c.toString());
                values.put("hour", Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));

                bdd.insert(DB_TABLE_NAME, null, values);
                bdd.close();
                Log.d(LOG_TAG, "add log");
            }
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Unable to write the db : " + e.toString());
        }
    }
}
