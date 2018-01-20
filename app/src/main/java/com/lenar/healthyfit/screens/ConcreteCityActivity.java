package com.lenar.healthyfit.screens;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.adapters.WeatherDataAdapter;
import com.lenar.healthyfit.application.WeatherApplication;
import com.lenar.healthyfit.entities.City;
import com.lenar.healthyfit.entities.WeatherData;
import com.lenar.healthyfit.models.CityWeatherController;
import com.lenar.healthyfit.utils.SharedPreferencesHelper;
import com.lenar.healthyfit.view.CityWeatherView;

import java.util.ArrayList;

public class ConcreteCityActivity extends AppCompatActivity implements CityWeatherView {

    CityWeatherController mController;

    private int position;
    public static final String KEY_CITY = "city_position";
    private City mCity;
    private WeatherDataAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concrete_bid);
        if (WeatherApplication.getInstance().getPreferencesHelper().getBoolean(SharedPreferencesHelper.KEY_KEEP_SCREEN_ON)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        position = getIntent().getIntExtra(KEY_CITY, 1);
        initViews();

        if (savedInstanceState == null) {
            mController = new CityWeatherController(this, ConcreteCityActivity.this, position);
        }
        mController.loadWeather(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mController.onStop();
    }

    private void initViews() {
        mProgressBar = findViewById(R.id.progress_bar);
        Toolbar toolbar = findViewById(R.id.tb_concrete_city);
        ImageView ivCity = findViewById(R.id.iv_city);
        refreshLayout = findViewById(R.id.srl_concrete_bid);
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mController.loadWeather(true);
            }
        });

        mCity = WeatherApplication.getInstance().getCities().get(position);

        toolbar.setTitle(mCity.getName());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setCollapsingToolbarLayoutTitle(mCity.getName());

        ivCity.setImageResource(mCity.getImage());

        RecyclerView recyclerView = findViewById(R.id.rv_concrete_bid);
        mAdapter = new WeatherDataAdapter(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        CollapsingToolbarLayout ctbConcreteBid = findViewById(R.id.ctb_concrete_bid);
        ctbConcreteBid.setTitle(title);
        ctbConcreteBid.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        ctbConcreteBid.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        ctbConcreteBid.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        ctbConcreteBid.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mController.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showWeather(ArrayList<WeatherData> data) {
        refreshLayout.setRefreshing(false);
        mAdapter.swap(data);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }
}
