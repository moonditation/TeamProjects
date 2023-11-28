package com.example.ssainfe_real;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.HashMap;
import java.util.Map;

public class NaverMapGeoCodingTest extends AppCompatActivity implements OnMapReadyCallback {

    private Button updateButton;
    private FirebaseFirestore db;
    private NaverMap naverMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_geo_coding_test);

        db = FirebaseFirestore.getInstance();

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(view -> updateCoordinatesToFirestore());


        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void updateCoordinatesToFirestore() {
        if (naverMap != null) {
            android.graphics.Point centerPoint = new android.graphics.Point(
                    naverMap.getWidth() / 2, naverMap.getHeight() / 2);

            PointF centerPointF = new PointF(centerPoint.x, centerPoint.y);
            LatLng centerLatLng = naverMap.getProjection().fromScreenLocation(centerPointF);

            if (centerLatLng != null) {
                // Now you have the coordinates of the center of the map
                // Upload this coordinate to Firestore or perform necessary operations here
                uploadCoordinatesToFirestore(centerLatLng);
            }
        }
    }

    private void uploadCoordinatesToFirestore(LatLng coordinates) {
        // Upload the coordinates to Firestore
        // For example:
        Map<String, Object> data = new HashMap<>();
        data.put("latitude", coordinates.latitude);
        data.put("longitude", coordinates.longitude);

        db.collection("coordinates")
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Coordinates added"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding coordinates", e));
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
        naverMap.setLocationSource(locationSource);
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
    }
}

