package com.example.spotapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class LabelContext extends AppCompatActivity {

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lable_context);

        // Intent에서 위치 정보를 받아옴
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);

        // 로그에 위치 정보 출력
        Log.d("LabelContext", "Latitude: " + latitude + " Longitude " + longitude);
    }
}
