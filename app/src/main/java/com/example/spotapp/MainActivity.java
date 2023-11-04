package com.example.spotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kakao.vectormap.MapView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    int nCurrentPermission = 0;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    private MapView mapView;
    private Button showMapButton;

    private static final String TAG = "MainActivity";   //TAG
    private View loginButton, logoutButton;
    private TextView nickname;
    private ImageView profileImage;
    private double cur_lat;
    private double cur_lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OnCheckPermission();        //위치정보 동의 여부

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  카카오 로그인   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

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

        loginButton.setOnClickListener(new View.OnClickListener() { //로그인
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
                        UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
                }   else {
                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback);

                }
            }

        });

        logoutButton.setOnClickListener(new View.OnClickListener() {    //로그아웃
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

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  카카오 로그인   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        
        mapView = findViewById(R.id.map_view);
        showMapButton = findViewById(R.id.show_map_button);

        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 길찾기 좌표
                double latitude = cur_lat;
                double longitude = cur_lon;

                // 지도를 열기 위한 URI 생성
                String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(PhotoSpot)";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        KakaoMapHelper.initializeMap(mapView);
    }

    private void OnCheckPermission() {  //위치정보 동의

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSIONS_REQUEST);
            }

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //마지막 위치 받아오기
            Location loc_Current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            cur_lat = loc_Current.getLatitude();
            cur_lon = loc_Current.getLongitude();

            // 현재 위치 정보를 KakaoMapHelper 클래스에 전달
            KakaoMapHelper.setCoordinates(cur_lat, cur_lon);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //동의 또는 거부에 따른 메세지
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }



    private void updateKakaoLoginUi(){  //로그인, 로그아웃에 따른 UI변경

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
