package com.example.spotapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LabelDBContext extends AppCompatActivity {
    private static final String BASE_URL = "http://210.123.182.64:8080/";
    private RetrofitService retrofitService;
    private double latitude;
    private double longitude;
    private ImageView profileImageView;
    private TextView titleTextView;
    private TextView discription;
    private TextView date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.label_db);
    }
}
