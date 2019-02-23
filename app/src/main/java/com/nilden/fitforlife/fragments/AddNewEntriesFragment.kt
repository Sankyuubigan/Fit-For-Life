package com.nilden.fitforlife.fragments

import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.nilden.fitforlife.FitForLifeApplication
import com.nilden.fitforlife.R
import com.nilden.fitforlife.adapters.FoodAdapter
import com.nilden.fitforlife.models.Food
import java.util.*

class AddNewEntriesFragment : Fragment() {

    internal var mData = ArrayList<Food>()
    private var et_name: EditText? = null
    private var et_count: EditText? = null
    private var foodAdapter: FoodAdapter? = null
    private var tv_description: TextView? = null
    private var tv_result: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_add_new_entries, container, false)

        et_name = v.findViewById(R.id.et_name)
        et_count = v.findViewById(R.id.et_count)
        tv_result = v.findViewById(R.id.tv_result)
        val recyclerView = v.findViewById<RecyclerView>(R.id.rv_food)
        foodAdapter = FoodAdapter(mData, activity)
        val layoutManager = LinearLayoutManager(activity)
        foodAdapter!!.setOnDeleteListener(object: FoodAdapter.OnDeleteListener {
            override fun onDelete(name: String) {
                updateInfo()
            }
        })
        recyclerView.adapter = foodAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        val androidID = Settings.Secure.getString(activity!!.contentResolver, Settings.Secure.ANDROID_ID)
        val date = DateFormat.format("dd MMMM", Calendar.getInstance()).toString()
        v.findViewById<View>(R.id.btn_save).setOnClickListener {
            if (!mData.isEmpty()) {
                for (food in mData)
                    FirebaseDatabase.getInstance().getReference("users_history").child(androidID).child(date).child("calories_collected").child(food.name).setValue(food.kkal.toString() + " ккал")
                mData.clear()
                updateInfo()
                Toast.makeText(activity, "Ваши записи успешно сохранены", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(activity, "Сначала добавьте записи в список", Toast.LENGTH_SHORT).show()
        }
        v.findViewById<View>(R.id.btn_add).setOnClickListener {
            if (et_name!!.text.length != 0 && et_count!!.text.length != 0) {
                mData.add(Food(et_name!!.text.toString(), java.lang.Double.valueOf(et_count!!.text.toString())!!))
                updateInfo()
                et_count!!.setText("")
                et_name!!.setText("")
            } else
                Toast.makeText(activity, "Пустые поля", Toast.LENGTH_SHORT).show()
        }
        tv_description = v.findViewById(R.id.tv_description)
        FitForLifeApplication.initAnimation(tv_description!!)
        return v
    }

    fun updateInfo() {
        foodAdapter!!.notifyDataSetChanged()
        var result = 0.0
        for (food in mData)
            result += food.kkal
        tv_result!!.text = "Итого: " + Math.round(result) + " ккал"
    }

}
