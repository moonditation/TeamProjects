package com.example.myapplication.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class NaverMapShowPromisePlace extends AppCompatActivity implements OnMapReadyCallback{

    private Button updateButton;
    private FirebaseFirestore db;
    private NaverMap naverMap;
    EditText editText;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_show_promise_place);

        db = FirebaseFirestore.getInstance();

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync( this);




        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // 위치 권한이 허용되어 있으면 현재 위치 표시
        naverMap.getUiSettings().setLocationButtonEnabled(true);

        double latitude = getIntent().getDoubleExtra("latitude",0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);

        // 카메라를 지정된 위치로 이동
        LatLng searchLocation = new LatLng(latitude,longitude );
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(searchLocation).animate(CameraAnimation.Linear);
        naverMap.moveCamera(cameraUpdate);

        // 마커 생성
        Marker marker = new Marker();
        marker.setPosition(searchLocation);
        marker.setMap(naverMap);
        marker.setCaptionText("목적지!");
        marker.setCaptionTextSize(25);
        marker.setCaptionAligns(Align.Top);
        marker.setCaptionOffset(30);
    }
}