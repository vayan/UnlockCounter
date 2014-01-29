package com.vaya.unlockcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadReceiver extends BroadcastReceiver {
    public static final String LOG_TAG = "UC_SCREENEVENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LockerService.class));
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //we don't use lock event, "sho why do keep it" shut up me.
            Log.d(LOG_TAG, "Screen locked");
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            DB db = new DB(context);
            db.add_log();
            db.close();
            Log.d(LOG_TAG, "Screen unlock");
        }
    }

}