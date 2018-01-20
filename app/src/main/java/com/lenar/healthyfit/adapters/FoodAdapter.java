package com.lenar.healthyfit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.models.Food;

import java.util.ArrayList;

/**
 * Created by Nilden on 20.01.2018.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private ArrayList<Food> mData;
//    private OnCityClickListener mClickListener;

    public FoodAdapter(ArrayList<Food> cities) {
        mData = cities;
    }

//    public void setOnCityClickListener(OnCityClickListener clickListener) {
//        mClickListener = clickListener;
//    }

    @Override
    public FoodAdapter.FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nutrition, parent, false);
        return new FoodAdapter.FoodViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FoodAdapter.FoodViewHolder holder, final int position) {
        holder.tv_food_name.setText(mData.get(position).getName());
        holder.tv_food_kkal.setText(mData.get(position).getKkal()+" ккал");
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tv_food_name;
        TextView tv_food_kkal;
        ImageView iv_delete;

        FoodViewHolder(View itemView) {
            super(itemView);
            tv_food_name = itemView.findViewById(R.id.tv_food_name);
            tv_food_kkal = itemView.findViewById(R.id.tv_food_kkal);
            iv_delete = itemView.findViewById(R.id.iv_delete);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            if (mClickListener != null) {
//                mClickListener.onCityClick(getAdapterPosition());
//            }
//        }
    }
}
