package com.example.spotapp;

import android.app.Application;
import android.view.View;

import com.bumptech.glide.Glide;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

public class KakaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Kakao SDK 초기화
        KakaoSdk.init(this, "c48b3a64e5508a8c1c6c4ff591dfa478");
    }

}

