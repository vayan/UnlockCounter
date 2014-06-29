package com.vaya.unlockcounter;

import android.app.backup.*;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;

/**
 * Created by vayan on 6/29/14.
 */
public class mBackupAgent extends BackupAgentHelper {

    // The name of the SharedPreferences file
    public static final String LOG_TAG = "BU";
    static final String PREFS = "com.vaya.unlockcounter_preferences";

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "prefs";
    static final String FILES_BACKUP_KEY = "myfiles";


    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        FileBackupHelper helperData = new FileBackupHelper(this,
                "../databases/" + DB.DB_NAME);

        SharedPreferencesBackupHelper helperPrefs = new SharedPreferencesBackupHelper(this, PREFS);

        addHelper(PREFS_BACKUP_KEY, helperPrefs);
        addHelper(FILES_BACKUP_KEY, helperData);
        Log.d(LOG_TAG, "backup agent created !");
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper performs backup
        Log.d(LOG_TAG, "backing up app data");
        synchronized (DB.sDataLock) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper restores the file
        Log.d(LOG_TAG, "restoring up app data");
        synchronized (DB.sDataLock) {
            super.onRestore(data, appVersionCode, newState);
        }
    }

}

