package com.example.ssainfe_real;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaverMapApplyLocationActivity extends FragmentActivity implements OnMapReadyCallback {



    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private FirebaseFirestore db;

    private Map<String, Object> data = new HashMap<>();
    private Marker[] saveMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_test);

        initializeCloudFirestore();

        saveMarkers = new Marker[100];

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
    private void getCollectionAndMakeMember() {
        CollectionReference collectionRef = db.collection("members");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                saveMarkers = new Marker[collectionRef.size()]; // 마커 배열의 크기를 설정

                int count = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    data = document.getData();
                    // 데이터가 올바르게 가져와지는지 확인하기 위해 로그 출력
                    Log.d(TAG, "Latitude: " + data.get("latitude") + ", Longitude: " + data.get("longitude"));

                    String name = (String) data.get("name");
                    // LatLng 데이터 가져오기
                    Double latitude = (Double) data.get("latitude");
                    Double longitude = (Double) data.get("longitude");

                    if (latitude != null && longitude != null) {
                        saveMarkers[count] = new Marker();
                        saveMarkers[count].setPosition(new LatLng( latitude, longitude));

                        InfoWindow infoWindow = new InfoWindow();
                        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {

                            @NonNull
                            @Override
                            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                                return "이름 : " + name + "\nlatitude : " + latitude + "\nlongitude : " + longitude;
                            }
                        });
                        setInfoWindowClickListener(saveMarkers[count], infoWindow);
                    } else {
                        Log.d(TAG, "잘못된 위경도 데이터: " + data);
                    }
                    count++;
                }
            } else {
                Log.d(TAG, "문서 가져오기 오류: ", task.getException());
            }
        });
    }


    private void setInfoWindowClickListener(Marker marker, InfoWindow infoWindow) {
        // 마커를 클릭하면 정보 창 열기/닫기
        marker.setOnClickListener(overlay -> {
            if (marker.getInfoWindow() == null) {
                infoWindow.open(marker); // 정보 창 열기
            } else {
                infoWindow.close(); // 정보 창 닫기
            }
            return true;
        });
    }


    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);

        for(Marker marker : saveMarkers){
            marker.setMap(naverMap);
        }


    }
}