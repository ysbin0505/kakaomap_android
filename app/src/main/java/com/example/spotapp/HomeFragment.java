package com.example.spotapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        hiddenKaKao();

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void hiddenKaKao() {
        // 카카오 로그인 버튼 숨기기
        MainActivity mainActivity = (MainActivity) getActivity();
        View loginButton = mainActivity.getLoginButton();
        View logoutButton = mainActivity.getLogoutButton();

        if (loginButton != null) {
            loginButton.setVisibility(View.GONE);
        }
        if (logoutButton != null) {
            logoutButton.setVisibility(View.GONE);
        }
    }
}
