package com.lenar.healthyfit.fragments;

import android.os.Bundle;
import android.provider.Settings;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lenar.healthyfit.FitForLifeApplication;
import com.lenar.healthyfit.R;
import com.lenar.healthyfit.adapters.FoodAdapter;
import com.lenar.healthyfit.models.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private TextView tv_description;
    private LinearLayout base_container;
    private ProgressBar progress_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        FirebaseDatabase.getInstance().getReference("users_history").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        base_container = v.findViewById(R.id.base_container);
        tv_description = v.findViewById(R.id.tv_description);
        progress_bar = v.findViewById(R.id.progress_bar);
        return v;
    }

    private void collectPhoneNumbers(Map<String, Object> users) {

//        ArrayList<Long> phoneNumbers = new ArrayList<>();
        if (users == null) {
            tv_description.setVisibility(View.VISIBLE);
            FitForLifeApplication.initAnimation(tv_description);
            progress_bar.setVisibility(View.GONE);
            return;
        }
        //iterate through each user, ignoring their UID
//        for (Map.Entry<String, Object> entry : users.entrySet()){

        String androidID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, Object> singleUser = (Map) users.get(androidID);
        //Get user map
//            Map singleUser = (Map) entry.getValue();
        for (Map.Entry<String, Object> date : singleUser.entrySet()) {
            Map<String, String> food = (Map) date.getValue();
            List<String> keySet = new ArrayList<>(food.keySet());
            List<String> values = new ArrayList<>(food.values());
            //Get phone field and append to list
            ArrayList<Food> mData = new ArrayList<>();
            double resultKkal = 0;
            for (int i = 0; i < food.size(); i++) {
                double kkal = Double.valueOf(values.get(i).split(" ")[0]);
                mData.add(new Food(keySet.get(i), kkal));
                resultKkal += kkal;
            }
//        }
            FoodAdapter foodAdapter = new FoodAdapter(mData);
            foodAdapter.setNotShowBtnDelete(true);
            RecyclerView recyclerView = new RecyclerView(getActivity());
            recyclerView.setAdapter(foodAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(date.getKey() + " - " + Math.round(resultKkal) + " ккал");
            textView.setPadding(0, 10, 0, 10);
            textView.setGravity(Gravity.CENTER);
            base_container.addView(textView);
            base_container.addView(recyclerView);
        }
        progress_bar.setVisibility(View.GONE);
    }
}
