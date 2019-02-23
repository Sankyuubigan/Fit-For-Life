package com.nilden.fitforlife.adapters

import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nilden.fitforlife.R
import com.nilden.fitforlife.adapters.FoodAdapter.FoodViewHolder
import com.nilden.fitforlife.models.Food
import java.util.*

 class FoodAdapter(private val mData: ArrayList<Food>?, internal var ctx: FragmentActivity?) : RecyclerView.Adapter<FoodViewHolder>() {
    internal var onDeleteListener: OnDeleteListener? = null

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
        holder.tv_food_name.text = mData!![position].name
        holder.tv_food_kkal.text = mData[position].kkal.toString() + " ккал"
        holder.iv_delete.setOnClickListener { showDeleteDialog("Вы уверены, что хотите удалить запись?", position) }
    }

    fun showDeleteDialog(message: String, position: Int) {
        AlertDialog.Builder(ctx!!)
                .setMessage(message)
                .setPositiveButton("Да") { dialog, which ->
                    onDeleteListener?.onDelete(mData!![position].name)
                    mData?.removeAt(position)
                }
                .setNegativeButton("Отмена") { dialog, which -> dialog.dismiss() }.create().show()
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_food_name: TextView
        var tv_food_kkal: TextView
        var iv_delete: ImageView

        init {
            tv_food_name = itemView.findViewById(R.id.tv_food_name)
            tv_food_kkal = itemView.findViewById(R.id.tv_food_kkal)
            iv_delete = itemView.findViewById(R.id.iv_delete)
        }

    }
}
