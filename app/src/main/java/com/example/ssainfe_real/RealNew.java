package com.example.ssainfe_real;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.ssainfe_real.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealNew extends AppCompatActivity implements OnMapReadyCallback {

    Marker marker = new Marker();
    Marker marker2 = new Marker();

    InfoWindow infoWindow = new InfoWindow();
    InfoWindow infoWindow2 = new InfoWindow();
    private FirebaseFirestore db;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    double latitude, longitude;

    private Member[] saveMembers;
    private Map<String, Object> data = new HashMap<>();

    private List<Marker> markerList = new ArrayList<>();
    private List<InfoWindow> infoWindowList = new ArrayList<>();

    private List<InfoWindow> locationButtonList = new ArrayList<>();

    private List<CircleOverlay> safeCircleList = new ArrayList<>();


    private Button updateButton;
    int numOfMembers=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_new);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        initializeCloudFirestore();
        mapFragment.getMapAsync(this);

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {
            // 업데이트 버튼 클릭 시 처리할 코드
            getCollectionAndMakeMemberTest();
        });
    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }


    private void getCollectionAndMakeMemberTest() {
        CollectionReference collectionRef = db.collection("members");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                removeMarkersAndInfoWindows();
                removeSafeCircleZone();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();

                    String name = (String) data.get("name");
                    Double latitude = (Double) data.get("latitude");
                    Double longitude = (Double) data.get("longitude");


                    if (latitude != null && longitude != null) {
                        Marker marker = new Marker();
                        marker.setPosition(new LatLng(latitude, longitude));
                        markerList.add(marker);

                        InfoWindow infoWindow = new InfoWindow();
                        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
                            @NonNull
                            @Override
                            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                                return "이름 : " + name + "\nlatitude : " + latitude + "\nlongitude : " + longitude;
                            }
                        });
                        infoWindowList.add(infoWindow);


                    } else {
                        Log.d(TAG, "잘못된 위경도 데이터: " + data);
                    }
                }
                // 모든 마커와 인포윈도우를 지도에 추가
                addMarkersToMap();
                if (!markerList.isEmpty()) {
                    LatLng firstLocation = markerList.get(3).getPosition();
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(firstLocation);
                    naverMap.moveCamera(cameraUpdate);
                }

                //특정 위치에 원 추가
                if (!markerList.isEmpty()) {
                    LatLng firstLocation = markerList.get(3).getPosition();
                    makeSafeCircleZone(firstLocation);
                }
            } else {
                Log.d(TAG, "문서 가져오기 오류: ", task.getException());
            }
        });
    }



    private void addMarkersToMap() {
        for (int i = 0; i < markerList.size(); i++) {
            Marker marker = markerList.get(i);
            InfoWindow infoWindow = infoWindowList.get(i);

            marker.setMap(naverMap);
            infoWindow.open(marker);

            // 각 마커에 클릭 이벤트 추가
            marker.setOnClickListener(overlay -> {
                if (marker.getInfoWindow() == null) {
                    infoWindow.open(marker);
                } else {
                    infoWindow.close();
                }
                return true;
            });
        }
    }

    //미완성, 화면에 document 개수만큼 버튼 만들어서 띄우는 용
    private void makeButtons() {
        RelativeLayout layout = findViewById(R.id.activity_real_layout);
        int prevButtonId = R.id.update_button;

        for (int i = 0; i < numOfMembers; i++) {
            Button memberButton = new Button(this);
            memberButton.setId(View.generateViewId());
            memberButton.setText("Member " + (i + 1));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            if (i > 0) {
                params.addRule(RelativeLayout.RIGHT_OF, prevButtonId);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT); // 첫 번째 버튼을 왼쪽에 정렬
            }

            memberButton.setLayoutParams(params);
            layout.addView(memberButton);

            prevButtonId = memberButton.getId(); // 다음 버튼을 오른쪽으로 배치하기 위해 ID 업데이트

            int finalI = i;
            memberButton.setOnClickListener(v -> {
                // Handle button click for each member
                // Example: updateLocationForMember(finalI);
            });
        }
    }

    private void removeMarkersAndInfoWindows() {
        for (Marker marker : markerList) {
            marker.setMap(null); // 마커 제거
        }

        markerList.clear(); // 리스트 초기화

        for (InfoWindow infoWindow : infoWindowList) {
            infoWindow.close(); // 인포윈도우 닫기
        }
        infoWindowList.clear(); // 리스트 초기화
    }

    private void makeSafeCircleZone(LatLng latLng){
        CircleOverlay circle = new CircleOverlay();
        circle.setCenter(latLng); // 원의 중심 위치 설정
        circle.setRadius(300); // 원의 반지름 설정 (미터 단위)
        circle.setColor(Color.argb(128, 255, 0, 0)); // 원의 색상 설정
        circle.setMap(naverMap); // 네이버 지도에 원 추가

        safeCircleList.add(circle); // 리스트에 추가

    }

    private void removeSafeCircleZone() {
        for (CircleOverlay circleOverlay : safeCircleList) {
            circleOverlay.setMap(null); // CircleOverlay 제거
        }
        safeCircleList.clear(); // 리스트 초기화
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);

        getCollectionAndMakeMemberTest();
        makeButtons();

    }
}