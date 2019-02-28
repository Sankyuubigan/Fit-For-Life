package com.nilden.fitforlife.screens

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.nilden.fitforlife.FitForLifeApplication
import com.nilden.fitforlife.R
import com.nilden.fitforlife.utils.SharedPreferencesHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var mSharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var remove_all_history: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mSharedPreferencesHelper = FitForLifeApplication.instance.preferencesHelper
        initViews()
        remove_all_history = findViewById(R.id.remove_all_history)
        remove_all_history.setOnClickListener { showDeleteDialog(this@SettingsActivity, getString(R.string.are_you_sure_delete_all_history)) }
    }

    fun showDeleteDialog(ctx: Context, message: String) {
        AlertDialog.Builder(ctx).setMessage(message)
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    val androidID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                    FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_database_name)).child(androidID).removeValue()
                            .addOnCompleteListener {
                                AlertDialog.Builder(this@SettingsActivity).setMessage(getString(R.string.your_history_has_removed_successfully))
                                        .setPositiveButton(getString(R.string.yes)) { dialog, which -> dialog.dismiss() }.create().show()
                            }
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }.create().show()
    }

    private fun initViews() {
        val toolbar = findViewById<Toolbar>(R.id.tb_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val switchKeepScreenOn = findViewById<SwitchCompat>(R.id.switch_keep_screen_on)
        switchKeepScreenOn.isChecked = mSharedPreferencesHelper.getBoolean(SharedPreferencesHelper.KEY_KEEP_SCREEN_ON)
        switchKeepScreenOn.setOnCheckedChangeListener { buttonView, isChecked -> mSharedPreferencesHelper.saveBoolean(SharedPreferencesHelper.KEY_KEEP_SCREEN_ON, isChecked) }

        val switchUpdateAutomatically = findViewById<SwitchCompat>(R.id.switch_update_automatically)
        switchUpdateAutomatically.isChecked = mSharedPreferencesHelper.getBoolean(SharedPreferencesHelper.KEY_UPDATE_AUTOMATICALLY)
        switchUpdateAutomatically.setOnCheckedChangeListener { buttonView, isChecked -> mSharedPreferencesHelper.saveBoolean(SharedPreferencesHelper.KEY_UPDATE_AUTOMATICALLY, isChecked) }

        val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.update_times))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = findViewById<AppCompatSpinner>(R.id.spinner_update_time)
        spinner.adapter = adapter
        spinner.prompt = "Title"

        var position = 2
        val updateTime = mSharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_UPDATING_TIME)
        when (updateTime) {
            15 -> position = 0
            30 -> position = 1
            60 -> position = 2
        }
        spinner.setSelection(position)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                var updateTime = 30
                when (position) {
                    0 -> updateTime = 15
                    1 -> updateTime = 30
                    2 -> updateTime = 60
                }
                mSharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_UPDATING_TIME, updateTime)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
