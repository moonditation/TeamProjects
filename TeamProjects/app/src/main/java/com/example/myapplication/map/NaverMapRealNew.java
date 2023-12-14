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
    private int locationUpdateInterval = 5000;
    private CollectionReference friendCollectionRef;
    private String myUid;

    private DocumentReference myDocumentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_real_new);

        cancelButton = findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    List<String> friendNames = new ArrayList<>();

                    int numOfButtons = querySnapshot.size();
                    Log.d("collection", ""+numOfButtons);
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String friendName = document.getString("friendName");
                        Log.d("friendName bb",friendName+"");

                        if (friendName != null) {
                            friendNames.add(friendName);
                        }
                    }
                    makeButtons(numOfButtons, friendNames);

                } else {
                    Log.d("collection", "쿼리 스냅샷이 null입니다.");
                }
            } else {
                Log.d("collection", "문서 가져오기 오류: ", task.getException());
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myUid = user.getUid();

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {
            getCollectionAndMakeMemberTest(0);
        });

        promisePlace = findViewById(R.id.primise_location_move_button);
        promisePlace.setOnClickListener(v -> {
            getCollectionAndMakeMemberTest(1);
        });


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                        Marker marker = new Marker();
                        marker.setPosition(new LatLng(latitude, longitude));
                        markerList.add(marker);

                        InfoWindow infoWindow = new InfoWindow();
                        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
                            @NonNull
                            @Override
                            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                                return "이름 : " + name;
                            }
                        });
                        infoWindowList.add(infoWindow);
                    } else {
                        Log.d(TAG, "잘못된 위경도 데이터: " + data);
                    }
                }
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

    private void makeButtons(int numOfButtons, List<String> friendNames) {
        RelativeLayout layout = findViewById(R.id.activity_real_layout);
        int prevButtonId = R.id.update_button;

        for (int i = 0; i < numOfButtons; i++) {
            Button memberButton = new Button(this);
            memberButton.setId(View.generateViewId());
            if (i < friendNames.size()) {
                memberButton.setText(friendNames.get(i));
            } else {
                memberButton.setText("Friend " + (i + 1));
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            if (i > 0) {
                params.addRule(RelativeLayout.RIGHT_OF, prevButtonId);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

            memberButton.setLayoutParams(params);
            layout.addView(memberButton);

            prevButtonId = memberButton.getId();

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
            marker.setMap(null);
        }

        markerList.clear();

        for (InfoWindow infoWindow : infoWindowList) {
            infoWindow.close();
        }
        infoWindowList.clear();
    }

    private void makeSafeCircleZone(LatLng latLng) {
        CircleOverlay circle = new CircleOverlay();
        circle.setCenter(latLng);
        circle.setRadius(300);
        circle.setColor(Color.argb(128, 255, 0, 0));
        circle.setMap(naverMap);

        safeCircleList.add(circle);

        for (Marker marker : markerList) {
            if (circle.getBounds().contains(marker.getPosition())) {
                updateInCircleFirestoreDocument(marker);
                break;
            }
        }

    }
    private void updateInCircleFirestoreDocument(Marker marker) {

        friendCollectionRef
                .whereEqualTo("friendLatitude", marker.getPosition().latitude)
                .whereEqualTo("friendLongitude", marker.getPosition().longitude)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if((boolean)document.get("friendArrive")==false){
                                document.getReference().update("friendArrive", true);

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
            circleOverlay.setMap(null);
        }
        safeCircleList.clear();
    }


    private Runnable locationUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            getCollectionAndMakeMemberTest(0);

            locationUpdateHandler.postDelayed(this, locationUpdateInterval);
        }
    };

    private void startEveryLocationUpdates() {
        locationUpdateHandler.postDelayed(locationUpdateRunnable, locationUpdateInterval);
    }

    private void stopLocationUpdates() {
        locationUpdateHandler.removeCallbacks(locationUpdateRunnable);
    }

    private void startLocationUpdates() {
        Runnable locationUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                getLocationAndUpload();
                delayedLocationUpdate();
            }
        };
        locationUpdateRunnable.run();
    }

    private void delayedLocationUpdate() {
        new Handler().postDelayed(this::getLocationAndUpload, 5000);
    }

    private void getLocationAndUpload() {
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

                            uploadToFirestore(latitude, longitude);
                        } else {
                            Log.e(TAG, "Failed to get location.");
                        }
                    }
                });
    }
    ///////////////////////여기 해야
    private void uploadToFirestore(double latitude, double longitude) {
        Map<String, Object> data = new HashMap<>();
        data.put("friendLatitude", latitude);
        data.put("friendLongitude", longitude);


        friendCollectionRef.whereEqualTo("friendUid", myUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
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
            if (!locationSource.isActivated()) {
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
        naverMap.setLocationSource(locationSource);

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

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