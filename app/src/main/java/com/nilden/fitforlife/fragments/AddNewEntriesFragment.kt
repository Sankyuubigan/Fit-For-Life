package com.nilden.fitforlife.fragments

import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.nilden.fitforlife.R
import com.nilden.fitforlife.adapters.FoodAdapter
import com.nilden.fitforlife.models.Food
import com.nilden.fitforlife.utils.AnimationHelper
import kotlinx.android.synthetic.main.fragment_add_new_entries.*
import kotlinx.android.synthetic.main.fragment_add_new_entries.view.*
import java.util.*

class AddNewEntriesFragment : Fragment() {

    private lateinit var foodAdapter: FoodAdapter
    private var mData = ArrayList<Food>()

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_add_new_entries, container, false)


        foodAdapter = FoodAdapter(mData, activity!!)
        val layoutManager = LinearLayoutManager(activity)
        foodAdapter.setOnDeleteListener(object : FoodAdapter.OnDeleteListener {
            override fun onDelete(name: String) {
                updateInfo()
            }
        })
        rootView.rv_food.adapter = foodAdapter
        rootView.rv_food.layoutManager = layoutManager
        rootView.rv_food.setHasFixedSize(true)
        val androidID = Settings.Secure.getString(activity!!.contentResolver, Settings.Secure.ANDROID_ID)
        val date = DateFormat.format("dd MMMM", Calendar.getInstance()).toString()
        rootView.btn_save.setOnClickListener {
            if (!mData.isEmpty()) {
                for (food in mData)
                    FirebaseDatabase.getInstance().getReference("users_history").child(androidID).child(date).child("calories_collected").child(food.name).setValue(food.kkal.toString() + " ккал")
                mData.clear()
                updateInfo()
                Toast.makeText(activity, "Ваши записи успешно сохранены", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(activity, "Сначала добавьте записи в список", Toast.LENGTH_SHORT).show()
        }
        rootView.btn_add.setOnClickListener {
            if (et_name.text.isNotEmpty() && et_count.text.isNotEmpty()) {
                mData.add(Food(et_name.text.toString(), java.lang.Double.valueOf(et_count.text.toString())))
                updateInfo()
                et_count.setText("")
                et_name.setText("")
            } else
                Toast.makeText(activity, "Пустые поля", Toast.LENGTH_SHORT).show()
        }
        AnimationHelper.initAnimation(rootView.tv_description)
        return rootView
    }

    fun updateInfo() {
        foodAdapter.notifyDataSetChanged()
        var result = 0.0
        for (food in mData)
            result += food.kkal
        tv_result.text = "Итого: ${Math.round(result)} ккал"
    }

}
