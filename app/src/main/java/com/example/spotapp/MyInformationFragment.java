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

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MyInformationFragment extends Fragment {

    private View loginButton, logoutButton; // 니가
    private Button button_settings;
    private Button button_account;

    public MyInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_information, container, false);

        button_settings = view.findViewById(R.id.button_settings);
        button_account = view.findViewById(R.id.button_account);
        loginButton = view.findViewById(R.id.login);
        logoutButton = view.findViewById(R.id.logout);

        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼 클릭 시 특정 URL로 이동하는 인텐트 생성
                String url = "https://open.kakao.com/o/sBanXdRf";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        button_account.setOnClickListener(new View.OnClickListener() {
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
                    // TBO
                }
                if (throwable != null) {
                    // TBO
                }
                // 카카오 로그인 상태에 따라 UI 업데이트
                updateKakaoLoginStatus(UserApiClient.getInstance().isKakaoTalkLoginAvailable(requireContext()));
                return null;
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(requireContext())) {
                    UserApiClient.getInstance().loginWithKakaoTalk(requireActivity(), callback);
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

        kakaoLogin();

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

}

