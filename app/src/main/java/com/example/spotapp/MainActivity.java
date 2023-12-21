package com.example.spotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kakao.vectormap.LatLng;

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

public class MainActivity extends AppCompatActivity {

    Fragment homeFragment, spotFragment, settingFragment;
    BottomNavigationView bottomNavigationView;

    /*서버 통신*/
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private RetrofitService retrofitService;


    // 나도 이거 겪어봤는데 이거 폰이랑 니 스프링부트의 localhost가 달라서 안되는거임
    // 여기 안스의 가상 폰으로는 localhost 또는 10.0.2.2는 돌아감
    // 나중에 외부 서버 하면 그 서버 아이피로

    /* 로그인 관련 */

    private static final String TAG = "MainActivity";   //TAG
    private View loginButton, logoutButton; // 니가

    public View getLoginButton(){
        return loginButton;
    }

    public View getLogoutButton(){
        return logoutButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();   //하단바
        checkLocationPermission();  //위치정보
    }

    private void checkLocationPermission() {
        /* 위치 관련 코드들 */
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //위치 권한 확인
            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            // 위치 업데이트 요청이 올때마다 실행하는 함수
            @Override
            public void onLocationChanged(Location location) {
                // 새로운 위치 정보가 도착했을 때 호출됩니다.
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // 현재 latitude, longitude 갱신
                SpotFragment.setLatitude(latitude);
                SpotFragment.setLongitude(longitude);

                //curLabel의 위치 이동
                if(SpotFragment.getCurLabel() != null) // SpotFragment로 이동한 적이 없으면 curLabel이 null이라 예외 터짐 방지
                    SpotFragment.getCurLabel().moveTo(LatLng.from(latitude, longitude));

                // 위도와 경도를 사용하여 현재 위치를 처리.

                Log.i("MainActivity", "Latitude: " + latitude + ", Longitude: " + longitude);

            }

            // 다른 메서드들 (onStatusChanged, onProviderEnabled, onProviderDisabled)도 구현 가능
        };

        // 주기적인 위치 업데이트 등록
        try {
            // GPS로부터 위치 업데이트를 요청합니다.
            // 500ms마다 위치 업데이트 요청 보냄.
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        /* -- 위치 관련 코드들 끝 -- */

    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void initLayout() {
        /* 하단 바 레이아웃 관련 코드들 */
        homeFragment = new HomeFragment();
        spotFragment = new SpotFragment();
        settingFragment = new SettingFragment();
        switchFragment(homeFragment);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    switchFragment(homeFragment);
                    return true;
                } else if (itemId == R.id.spot) {
                    switchFragment(spotFragment);
                    return true;
                } else if (itemId == R.id.setting) {
                    switchFragment(settingFragment);
                    return true;
                }
                return false;
            }
        });
        /* 하단 바 레이아웃 관련 코드들 끝 */

    }

}