package com.wm4n.lockscreen;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private final static String PREF_KEY_UNLOCK_SWITCH = "unlock_switch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch switchView = (Switch)findViewById(R.id.unlock_switch);

        if(null != switchView) {
            boolean unlockSwitchPref = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_KEY_UNLOCK_SWITCH, true);
            switchView.setChecked(unlockSwitchPref);
            final Intent intent = new Intent(this, LockScreenService.class);
            if(unlockSwitchPref) {
                startService(intent);
            }
            switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        startService(intent);
                    }
                    else {
                        stopService(intent);
                    }
                }
            });
        }


    }

}
