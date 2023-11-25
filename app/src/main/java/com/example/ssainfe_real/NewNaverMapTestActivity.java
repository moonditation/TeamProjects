package com.example.ssainfe_real;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.Map;

public class NewNaverMapTestActivity extends FragmentActivity implements OnMapReadyCallback {
    Marker marker = new Marker();
    Marker marker2 = new Marker();

    InfoWindow infoWindow = new InfoWindow();
    InfoWindow infoWindow2 = new InfoWindow();
    private FirebaseFirestore db;



    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_test);

//        initializeCloudFirestore();
//        getAndSendDocument();

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);




    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
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

                        marker.setPosition(new LatLng(latitude,longitude));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);

        marker.setMap(naverMap);
        infoWindow.open(marker);

        marker2.setPosition(new LatLng(37.4944064, 126.9599747));
        marker2.setMap(naverMap);
        infoWindow2.open(marker2);

        naverMap.setOnMapClickListener((coord, point) -> {
            infoWindow.close();
        });

        // 마커를 클릭하면:
        Overlay.OnClickListener listener = overlay -> {
            Marker marker = (Marker) overlay;

            if (marker.getInfoWindow() == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker);
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }

            return true;
        };

        marker2.setOnClickListener(listener);
    }

}