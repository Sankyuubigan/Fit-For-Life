package com.lenar.healthyfit.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.lenar.healthyfit.R;
import com.lenar.healthyfit.adapters.FoodAdapter;
import com.lenar.healthyfit.models.Food;
import com.lenar.healthyfit.screens.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AddNewEntriesFragment extends Fragment {

    ArrayList<Food> mData = new ArrayList<>();
    private EditText et_name;
    private EditText et_count;
    private FoodAdapter foodAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_new_entries, container, false);
        // Inflate the layout for this fragment
        et_name = v.findViewById(R.id.et_name);
        et_count = v.findViewById(R.id.et_count);
        initViews();
        return v;
    }

    private void initViews() {


        RecyclerView recyclerView = findViewById(R.id.rv_food);
        foodAdapter = new FoodAdapter(mData);
//        citiesAdapter.setOnCityClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        final String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final String date = DateFormat.format("MMMM dd yyyy", Calendar.getInstance()).toString();
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("users_history").child(androidID).child(date).child("Булочка").setValue("100500 Ккал");
            }
        });
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_name.getText().length() != 0 && et_count.getText().length() != 0) {
                    mData.add(new Food(et_name.getText().toString(), Double.valueOf(et_count.getText().toString())));
                    foodAdapter.notifyDataSetChanged();
                } else Toast.makeText(MainActivity.this, "Пустые поля", Toast.LENGTH_SHORT).show();
            }
        });
        initAnimation();
    }

    public void initAnimation() {
        TextView tv_description = findViewById(R.id.tv_description);
        ScaleAnimation animationScale = new ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f, ScaleAnimation.RELATIVE_TO_SELF, 0.51f, ScaleAnimation.RELATIVE_TO_SELF, 0.52f);
        animationScale.setDuration(5000);
        animationScale.setRepeatCount(Animation.INFINITE);
        animationScale.setInterpolator(new CycleInterpolator(1));
        RotateAnimation animationRotate = new RotateAnimation(0, 3, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animationRotate.setDuration(800);
        animationRotate.setInterpolator(new CycleInterpolator(1));
        animationRotate.setRepeatCount(Animation.INFINITE);
        final AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(animationRotate);
        animationSet.addAnimation(animationScale);
//        animationSet.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                animationSet.start();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        animationSet.setDuration(3000);
        animationSet.setRepeatCount(Animation.INFINITE);
//        animationSet.setRepeatMode(Animation.REVERSE);
        tv_description.startAnimation(animationSet);
    }

}
