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
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kakao.vectormap.LatLng;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    Fragment homeFragment, spotFragment, settingFragment;
    BottomNavigationView bottomNavigationView;

    /* 로그인 관련 */

    private static final String TAG = "MainActivity";   //TAG
    private View loginButton, logoutButton;

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
        updateKakaoLoginUi();   //카카오 로그인UI
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
            }

            // 다른 메서드들 (onStatusChanged, onProviderEnabled, onProviderDisabled)도 구현 가능합니다.
        };

        // 주기적인 위치 업데이트 등록
        try {
            // GPS로부터 위치 업데이트를 요청합니다.
            // 500ms마다 위치 업데이트 요청 보냄.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        /* -- 위치 관련 코드들 끝 -- */

    }


    private void updateKakaoLoginUi() {

        if (loginButton == null || logoutButton == null) {
            loginButton = findViewById(R.id.login);
            logoutButton = findViewById(R.id.logout);
        }

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null) {
                    // TBO
                }
                if (throwable != null) {
                    // TBO
                }
                // 카카오 로그인 상태에 따라 UI 업데이트
                updateKakaoLoginStatus(UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this));
                return null;
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
                } else {
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
                        // 로그아웃 후 UI 업데이트
                        updateKakaoLoginStatus(false);
                        return null;
                    }
                });
            }
        });

        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                // 사용자 정보 받아오기 성공 시 UI 업데이트
                if (user != null) {
                    Log.i(TAG, "invoke : id = " + user.getId());
                    Log.i(TAG, "invoke : id = " + user.getKakaoAccount().getProfile());
                    updateKakaoLoginStatus(true);
                } else {
                    // 사용자 정보 받아오기 실패 시 UI 업데이트
                    updateKakaoLoginStatus(false);
                }
                return null;
            }
        });
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