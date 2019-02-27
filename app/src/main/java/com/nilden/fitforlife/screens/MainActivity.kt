package com.nilden.fitforlife.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

import com.nilden.fitforlife.R
import com.nilden.fitforlife.fragments.AddNewEntriesFragment
import com.nilden.fitforlife.fragments.HistoryFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    internal lateinit var toolbar: Toolbar
    internal lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer = findViewById(R.id.drawer_layout)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AddNewEntriesFragment()).commit()

        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.add_new_entries)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = object : ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                if (slideOffset == 0f) {
                    // drawer closed
                } else if (slideOffset != 0f) {
                    // started opening
                    val view = currentFocus
                    if (view != null) {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
                super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.add_new_entries) {
            // Handle the camera action
            toolbar.setTitle(R.string.add_new_entries)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AddNewEntriesFragment()).commit()
        } else if (id == R.id.history) {
            toolbar.setTitle(R.string.history_of_entries)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HistoryFragment()).commit()
        } else if (id == R.id.pedometer) {
            val settingIntent = Intent(this, PedometerActivity::class.java)
            startActivity(settingIntent)
        } else if (id == R.id.settings) {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
