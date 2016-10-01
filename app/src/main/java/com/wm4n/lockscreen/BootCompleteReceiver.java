package com.wm4n.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean unlockSwitchPref = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(LockScreenService.KEY_START_LOCKED, true);
        final Intent serviceIntent = new Intent(context, LockScreenService.class);
        serviceIntent.putExtra(LockScreenService.KEY_START_LOCKED, true);
        if(unlockSwitchPref) {
            context.startService(serviceIntent);
        }
    }
}
