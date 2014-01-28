package com.vaya.unlockcounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SQLite  extends SQLiteOpenHelper {
    /*
    type_event in the db : 1 == unlock , 0 == lock
     */

    public static final int DB_SCREEN_UNLOCK = 1;
    public static final int DB_SCREEN_LOCK = 0;


    public static final String LOG_TAG = "UC_SQLITE";
    private Context ctxt;
    private static final String DB_NAME = "log.db";
    private static final String DB_TABLE_NAME = "log";
    private static final int DB_VERSION = 1;
    private static final String DB_CREATE =
            "CREATE TABLE log (\n" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            " type_event INTEGER NOT NULL DEFAULT '0',\n" +
            " date TEXT NOT NULL\n" +
            ");";
    private String[] DB_ALL_COLUMNS = {"type_event",
            "date" };

    public SQLite(Context context) {
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

    }

    public List<String> get_log() {
        List<String> all_logs = new ArrayList<String>();
        SQLiteDatabase bdd = this.getWritableDatabase();

        Cursor cursor = bdd.query(DB_TABLE_NAME,
                DB_ALL_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            all_logs.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        bdd.close();
        return all_logs;
    }

    public void add_log(Integer type_event) {
        SQLiteDatabase bdd = this.getWritableDatabase();
        Long c = Calendar.getInstance().getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put("type_event", type_event);
        values.put("date", c.toString());

        bdd.insert(DB_TABLE_NAME, null, values);
        bdd.close();
        Log.d(LOG_TAG, "add log");
    }
}
