package com.lenar.healthyfit.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.fragments.AddNewEntriesFragment;
import com.lenar.healthyfit.listeners.OnCityClickListener;

import static com.lenar.healthyfit.screens.ConcreteCityActivity.KEY_CITY;

public class MainActivity extends AppCompatActivity implements OnCityClickListener {

    private FrameLayout fragment_container;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(new Intent(this, HistoryActivity.class));
        fragment_container = findViewById(R.id.fragment_container);
        FragmentManager myFragmentManager;
        myFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new AddNewEntriesFragment());
        fragmentTransaction.commit();

        Toolbar toolbar = findViewById(R.id.tb_main);
        toolbar.setTitle("Добавить новые записи");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset != 0) {
                    // started opening
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCityClick(int position) {
        Intent concreteCityIntent = new Intent(this, ConcreteCityActivity.class);
        concreteCityIntent.putExtra(KEY_CITY, position);
        startActivity(concreteCityIntent);
    }
}
