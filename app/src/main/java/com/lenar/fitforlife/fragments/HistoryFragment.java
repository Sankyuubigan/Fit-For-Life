package com.lenar.fitforlife.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lenar.fitforlife.FitForLifeApplication;
import com.lenar.fitforlife.R;
import com.lenar.fitforlife.adapters.FoodAdapter;
import com.lenar.fitforlife.models.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HistoryFragment extends Fragment {

    private TextView tv_description;
    private LinearLayout base_container;
    private ProgressBar progress_bar;
    private String androidID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        androidID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        base_container = v.findViewById(R.id.base_container);
        tv_description = v.findViewById(R.id.tv_description);
        progress_bar = v.findViewById(R.id.progress_bar);
        updateInfo();
        return v;
    }

    private void updateInfo() {
        progress_bar.setVisibility(View.VISIBLE);
        base_container.removeAllViews();
//        FirebaseDatabase.getInstance().getReference("calories_burned").child(androidID).child(last_date).child(String.valueOf(mSteps)).setValue(String.valueOf(mCalories));
        FirebaseDatabase.getInstance().getReference("users_history").child(androidID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        showFood((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void showFood(Map<String, Object> singleUser) {

//        ArrayList<Long> phoneNumbers = new ArrayList<>();
        if (singleUser == null) {
            tv_description.setVisibility(View.VISIBLE);
            FitForLifeApplication.initAnimation(tv_description);
            progress_bar.setVisibility(View.GONE);
            return;
        }
        //iterate through each user, ignoring their UID
//        for (Map.Entry<String, Object> entry : users.entrySet()){

//        Map<String, Object> singleUser = (Map) users.get(androidID);
        //Get user map
//            Map singleUser = (Map) entry.getValue();

        Map<String, Object> sortSingleUser = new TreeMap<String, Object>(singleUser);
//        Collections.sort(singleUser, new Comparator<String,Object>() {
//            @Override
//            public int compare(String s, String t1) {
//                return 0;
//            }
//        });

        for (final Map.Entry<String, Object> date : sortSingleUser.entrySet()) {
            Object calories_collected, calories_burned;
            calories_collected = ((Map) date.getValue()).get("calories_collected");
            calories_burned = ((Map) date.getValue()).get("calories_burned");

            if (calories_collected == null && calories_burned == null)
                continue;

            Map<String, String> food = (Map) calories_collected;

            List<String> keySet;
            List<String> values;
            if (food == null) {
                food = new TreeMap<>();
                keySet = new ArrayList<>();
                values = new ArrayList<>();
            } else {
                keySet = new ArrayList<>(food.keySet());
                values = new ArrayList<>(food.values());
            }

            //Get phone field and append to list
            ArrayList<Food> mData = new ArrayList<>();
            double resultKkal = 0;
            for (int i = 0; i < food.size(); i++) {
                double kkal = Double.valueOf(values.get(i).split(" ")[0]);
                mData.add(new Food(keySet.get(i), kkal));
                resultKkal += kkal;
            }
//        }
            final FoodAdapter foodAdapter = new FoodAdapter(mData, getActivity());
//            foodAdapter.setNotShowBtnDelete(true);
            foodAdapter.setOnDeleteListener(new FoodAdapter.OnDeleteListener() {
                @Override
                public void onDelete(String name) {
                    foodAdapter.notifyDataSetChanged();
//                    final String date = DateFormat.format("dd MMMM", Calendar.getInstance()).toString();
                    FirebaseDatabase.getInstance().getReference("users_history").child(androidID).child(date.getKey()).child("calories_collected").child(name).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    updateInfo();
                                }
                            });
                }
            });
            RecyclerView recyclerView = new RecyclerView(getActivity());
            recyclerView.setAdapter(foodAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            TextView textDate = new TextView(getActivity());
            textDate.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textDate.setText(date.getKey());
            textDate.setPadding(0, 10, 0, 10);
            textDate.setGravity(Gravity.CENTER);
            textDate.setTypeface(Typeface.DEFAULT_BOLD);

            base_container.addView(textDate);

            if (resultKkal != 0) {
                String stringDescription = "Набрано: " + Math.round(resultKkal * 100.0) / 100.0 + " ккал";

                TextView textDescription = new TextView(getActivity());
                textDescription.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textDescription.setText(stringDescription);
                textDescription.setPadding(0, 10, 0, 10);
                textDescription.setGravity(Gravity.CENTER);
                textDescription.setTextColor(getResources().getColor(R.color.colorPrimary));
                textDescription.setTypeface(Typeface.DEFAULT_BOLD);
                base_container.addView(textDescription);
            }
            base_container.addView(recyclerView);
            if (calories_burned != null) {
                final Map<String, String> burned = (Map) calories_burned;
                String stringDescription = "Сожжено: " + burned.values() + " ккал. Шагов: " + burned.keySet();

                TextView textDescription = new TextView(getActivity());
                textDescription.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textDescription.setText(stringDescription);
                textDescription.setPadding(0, 10, 0, 10);
                textDescription.setGravity(Gravity.CENTER);
                textDescription.setTextColor(getResources().getColor(R.color.colorAccent));
                textDescription.setTypeface(Typeface.DEFAULT_BOLD);
                base_container.addView(textDescription);
            }


        }
        progress_bar.setVisibility(View.GONE);
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}
