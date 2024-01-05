package com.example.spotapp;


import com.example.spotapp.client.ApiResponse;
import com.example.spotapp.client.LocationData;
import com.example.spotapp.client.WeatherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    @POST("/api/user/{snsId}")
    Call<String> saveKakaoUserInfo(@Path("snsId") Long snsId,
                                   @Header("Authorization") String authorizationHeader,
                                   @Body String nickname);

    @POST("/locations/save")
    Call<Void> addLocation(@Body LocationData locationData);


    @GET("/locations")
    Call<List<LocationData>> getAllLocations();

    @GET("/locations/api")
    Call<ApiResponse<List<LocationData>>> getLocations(@Query("latitude") Double latitude, @Query("longitude") Double longitude);

    @GET("/locations/{locationId}")
    Call<LocationData> getLocationDetails(@Path("locationId") Long locationId);

    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String city,
            @Query("appid") String apiKey
    );

}
