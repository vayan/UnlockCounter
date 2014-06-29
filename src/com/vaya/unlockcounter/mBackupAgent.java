package com.vaya.unlockcounter;

import android.app.backup.*;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

/**
 * Created by vayan on 6/29/14.
 */
public class mBackupAgent extends BackupAgentHelper {

    // The name of the SharedPreferences file
    static final String PREFS = "user_preferences";
    static final String LOCK_LOGS = "log.db";


    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "prefs";
    static final String FILES_BACKUP_KEY = "myfiles";


    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        FileBackupHelper helperData = new FileBackupHelper(this, LOCK_LOGS);
        SharedPreferencesBackupHelper helperPrefs = new SharedPreferencesBackupHelper(this, PREFS);

        addHelper(PREFS_BACKUP_KEY, helperPrefs);
        addHelper(FILES_BACKUP_KEY, helperData);

    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper performs backup
        synchronized (DB.sDataLock) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper restores the file
        synchronized (DB.sDataLock) {
            super.onRestore(data, appVersionCode, newState);
        }
    }

}

