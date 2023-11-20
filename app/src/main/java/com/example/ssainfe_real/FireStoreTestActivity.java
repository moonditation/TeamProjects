package com.example.ssainfe_real;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
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

        getAndSendDocument();
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

    private void getAndSendDocument() {
        CollectionReference collectionRef = db.collection("members");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int documentCount = task.getResult().size();



                    DocumentReference docRef = collectionRef.document("member1");

                    DocumentSnapshot document = task.getResult().getDocuments().get(1);
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG, "DocumentSnapshot data: " + data);

                        String name = (String) data.get("name");
                        double latitude = (double) data.get("latitude");
                        double longitude = (double) data.get("longitude");
                        Log.d(TAG, "name = " + name);

                        Intent intent = new Intent(FireStoreTestActivity.this, NaverMapTestActivity.class);

                        intent.putExtra("NAME_KEY", name);
                        intent.putExtra("LATITUDE_KEY", latitude);
                        intent.putExtra("LONGITUDE_KEY", longitude);
                        startActivity(intent);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                }  else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }



}