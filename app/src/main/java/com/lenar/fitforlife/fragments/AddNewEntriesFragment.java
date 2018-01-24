package com.lenar.fitforlife.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.lenar.fitforlife.FitForLifeApplication;
import com.lenar.fitforlife.R;
import com.lenar.fitforlife.adapters.FoodAdapter;
import com.lenar.fitforlife.models.Food;

import java.util.ArrayList;
import java.util.Calendar;

public class AddNewEntriesFragment extends Fragment {

    ArrayList<Food> mData = new ArrayList<>();
    private EditText et_name;
    private EditText et_count;
    private FoodAdapter foodAdapter;
    private TextView tv_description;
    private TextView tv_result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_new_entries, container, false);

        et_name = v.findViewById(R.id.et_name);
        et_count = v.findViewById(R.id.et_count);
        tv_result = v.findViewById(R.id.tv_result);
        RecyclerView recyclerView = v.findViewById(R.id.rv_food);
        foodAdapter = new FoodAdapter(mData,getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        foodAdapter.setOnDeleteListener(new FoodAdapter.OnDeleteListener() {
            @Override
            public void onDelete(String name) {
                updateInfo();
            }
        });
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        final String androidID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        final String date = DateFormat.format("dd MMMM", Calendar.getInstance()).toString();
        v.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mData.isEmpty()) {
                    for (Food food : mData)
                        FirebaseDatabase.getInstance().getReference("users_history").child(androidID).child(date).child(food.getName()).setValue(food.getKkal() + " ккал");
                    mData.clear();
                    updateInfo();
                    Toast.makeText(getActivity(), "Ваши записи успешно сохранены", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "Сначала добавьте записи в список", Toast.LENGTH_SHORT).show();
            }
        });
        v.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_name.getText().length() != 0 && et_count.getText().length() != 0) {
                    mData.add(new Food(et_name.getText().toString(), Double.valueOf(et_count.getText().toString())));
                    updateInfo();
                    et_count.setText("");
                    et_name.setText("");
                } else Toast.makeText(getActivity(), "Пустые поля", Toast.LENGTH_SHORT).show();
            }
        });
        tv_description = v.findViewById(R.id.tv_description);
        FitForLifeApplication.initAnimation(tv_description);
        return v;
    }

    public void updateInfo() {
        foodAdapter.notifyDataSetChanged();
        double result = 0;
        for (Food food : mData)
            result += food.getKkal();
        tv_result.setText("Итого: " + Math.round(result) + " ккал");
    }

}
