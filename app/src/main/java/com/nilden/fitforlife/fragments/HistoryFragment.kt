package com.nilden.fitforlife.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nilden.fitforlife.R
import com.nilden.fitforlife.adapters.FoodAdapter
import com.nilden.fitforlife.models.Food
import com.nilden.fitforlife.utils.AnimationHelper
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var tv_description: TextView
    private lateinit var base_container: LinearLayout
    private lateinit var progress_bar: ProgressBar
    private lateinit var androidID: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_history, container, false)

        androidID = Settings.Secure.getString(activity!!.contentResolver, Settings.Secure.ANDROID_ID)
        base_container = v.findViewById(R.id.base_container)
        tv_description = v.findViewById(R.id.tv_description)
        progress_bar = v.findViewById(R.id.progress_bar)
        updateInfo()
        return v
    }

    private fun updateInfo() {
        progress_bar.visibility = View.VISIBLE
        base_container.removeAllViews()
        FirebaseDatabase.getInstance().getReference("users_history").child(androidID).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        //Get map of users in datasnapshot
                        showFood(dataSnapshot.value as Map<String, Any>?)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        //handle databaseError
                    }
                })
    }

    private fun addTextDescription(description: String, color: Int) {
        val textDescription = TextView(activity)
        textDescription.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textDescription.text = description
        textDescription.setPadding(0, 10, 0, 10)
        textDescription.gravity = Gravity.CENTER
        textDescription.setTextColor(resources.getColor(color))
        textDescription.typeface = Typeface.DEFAULT_BOLD
        base_container.addView(textDescription)
    }

    private fun showFood(singleUser: Map<String, Any>?) {

        if (singleUser == null) {
            tv_description.visibility = View.VISIBLE
            AnimationHelper.initAnimation(tv_description)
            progress_bar.visibility = View.GONE
            return
        }
        //sort array
        val sortSingleUser = TreeMap(singleUser)

        for ((key, value) in sortSingleUser) {
            val calories_collected: Any?
            val calories_burned: Any?
            calories_collected = (value as Map<*, *>)["calories_collected"]
            calories_burned = value["calories_burned"]

            if (calories_collected == null && calories_burned == null)
                continue

            var food: Map<String, String>? = calories_collected as Map<String, String>?

            val keySet: List<String>
            val values: List<String>
            if (food == null) {
                food = TreeMap()
                keySet = ArrayList()
                values = ArrayList()
            } else {
                keySet = ArrayList(food.keys)
                values = ArrayList(food.values)
            }

            //Get phone field and append to list
            val mData = ArrayList<Food>()
            var resultKkal = 0.0
            for (i in 0 until food.size) {
                val kkal = java.lang.Double.valueOf(values[i].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])!!
                mData.add(Food(keySet[i], kkal))
                resultKkal += kkal
            }
            //        }
            val foodAdapter = FoodAdapter(mData, activity!!)
            foodAdapter.setOnDeleteListener(object : FoodAdapter.OnDeleteListener {
                override fun onDelete(name: String) {
                    foodAdapter.notifyDataSetChanged()
                    FirebaseDatabase.getInstance().getReference("users_history").child(androidID).child(key).child("calories_collected").child(name).removeValue()
                            .addOnCompleteListener { updateInfo() }
                }
            })
            val recyclerView = RecyclerView(activity)
            recyclerView.adapter = foodAdapter
            recyclerView.layoutManager = LinearLayoutManager(activity)

            addTextDescription(key,android.R.color.darker_gray)

            if (resultKkal != 0.0) {
                val stringDescription = "Набрано: " + Math.round(resultKkal * 100.0) / 100.0 + " ккал"

                addTextDescription(stringDescription, R.color.colorPrimary)
            }
            base_container.addView(recyclerView)
            if (calories_burned != null) {
                val burned = calories_burned as Map<*, *>
                val stringDescription = "Сожжено: " + burned.values + " ккал. Шагов: " + burned.keys

                addTextDescription(stringDescription, R.color.colorAccent)
            }


        }
        progress_bar.visibility = View.GONE
    }
}
