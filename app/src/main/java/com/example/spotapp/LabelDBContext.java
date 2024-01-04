package com.example.spotapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotapp.client.LocationData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LabelDBContext extends AppCompatActivity {
    private static final String BASE_URL = "http://192.168.239.152:8080/"; //home "http://210.123.182.64:8080/"; // "http://10.0.2.2:8080/" virtual // "http://172.25.80.1:8080/"

    private RetrofitService retrofitService;
    private ImageView profileImageView;
    private TextView titleTextView;
    private TextView description;
    private TextView date;
    private Long locationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.label_db);

        profileImageView = findViewById(R.id.picture);
        titleTextView = findViewById(R.id.title);
        description = findViewById(R.id.description);
        //date = findViewById(R.id.date);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            locationId = extras.getLong("locationId");
            System.out.println("Location ID: " + locationId);

            fetchLocationDetails(locationId);
        }

        redoButton();
        doneButton();
    }

    private void fetchLocationDetails(Long locationId) {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(new OkHttpClient())
                .build();
        retrofitService = retrofit.create(RetrofitService.class);

        Call<LocationData> call = retrofitService.getLocationDetails(locationId);
        call.enqueue(new Callback<LocationData>() {
            @Override
            public void onResponse(Call<LocationData> call, Response<LocationData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LocationData locationData = response.body();
                    updateUI(locationData);
                    Log.e("LabelDBContext", "Success to fetch location details");
                } else {
                    Log.e("LabelDBContext", "Failed to fetch location details");
                }
            }

            @Override
            public void onFailure(Call<LocationData> call, Throwable t) {
                Log.e("LabelDBContext", "Error: " + t.getMessage());
            }
        });
    }

    private void updateUI(LocationData locationData) {
        titleTextView.setText(locationData.getTitle());
        description.setText(locationData.getDescription());
        //profileImageView.setImageURI(locationData.getImage());
    }

    private void doneButton() {
        Button doneButton = findViewById(R.id.done_button);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void redoButton() {
        FloatingActionButton redoButton = findViewById(R.id.redoButton);
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SpotFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
