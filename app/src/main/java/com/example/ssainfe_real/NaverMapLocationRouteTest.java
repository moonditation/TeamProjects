package com.example.ssainfe_real;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolylineOverlay;

import java.util.ArrayList;
import java.util.List;

public class NaverMapLocationRouteTest extends AppCompatActivity implements OnMapReadyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_location_route_test);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        List<LatLng> path = new ArrayList<>();
        // 경로를 그리기 위한 좌표들을 리스트에 추가합니다. 예시로 좌표를 하드코딩하여 추가합니다.
        path.add(new LatLng(37.5670135, 126.9783740)); // 시작점
        path.add(new LatLng(37.5666812, 126.9789967));
        path.add(new LatLng(37.5663981, 126.9779067));
        path.add(new LatLng(37.5658823, 126.9781736)); // 도착점

        // PolylineOverlay를 생성하여 경로를 그립니다.
        PolylineOverlay polyline = new PolylineOverlay();
        polyline.setCoords(path);
        polyline.setColor(android.graphics.Color.RED);
        polyline.setMap(naverMap);

        // 시작점과 도착점에 마커를 추가합니다.
        Marker startMarker = new Marker();
        startMarker.setPosition(path.get(0));
        startMarker.setMap(naverMap);

        Marker endMarker = new Marker();
        endMarker.setPosition(path.get(path.size() - 1));
        endMarker.setMap(naverMap);

        // 지도를 시작점을 중심으로 이동합니다.
        naverMap.moveCamera(com.naver.maps.map.CameraUpdate.scrollTo(path.get(0)));
    }
}