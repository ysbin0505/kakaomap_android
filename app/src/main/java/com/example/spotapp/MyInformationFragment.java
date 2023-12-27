package com.example.spotapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.spotapp.client.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class MyInformationFragment extends Fragment {

    /*서버 통신*/
    private static final String BASE_URL = "http://192.168.0.48:8080/"; // "http://10.0.2.2:8080/"
    private RetrofitService retrofitService;
    private String oauthToken;

    private Long userId;
    /* 서버 통신*/

    private View loginButton, logoutButton;
    private Button button_settings;
    private Button button_account;

    public View getLoginButton(){
        return loginButton;
    }

    public View getLogoutButton(){
        return logoutButton;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_information, container, false);

        button_settings = view.findViewById(R.id.button_settings);
        button_account = view.findViewById(R.id.button_account);
        loginButton = view.findViewById(R.id.login);
        logoutButton = view.findViewById(R.id.logout);

        button_settings.setOnClickListener(new View.OnClickListener() { //고객센터
            @Override
            public void onClick(View view) {
                // 버튼 클릭 시 특정 URL로 이동하는 인텐트 생성
                String url = "https://open.kakao.com/o/sBanXdRf";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        button_account.setOnClickListener(new View.OnClickListener() {  //내 계정, 탈퇴하기 기능
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = new AccountFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        if (loginButton == null || logoutButton == null) {
            loginButton = view.findViewById(R.id.login);
            logoutButton = view.findViewById(R.id.logout);
        }

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null) {
                    System.out.println("oAuthToken : " + oAuthToken.getAccessToken());
                    oauthToken = oAuthToken.getAccessToken();
                    kakaoLogin();
                    updateKakaoLoginStatus(UserApiClient.getInstance().isKakaoTalkLoginAvailable(requireContext()));
                }
                if (throwable != null) {
                    // TBO
                }
                return null;
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(requireContext())) {
                    UserApiClient.getInstance().loginWithKakaoTalk(requireActivity(), callback);
                    kakaoLogin();
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(requireActivity(), callback);
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        // 로그아웃 후 UI 업데이트
                        updateKakaoLoginStatus(false);
                        return null;
                    }
                });
            }
        });
        return view;
    }

    private void updateKakaoLoginStatus(boolean isLoggedIn) {
        if (loginButton != null && logoutButton != null) {
            if (isLoggedIn) {
                loginButton.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);
            } else {
                loginButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.GONE);
            }
        }
    }


    private void kakaoLogin() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    /**
                     * 서버로 보내기
                     */
                    String kakaoUserName = user.getKakaoAccount().getProfile().getNickname();
                    //String kakaoUserProfile= user.getKakaoAccount().getProfile().getProfileImageUrl();
                    // 로그인이 성공했을 때만 서버로 데이터 보내기
                    userId = user.getId();
                    Client.setSns_id(userId);
                    System.out.println(kakaoUserName);
                    sendKakaoUserInfoToServer(kakaoUserName);

                    Log.i(TAG, "invoke : id = " + user.getId());
                    Log.i(TAG, "invoke : id = " + user.getKakaoAccount().getProfile());



                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);
                } else {
                    loginButton.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.GONE);
                }
                return Unit.INSTANCE;
            }
        });
    }


    private void sendKakaoUserInfoToServer(String kakaoUserName) {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).client(new OkHttpClient()).build();
        retrofitService = retrofit.create(RetrofitService.class);

        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(kakaoUserName);
        String authorizationHeader = oauthToken;
        System.out.println("제발나와라 : " + userId);
        Call<String> call = retrofitService.saveKakaoUserInfo(userId ,authorizationHeader, kakaoUserName);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Data sent successfully");
                } else {
                    Log.e("API", "Failed to send data");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //통신 실패 시 처리
                Log.e("API", "Network Error : " + t.getMessage());
            }
        });
    }
}