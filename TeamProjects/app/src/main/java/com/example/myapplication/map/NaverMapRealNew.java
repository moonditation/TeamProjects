package com.example.myapplication.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class NaverMapRealNew extends AppCompatActivity implements OnMapReadyCallback {

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

    private Map<String, Object> data = new HashMap<>();

    private List<Marker> markerList = new ArrayList<>();
    private List<InfoWindow> infoWindowList = new ArrayList<>();

    private List<InfoWindow> locationButtonList = new ArrayList<>();

    private List<CircleOverlay> safeCircleList = new ArrayList<>();


    private AppCompatButton updateButton;
    private AppCompatButton promisePlace;

    private ImageButton cancelButton;


    private Handler locationUpdateHandler = new Handler();
    private int locationUpdateInterval = 5000; // 5초 간격
    private CollectionReference friendCollectionRef;
    private String myUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_real_new);

        cancelButton = findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프래그먼트 2 종료 후 프래그먼트 1로 돌아가기
                finish();
            }
        });


        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        initializeCloudFirestore();
        mapFragment.getMapAsync(this);

        Log.d("collection", getIntent().getStringExtra("promiseUid"));
        friendCollectionRef= db.collection("promisesPractice").document(getIntent().getStringExtra("promiseUid")).collection("friends");
        friendCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    int numOfButtons = querySnapshot.size();
                    Log.d("collection", ""+numOfButtons);
                    makeButtons(numOfButtons);
                } else {
                    Log.d("collection", "쿼리 스냅샷이 null입니다.");
                }
            } else {
                Log.d("collection", "문서 가져오기 오류: ", task.getException());
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myUid = user.getUid();

        // 업데이트 버튼 클릭 시 처리할 코드
        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {
            getCollectionAndMakeMemberTest(0);
        });

        // 약속장소 버튼 클릭 시 처리할 코드
        promisePlace = findViewById(R.id.primise_location_move_button);
        promisePlace.setOnClickListener(v -> {
            getCollectionAndMakeMemberTest(1);
        });


        // fusedLocationClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 위치 업데이트 시작
        startLocationUpdates();
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        startEveryLocationUpdates();
    }


    private void initializeCloudFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    private void getCollectionAndMakeMemberTest(int codeNum) {
        friendCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                removeMarkersAndInfoWindows();
                removeSafeCircleZone();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();

                    String name = (String) data.get("friendName");
                    Double latitude = (Double) data.get("friendLatitude");
                    Double longitude = (Double) data.get("friendLongitude");

                    if (latitude != null && longitude != null) {
                        // 마커와 인포윈도우 추가
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
                    DocumentReference documentReference = db.collection("promisesPractice").document(getIntent().getStringExtra("promiseUid"));
                    documentReference.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot document = task1.getResult();
                            if (document.exists()) {
                                Double latitude = document.getDouble("promiseLatitude");
                                Double longitude = document.getDouble("promiseLongitude");

                                if (latitude != null && longitude != null) {
                                    LatLng firstLocation = new LatLng(latitude, longitude);
                                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(firstLocation).animate(CameraAnimation.Linear);
                                    naverMap.moveCamera(cameraUpdate);
                                } else {
                                    Log.d("Latitude", "Latitude 또는 Longitude 값이 null");
                                }
                            } else {
                                Log.d("Latitude", "해당 문서가 존재하지 않습니다");
                            }
                        } else {
                            Log.d("Latitude", "문서 가져오기 실패");
                        }
                    });
                }

                // 특정 위치에 원 추가
                if (!markerList.isEmpty()) {
                    DocumentReference documentReference = db.collection("promisesPractice").document(getIntent().getStringExtra("promiseUid"));

                    documentReference.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot document = task1.getResult();
                            if (document.exists()) {
                                Double latitude = document.getDouble("promiseLatitude");
                                Double longitude = document.getDouble("promiseLongitude");

                                if (latitude != null && longitude != null) {
                                    LatLng firstLocation = new LatLng(latitude, longitude);
                                    makeSafeCircleZone(firstLocation);
                                } else {
                                    Log.d("Latitude", "Latitude 또는 Longitude 값이 null입니다.");
                                }
                            } else {
                                Log.d("Latitude", "해당 문서가 존재하지 않습니다.");
                            }
                        } else {
                            Log.d("Latitude", "문서 가져오기 실패: ", task1.getException());
                        }
                    });
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

            //클릭하면 infoWindow 나옴
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
        circle.setCenter(latLng);
        circle.setRadius(300); // 원의 반지름 설정 (미터)
        circle.setColor(Color.argb(128, 255, 0, 0));
        circle.setMap(naverMap);

        safeCircleList.add(circle);

        // 클릭 시 원 안에 있는 마커를 찾아서 업데이트
        for (Marker marker : markerList) {
            if (circle.getBounds().contains(marker.getPosition())) {
                // 원 안에 해당 마커가 포함된 경우 Firestore 문서 업데이트
                updateInCircleFirestoreDocument(marker);
                break;
            }
        }

    }
    private void updateInCircleFirestoreDocument(Marker marker) {
        // Firestore 문서 업데이트 로직
        // marker를 기준으로 Firestore 문서를 찾아서 업데이트하는 코드를 작성합니다.
        // 예시: "members" 컬렉션에서 특정 조건을 만족하는 문서를 찾아 필드를 업데이트합니다.

        friendCollectionRef
                .whereEqualTo("friendLatitude", marker.getPosition().latitude)
                .whereEqualTo("friendLongitude", marker.getPosition().longitude)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 해당 문서의 필드 업데이트
                            if((boolean)document.get("friendArrive")==false){
                                document.getReference().update("friendArrive", true); // "arrive" 필드를 1로 업데이트

                                Timestamp timestamp = com.google.firebase.Timestamp.now();
                                document.getReference().update("friendArriveTime", timestamp);
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
        new Handler().postDelayed(this::getLocationAndUpload, 5000);
    }

    private void getLocationAndUpload() {
        // 위치 정보 가져오기
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
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
    ///////////////////////여기 해야
    private void uploadToFirestore(double latitude, double longitude) {
        // Firestore에 데이터 업로드
        Map<String, Object> data = new HashMap<>();
        data.put("friendLatitude", latitude);
        data.put("friendLongitude", longitude);


        // 사용자 UID와 같은 이름의 문서를 찾아 데이터 업데이트
        friendCollectionRef.whereEqualTo("friendUid", myUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // 사용자의 UID와 일치하는 문서를 찾았을 때 데이터 업데이트
                        documentSnapshot.getReference().update(data)
                                .addOnSuccessListener(aVoid ->
                                        Log.d(TAG, "DocumentSnapshot updated with ID: " + documentSnapshot.getId()))
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error updating document", e));
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error getting documents", e));
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
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // 액티비티가 종료될 때 위치 업데이트 작업 중지
//         stopLocationUpdates();
//    }
}