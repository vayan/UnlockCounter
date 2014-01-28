package com.vaya.unlockcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenBroadReceiver extends BroadcastReceiver {
    public static final String LOG_TAG = "UC_SCREENEVENT";
    private Context ctxt = null;

    public ScreenBroadReceiver(Context ctxt) {
        this.ctxt = ctxt;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            SQLite db = new SQLite(this.ctxt);
            db.add_log(SQLite.DB_SCREEN_LOCK);
            Log.d(LOG_TAG, "Screen locked");
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            SQLite db = new SQLite(this.ctxt);
            db.add_log(SQLite.DB_SCREEN_UNLOCK);
            Log.d(LOG_TAG, "Screen unlock");
        }
    }

}