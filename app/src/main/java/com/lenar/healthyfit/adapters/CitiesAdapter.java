package com.lenar.healthyfit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.entities.City;
import com.lenar.healthyfit.listeners.OnCityClickListener;

import java.util.ArrayList;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CityViewHolder> {

    private ArrayList<City> mCities;
    private OnCityClickListener mClickListener;

    public CitiesAdapter(ArrayList<City> cities) {
        mCities = cities;
    }

    public void setOnCityClickListener(OnCityClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public CitiesAdapter.CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CitiesAdapter.CityViewHolder holder, int position) {
        holder.tvCityName.setText(mCities.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mCities == null ? 0 : mCities.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCityName;

        CityViewHolder(View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tv_city_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onCityClick(getAdapterPosition());
            }
        }
    }
}
