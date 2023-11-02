package com.example.spotapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.kakao.vectormap.MapView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mapView = findViewById(R.id.map_view);
        KakaoMapHelper.initializeMap(mapView);
    }
}
