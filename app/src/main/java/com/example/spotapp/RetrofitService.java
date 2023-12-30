package com.example.spotapp;

import android.location.Location;
import android.location.LocationRequest;

import com.example.spotapp.client.ApiResponse;
import com.example.spotapp.client.LocationData;

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

    @GET("/locations") // 실제 API 엔드포인트에 따라 수정
    Call<List<LocationData>> getLocations();


}
