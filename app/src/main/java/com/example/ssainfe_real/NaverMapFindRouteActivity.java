package com.example.ssainfe_real;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NaverMapFindRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView textView;
    EditText editText;
    private List<Marker> markerList = new ArrayList<>();
    private NaverMap naverMap;

    private JSONArray pathArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map_find_route);
//        editText = findViewById(R.id.editStartText);

        textView = findViewById(R.id.textView);

//        FragmentManager fm = getSupportFragmentManager();
//        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
//        if (mapFragment == null) {
//            mapFragment = MapFragment.newInstance();
//            fm.beginTransaction().add(R.id.map, mapFragment).commit();
//        }
//        mapFragment.getMapAsync(this);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        String start = editText.getText().toString();
//                        String goal = editText.getText().toString();
                        String start = "126.7126113,37.4546141";
                        String goal = "126.9599747,37.495861";
                        requestGeocode(start, goal);
                    }
                }).start();
            }
        });

    }

    public void requestGeocode(String start, String goal) {
        try {
            BufferedReader bufferedReader;
            StringBuilder stringBuilder = new StringBuilder();

            String query = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=" + URLEncoder.encode(start, "UTF-8")+"&goal="+ URLEncoder.encode(goal, "UTF-8");
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

                int indexFirst;
                int indexLast;

                indexFirst = stringBuilder.indexOf("path");
                indexLast = stringBuilder.indexOf("section");
                String distance= stringBuilder.substring(indexFirst , indexLast);


                textView.setText(indexFirst +"   "+indexLast+"\n"+distance);
                Log.d("routeJson", distance);


//                textView.setText(stringBuilder.toString());




            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeMarkerWithPathArray(JSONArray pathArray){

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        for(Marker marker : markerList){
            marker.setMap(naverMap);
        }
    }


}