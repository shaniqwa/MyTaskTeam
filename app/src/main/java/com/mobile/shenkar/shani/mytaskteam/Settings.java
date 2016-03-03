package com.mobile.shenkar.shani.mytaskteam;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Viki on 02/03/2016.
 */
public class Settings extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        myToolbar.setTitle("Settings");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        NumberPicker np = (NumberPicker)findViewById(R.id.numberPicker1);
        np.setMinValue(1);// restricted number to minimum value i.e 1
        np.setMaxValue(31);// restricked number to maximum value i.e. 31
        np.setWrapSelectorWheel(true);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String Val = String.valueOf(newVal);

                PreferenceManager.getDefaultSharedPreferences(Settings.this).edit().putString("TimeInterval", Val).commit();
            }
        });

        Log.d("NumberPicker", "NumberPicker");

    }

}/* NumberPickerActivity */

