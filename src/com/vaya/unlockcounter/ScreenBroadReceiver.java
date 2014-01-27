package com.vaya.unlockcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenBroadReceiver extends BroadcastReceiver {
    public static final String LOG_TAG = "UC_SCREENEVENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // screen locked
            Log.d(LOG_TAG, "Screen locked");
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            // screen is on
            Log.d(LOG_TAG, "Screen unlock");
        }
    }

}