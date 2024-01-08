package com.example.spotapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.spotapp.client.LocationData;
import com.example.spotapp.client.WeatherResponse;
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

    private static final String API_KEY = "8b730f5acda8b3b1aa30e8beadfccc42";
    private RetrofitService retrofitService;
    private ImageView profileImageView;
    private TextView titleTextView;
    private TextView description;
    private TextView date;
    private Long locationId;
    private TextView weatherInfo;
    private ImageView weatherIcon;
    private Integer iconResourceId;
    private static double latitude;
    private static double longitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.label_db);

        profileImageView = findViewById(R.id.picture);
        titleTextView = findViewById(R.id.title);
        description = findViewById(R.id.description);
        //date = findViewById(R.id.date);
        weatherInfo = findViewById(R.id.weatherInfo);
        weatherIcon = findViewById(R.id.weatherIcon);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            locationId = extras.getLong("locationId");
            System.out.println("Location ID: " + locationId);

            fetchLocationDetails(locationId);
        }
        weather();
        redoButton();
        doneButton();
    }

    private void weather(){
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        retrofitService = retrofit.create(RetrofitService.class);

        Call<WeatherResponse> call = retrofitService.getWeather(latitude,longitude, API_KEY);
        System.out.println("latitude = " + latitude + " longitude = " + longitude);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    Log.d("API", "여기까진 OK");
                    if (weatherResponse != null) {
                        WeatherResponse.WeatherInfo[] weatherInfoArray = weatherResponse.getWeatherInfo();
                        if (weatherInfoArray != null && weatherInfoArray.length > 0) {
                            WeatherResponse.WeatherInfo weatherInfoItem = weatherInfoArray[0];
                            String condition = weatherInfoItem.getCondition();
                            String iconUrl = weatherInfoItem.getIconUrl();

                            weatherInfo.setText(condition);
                            setWeatherIcon(iconUrl);
                            Log.d("API", "Data sent successfully");
                        }
                    }
                } else {
                    Log.e("API", "Failed to receive data");
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("API", "Network Error : " + t.getMessage());
            }
        });
    }

    private void setWeatherIcon(String condition) {
        int sunnyResourceId = R.drawable.sunny;
        int rainnyResourceId = R.drawable.rainny;
        int cloudyResourceId = R.drawable.cloudy;
        int snowyResourceId = R.drawable.snowy;
        int defaultId = R.drawable.loading;

        // condition 값에 따라서 iconResourceId 설정
        switch (condition) {
            case "01d":
                iconResourceId = sunnyResourceId;
                break;
            case "02d":
                iconResourceId = cloudyResourceId;
                break;
            case "03d":
                iconResourceId = cloudyResourceId;
                break;
            case "04d":
                iconResourceId = cloudyResourceId;
                break;
            case "09d":
                iconResourceId = rainnyResourceId;
                break;
            case "13d":
                iconResourceId = snowyResourceId;
                break;
            default:
                iconResourceId = defaultId;
                break;
        }

        System.out.println("iconResourceId = " + iconResourceId);

        Glide.with(LabelDBContext.this)
                .load(iconResourceId)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Image load failed for condition: " + condition, e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "Image loaded successfully for condition: " + condition);
                        return false;
                    }
                })
                .into(weatherIcon);

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
                    // 추가: 위치 데이터로부터 위도와 경도 값을 가져와 설정
                    latitude = locationData.getLatitude();
                    longitude = locationData.getLongitude();
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
