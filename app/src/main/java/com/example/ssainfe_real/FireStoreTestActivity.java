package com.example.ssainfe_real;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FireStoreTestActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_store_test);

        initializeCloudFirestore();

        addData();

    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }

    private void addData() {
        CollectionReference cities = db.collection("testMembers");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "woohyuk");
        data1.put("latitude", 37.4944064);
        data1.put("longitude", 126.9599747);
        data1.put("arrive", false);
        cities.document("member1").set(data1);


        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "jongmin");
        data2.put("latitude", 37.495861);
        data2.put("longitude", 126.953991);
        cities.document("member2").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "gwanrock");
        data3.put("latitude", 37.4980428);
        data3.put("longitude", 126.9584456);
        cities.document("member3").set(data3);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "uijung");
        data4.put("latitude", 37.4987373);
        data4.put("longitude", 126.9624452);
        cities.document("member4").set(data4);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "junha");
        data5.put("latitude", 37.4987373);
        data5.put("longitude", 126.9624452);
        cities.document("member5").set(data5);
    }




}