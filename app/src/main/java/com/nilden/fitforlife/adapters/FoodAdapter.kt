package com.nilden.fitforlife.adapters

import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nilden.fitforlife.R
import com.nilden.fitforlife.adapters.FoodAdapter.FoodViewHolder
import com.nilden.fitforlife.models.Food
import kotlinx.android.synthetic.main.item_nutrition.view.*
import java.util.*

class FoodAdapter(private val mData: ArrayList<Food>, internal var ctx: FragmentActivity) : RecyclerView.Adapter<FoodViewHolder>() {
    private var onDeleteListener: OnDeleteListener? = null

    interface OnDeleteListener {
        fun onDelete(name: String)
    }

    fun setOnDeleteListener(onDeleteListener: OnDeleteListener) {
        this.onDeleteListener = onDeleteListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_nutrition, parent, false)
        return FoodViewHolder(v)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bindFood(mData[position])
    }

    fun showDeleteDialog(message: String, position: Int) {
        AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(ctx.getString(R.string.yes)) { dialog, which ->
                    onDeleteListener?.onDelete(mData[position].name)
                    mData.removeAt(position)
                }
                .setNegativeButton(ctx.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }.create().show()
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindFood(food: Food) {
            itemView.tv_food_name
            itemView.tv_food_name.text = food.name
            itemView.tv_food_kkal.text = "${food.kkal} ккал"
            itemView.iv_delete.setOnClickListener { showDeleteDialog(ctx.getString(R.string.are_you_sure_delete_this_entry), adapterPosition) }
        }

    }
}
