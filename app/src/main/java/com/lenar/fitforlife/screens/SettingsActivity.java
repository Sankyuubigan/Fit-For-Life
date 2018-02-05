package com.lenar.fitforlife.screens;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.lenar.fitforlife.FitForLifeApplication;
import com.lenar.fitforlife.R;
import com.lenar.fitforlife.utils.SharedPreferencesHelper;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferencesHelper mSharedPreferencesHelper;
    private TextView remove_all_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mSharedPreferencesHelper = FitForLifeApplication.getInstance().getPreferencesHelper();
        initViews();
        remove_all_history = findViewById(R.id.remove_all_history);
        remove_all_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(SettingsActivity.this, "Вы уверены, что хотите очистить всю Вашу историю записей?");
            }
        });
    }

    public void showDeleteDialog(Context ctx, String message) {
        new AlertDialog.Builder(ctx).setMessage(message)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        FirebaseDatabase.getInstance().getReference("users_history").child(androidID).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        new AlertDialog.Builder(SettingsActivity.this).setMessage("Ваша история записей была успешно очищена")
                                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create().show();
                                    }
                                });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
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
