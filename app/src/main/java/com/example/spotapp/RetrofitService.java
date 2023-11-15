package com.example.spotapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitService {
    @POST("/api/kakao/save")
    Call<String> saveKakaoUserInfo(@Body KakaoUserInfo kakaoUserInfo);
}
