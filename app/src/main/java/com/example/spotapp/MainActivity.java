package com.example.spotapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kakao.vectormap.MapView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private Button showMapButton;

    private static final String TAG = "MainActivity";   //TAG
    private View loginButton, logoutButton;
    private TextView nickname;
    private ImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login);
        logoutButton = findViewById(R.id.logout);
        nickname = findViewById(R.id.nickname);
        profileImage = findViewById(R.id.profile);

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null){
                    //TBO
                }
                if (throwable != null){
                    //TBO
                }
                updateKakaoLoginUi();
                return null;
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
                        UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
                }   else {
                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback);

                }
            }

        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });
            }
        });



        updateKakaoLoginUi();

        mapView = findViewById(R.id.map_view);
        showMapButton = findViewById(R.id.show_map_button);

        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 길찾기 좌표
                double latitude = 35.2800;
                double longitude = 128.4155;

                // 지도를 열기 위한 URI 생성
                String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(PhotoSpot)";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        KakaoMapHelper.initializeMap(mapView);
    }

    private void updateKakaoLoginUi(){

        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null){

                    Log.i(TAG, "invoke : id = " + user.getId());
                    Log.i(TAG, "invoke : id = " + user.getKakaoAccount().getProfile());



                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);

                }else {

                    loginButton.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.GONE);

                }
                return null;
            }
        });
    }
}
