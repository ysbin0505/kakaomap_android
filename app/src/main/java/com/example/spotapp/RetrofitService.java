package com.example.spotapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {
    @POST("/api/user/{snsId}")
    Call<String> saveKakaoUserInfo(@Path("snsId") Long snsId,
                                   @Header("Authorization") String authorizationHeader,
                                   @Body String nickname);
}
