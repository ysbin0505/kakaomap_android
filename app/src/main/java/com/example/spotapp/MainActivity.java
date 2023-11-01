package com.example.spotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mapView = findViewById(R.id.map_view);
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }

        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
            }
        });
    }
}