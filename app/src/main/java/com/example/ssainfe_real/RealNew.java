package com.example.ssainfe_real;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
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
    private static final int LOCATION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = "LocationUpdateActivity";
    private NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    double latitude, longitude;

    private Member[] saveMembers;
    private Map<String, Object> data = new HashMap<>();

    private List<Marker> markerList = new ArrayList<>();
    private List<InfoWindow> infoWindowList = new ArrayList<>();

    private List<InfoWindow> locationButtonList = new ArrayList<>();

    private List<CircleOverlay> safeCircleList = new ArrayList<>();


    private Button updateButton;
    private Button promisePlace;
    int numOfMembers = 3;

    private Handler locationUpdateHandler = new Handler();
    private int locationUpdateInterval = 5000; // 5초 간격

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

        CollectionReference collectionRef = db.collection("members");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    int numOfButtons = querySnapshot.size();
                    makeButtons(numOfButtons);
                } else {
                    Log.d(TAG, "쿼리 스냅샷이 null입니다.");
                }
            } else {
                Log.d(TAG, "문서 가져오기 오류: ", task.getException());
            }
        });


        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {
            // 업데이트 버튼 클릭 시 처리할 코드
            getCollectionAndMakeMemberTest(0);
        });

        promisePlace = findViewById(R.id.primise_location_move_button);
        promisePlace.setOnClickListener(v -> {
            getCollectionAndMakeMemberTest(1);
        });


        // fusedLocationClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        // 위치 업데이트 시작
        startLocationUpdates();
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        startEveryLocationUpdates();
    }


    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }


    private void getCollectionAndMakeMemberTest(int codeNum) {
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


                if (!markerList.isEmpty() && codeNum == 1) {
                    LatLng firstLocation = markerList.get(3).getPosition();
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(firstLocation).animate(CameraAnimation.Linear);
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

    private void makeButtons(int numOfButtons) {
        RelativeLayout layout = findViewById(R.id.activity_real_layout);
        int prevButtonId = R.id.update_button;

        for (int i = 0; i < numOfButtons; i++) {
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
                if (!markerList.isEmpty() && finalI < markerList.size()) {
                    LatLng location = markerList.get(finalI).getPosition();
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(location).animate(CameraAnimation.Linear);
                    naverMap.moveCamera(cameraUpdate);
                }
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

    private void makeSafeCircleZone(LatLng latLng) {
        CircleOverlay circle = new CircleOverlay();
        circle.setCenter(latLng); // 원의 중심 위치 설정
        circle.setRadius(300); // 원의 반지름 설정 (미터 단위)
        circle.setColor(Color.argb(128, 255, 0, 0)); // 원의 색상 설정
        circle.setMap(naverMap); // 네이버 지도에 원 추가

        safeCircleList.add(circle); // 리스트에 추가

//            // 클릭 시 원 안에 있는 마커를 찾아서 업데이트
            for (Marker marker : markerList) {
                if (circle.getBounds().contains(marker.getPosition())) {
                    // 원 안에 해당 마커가 포함된 경우 Firestore 문서 업데이트
                    updateInCircleFirestoreDocument(marker); // Firestore 업데이트 메서드 호출
                    break; // 하나의 마커만 업데이트하기 위해 반복문 종료
                }
            }

    }
    private void updateInCircleFirestoreDocument(Marker marker) {
        // Firestore 문서 업데이트 로직
        // marker를 기준으로 Firestore 문서를 찾아서 업데이트하는 코드를 작성합니다.
        // 예시: "members" 컬렉션에서 특정 조건을 만족하는 문서를 찾아 필드를 업데이트합니다.

        // 예시 코드:
        db.collection("members")
                .whereEqualTo("latitude", marker.getPosition().latitude)
                .whereEqualTo("longitude", marker.getPosition().longitude)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 해당 문서의 필드 업데이트
                            if((boolean)document.get("arrive")==false){
                                document.getReference().update("arrive", true); // "arrive" 필드를 1로 업데이트
                                document.getReference().update("arriveTime", System.currentTimeMillis()); // "arriveTime" 필드를 현재 시간으로 업데이트

                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }


    private void removeSafeCircleZone() {
        for (CircleOverlay circleOverlay : safeCircleList) {
            circleOverlay.setMap(null); // CircleOverlay 제거
        }
        safeCircleList.clear(); // 리스트 초기화
    }


    private Runnable locationUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            // 위치 업데이트 로직을 실행합니다.
            getCollectionAndMakeMemberTest(0); // 위치 업데이트 메서드 호출

            // 다음 위치 업데이트를 예약합니다.
            locationUpdateHandler.postDelayed(this, locationUpdateInterval);
        }
    };

    private void startEveryLocationUpdates() {
        // 일정 시간마다 위치 업데이트를 호출하는 Runnable을 실행합니다.
        locationUpdateHandler.postDelayed(locationUpdateRunnable, locationUpdateInterval);
    }

    private void stopLocationUpdates() {
        // 위치 업데이트를 중지합니다.
        locationUpdateHandler.removeCallbacks(locationUpdateRunnable);
    }

    private void startLocationUpdates() {
        Runnable locationUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                getLocationAndUpload();
                // 5초 후에 다시 호출
                delayedLocationUpdate();
            }
        };
        locationUpdateRunnable.run();
    }

    private void delayedLocationUpdate() {
        // 5초 후에 위치 업데이트 메소드 호출
        new android.os.Handler().postDelayed(this::getLocationAndUpload, 5000);
    }

    private void getLocationAndUpload() {
        // 위치 정보 가져오기
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            android.location.Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Firestore에 업로드
                            uploadToFirestore(latitude, longitude);
                        } else {
                            Log.e(TAG, "Failed to get location.");
                        }
                    }
                });
    }

    private void uploadToFirestore(double latitude, double longitude) {
        // Firestore에 데이터 업로드
        Map<String, Object> data = new HashMap<>();
        data.put("latitude", latitude);
        data.put("longitude", longitude);

        // "locations" 컬렉션에 새로운 문서로 데이터 추가
        db.collection("locations")
                .add(data)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error adding document", e));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        // NaverMap 객체에 위치 소스 설정
        naverMap.setLocationSource(locationSource);

        // 위치 추적 모드 설정 (원하는 경우)
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        // 사용자에게 위치 권한 요청
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // 위치 권한이 허용되어 있으면 현재 위치 표시
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);


        getCollectionAndMakeMemberTest(1);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 위치 업데이트 작업을 중지합니다.
//        stopLocationUpdates();
    }
}