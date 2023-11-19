package com.example.ssainfe_real;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class FireStoreActivity extends AppCompatActivity {

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_store);

        initializeCloudFirestore();

        addData();

    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }

    private void addData() {
        CollectionReference cities = db.collection("members");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "woohyuk");
        data1.put("latitude", 37.4944064);
        data1.put("longitude", 126.9599747);
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




    }


}