package com.lenar.healthyfit.screens;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lenar.healthyfit.R;
import com.lenar.healthyfit.adapters.FoodAdapter;
import com.lenar.healthyfit.models.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private TextView tv_description;
    private LinearLayout base_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.tb_main);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
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


    }

    private void collectPhoneNumbers(Map<String, Object> users) {

//        ArrayList<Long> phoneNumbers = new ArrayList<>();
        if (users == null) {
            tv_description = findViewById(R.id.tv_description);
            tv_description.setText("Здесь будет выводиться история Ваших записей приёмов пищи. Вы ещё ни разу не добавляли записи.");
            return;
        }
        //iterate through each user, ignoring their UID
//        for (Map.Entry<String, Object> entry : users.entrySet()){
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, Object> singleUser = (Map) users.get(androidID);
        //Get user map
//            Map singleUser = (Map) entry.getValue();
        for (Map.Entry<String, Object> date : singleUser.entrySet()) {
            Map<String, String> food = (Map) date.getValue();
            List<String> keySet = new ArrayList<>(food.keySet());
            List<String> values = new ArrayList<>(food.values());
            //Get phone field and append to list
            ArrayList<Food> mData = new ArrayList<>();
            for (int i = 0; i < singleUser.size(); i++)
                mData.add(new Food(keySet.get(i), Double.valueOf(values.get(i))));
//        }
            FoodAdapter foodAdapter = new FoodAdapter(mData);
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setAdapter(foodAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            base_container = findViewById(R.id.base_container);
            TextView textView = new TextView(this);
            textView.setText(date.getKey());
            base_container.addView(textView);
            base_container.addView(recyclerView);
        }
    }
}
