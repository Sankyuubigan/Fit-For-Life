package com.lenar.healthyfit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.entities.WeatherData;

import java.util.ArrayList;

public class WeatherDataAdapter extends RecyclerView.Adapter<WeatherDataAdapter.ViewHolder> {

    private ArrayList<WeatherData> mWeatherData;

    public WeatherDataAdapter(ArrayList<WeatherData> weatherData) {
        mWeatherData = weatherData;
    }

    @Override
    public WeatherDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_data, parent, false);
        return new ViewHolder(itemView);
    }

    public void swap(ArrayList<WeatherData> data) {
        if (mWeatherData == null) {
            mWeatherData = new ArrayList<>();
        } else {
            mWeatherData.clear();
        }

        if (data != null && data.size() > 0) {
            mWeatherData.addAll(data);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(WeatherDataAdapter.ViewHolder holder, int position) {
        holder.icon.setImageResource(mWeatherData.get(position).getIcon());
        holder.data.setText(mWeatherData.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return mWeatherData == null ? 0 : mWeatherData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView data;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_option_icon);
            data = itemView.findViewById(R.id.tv_option_name);
        }
    }
}
