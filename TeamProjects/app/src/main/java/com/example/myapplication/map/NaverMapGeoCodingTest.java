package com.example.myapplication.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NaverMapGeoCodingTest extends AppCompatActivity implements OnMapReadyCallback {

    private AppCompatButton updateButton;
    private FirebaseFirestore db;
    private NaverMap naverMap;
    EditText editText;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_geo_coding_test);

        db = FirebaseFirestore.getInstance();
        editText = findViewById(R.id.editText);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(view -> updateCoordinatesToFirestore());
        AppCompatButton button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String address = editText.getText().toString();
                        requestGeocode(address);
                    }
                }).start();
            }
        });

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
                double latitude = centerLatLng.latitude;
                double longitude = centerLatLng.longitude;
                Log.d("woohyuk", latitude+""+longitude);

                Intent intent = new Intent();
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
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

    public void requestGeocode(String address) {
        try {
            BufferedReader bufferedReader;
            StringBuilder stringBuilder = new StringBuilder();
//            String addr = "서울특별시 동작구 상도로 369";
            String query = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + URLEncoder.encode(address, "UTF-8");
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "uilhyth3w8");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", "nTSWLMxsdhTtTSBbuVviiRWQX7ntNeK6gcym9oZB");
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                //textView.setText(stringBuilder);

                int indexFirst;
                int indexLast;

                indexFirst = stringBuilder.indexOf("\"x\":\"");
                indexLast = stringBuilder.indexOf("\",\"y\":");
                String x = stringBuilder.substring(indexFirst + 5, indexLast);

                indexFirst = stringBuilder.indexOf("\"y\":\"");
                indexLast = stringBuilder.indexOf("\",\"distance\":");
                String y = stringBuilder.substring(indexFirst + 5, indexLast);


                // 위도, 경도 값을 가져온 후 double로 변환
                double latitude = Double.parseDouble(y);
                double longitude = Double.parseDouble(x);

                LatLng searchLocation = new LatLng(latitude, longitude) ;
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(searchLocation).animate(CameraAnimation.Linear);
                naverMap.moveCamera(cameraUpdate);

                bufferedReader.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }
}

