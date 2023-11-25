package com.example.ssainfe_real;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

public class NaverMapTestActivity extends FragmentActivity implements OnMapReadyCallback {
    Marker marker = new Marker();
    Marker marker2 = new Marker();

    InfoWindow infoWindow = new InfoWindow();
    InfoWindow infoWindow2 = new InfoWindow();


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_test);


        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        if (intent != null) {
            // 전달된 데이터의 키 값을 사용하여 데이터 추출
            String receivedName = intent.getStringExtra("NAME_KEY");
            double receivedLatitude = intent.getDoubleExtra("LATITUDE_KEY", 0d);
            double receivedLongitude = intent.getDoubleExtra("LONGITUDE_KEY", 0d);


            // 추출된 데이터를 사용하여 작업 수행
            if (receivedName != null) {
                marker.setPosition(new LatLng(receivedLatitude, receivedLongitude));

                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {

                    @NonNull
                    @Override
                    public CharSequence getText(@NonNull InfoWindow infoWindow) {
                        return "이름 : " + receivedName + "\nlatitude : " + receivedLatitude + "\nlongitude : " + receivedLongitude;
                    }
                });
            }
        } else {
            Log.d("RecievedWrong", "wrong");
        }

        infoWindow2.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return "여기에 사람 정보 알려줌2";
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

        marker.setOnClickListener(listener);
    }

}