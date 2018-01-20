package com.lenar.healthyfit.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.application.WeatherApplication;
import com.lenar.healthyfit.utils.SharedPreferencesHelper;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferencesHelper mSharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mSharedPreferencesHelper = WeatherApplication.getInstance().getPreferencesHelper();
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.tb_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SwitchCompat switchKeepScreenOn = findViewById(R.id.switch_keep_screen_on);
        switchKeepScreenOn.setChecked(mSharedPreferencesHelper.getBoolean(SharedPreferencesHelper.KEY_KEEP_SCREEN_ON));
        switchKeepScreenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferencesHelper.saveBoolean(SharedPreferencesHelper.KEY_KEEP_SCREEN_ON, isChecked);
            }
        });

        SwitchCompat switchUpdateAutomatically = findViewById(R.id.switch_update_automatically);
        switchUpdateAutomatically.setChecked(mSharedPreferencesHelper.getBoolean(SharedPreferencesHelper.KEY_UPDATE_AUTOMATICALLY));
        switchUpdateAutomatically.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferencesHelper.saveBoolean(SharedPreferencesHelper.KEY_UPDATE_AUTOMATICALLY, isChecked);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.update_times));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        AppCompatSpinner spinner = findViewById(R.id.spinner_update_time);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Title");

        int position = 2;
        int updateTime = mSharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_UPDATING_TIME);
        switch (updateTime) {
            case 15:
                position = 0;
                break;
            case 30:
                position = 1;
                break;
            case 60:
                position = 2;
                break;
        }
        spinner.setSelection(position);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int updateTime = 30;
                switch (position) {
                    case 0:
                        updateTime = 15;
                        break;
                    case 1:
                        updateTime = 30;
                        break;
                    case 2:
                        updateTime = 60;
                        break;
                }
                mSharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_UPDATING_TIME, updateTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
