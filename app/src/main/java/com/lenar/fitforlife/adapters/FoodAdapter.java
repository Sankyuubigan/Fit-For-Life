package com.lenar.fitforlife.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenar.fitforlife.R;
import com.lenar.fitforlife.models.Food;

import java.util.ArrayList;

/**
 * Created by Nilden on 20.01.2018.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    //    private boolean notShowBtnDelete = false;
    private ArrayList<Food> mData;
    //    private OnCityClickListener mClickListener;
    View.OnClickListener onDeleteListener;
    Context ctx;

    public void setOnDeleteListener(View.OnClickListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public FoodAdapter(ArrayList<Food> mData, Context ctx) {
        this.mData = mData;
        this.ctx = ctx;
    }

//    public void setNotShowBtnDelete(boolean notShowBtnDelete) {
//        this.notShowBtnDelete = notShowBtnDelete;
//    }
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
        holder.tv_food_kkal.setText(mData.get(position).getKkal() + " ккал");
//        if (notShowBtnDelete)
//            holder.iv_delete.setVisibility(View.INVISIBLE);
//        else
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog("Вы уверены, что хотите удалить запись?", position);
            }
        });
    }

    public void showDeleteDialog(String message, final int position) {
        new AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mData.remove(position);
                        onDeleteListener.onClick(null);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
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
