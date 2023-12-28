package com.example.spotapp;

import android.location.Location;

import com.example.spotapp.client.ApiResponse;

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

    @GET("/locations")
    Call<ApiResponse<List<Location>>> getLocations(
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude
    );

}
