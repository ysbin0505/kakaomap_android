package com.example.spotapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.sdk.user.UserApiClient;

//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SettingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageView profileImageView;
    private TextView nicknameTextView;

    public ImageView getProfileImageView() {
        return profileImageView;
    }

    public void setProfileImageView(ImageView profileImageView) {
        this.profileImageView = profileImageView;
    }

    public TextView getNicknameTextView() {
        return nicknameTextView;
    }

    public void setNicknameTextView(TextView nicknameTextView) {
        this.nicknameTextView = nicknameTextView;
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        profileImageView = view.findViewById(R.id.profileImageView);
        nicknameTextView = view.findViewById(R.id.nicknameTextView);

        // 카카오톡 로그인 후 사용자 정보 가져오기
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                // 사용자 정보를 가져오는 도중 에러가 발생한 경우 처리
                // 에러 처리 로직 추가하기 (예: 토스트 메시지를 통한 사용자에게 알림)
                return null;
            }
            if (user != null) {
                // 사용자 정보를 성공적으로 가져온 경우 프로필 이미지와 닉네임 업데이트
                String nickname = user.getKakaoAccount().getProfile().getNickname();
                String profileImageUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();

                // 닉네임 설정
                nicknameTextView.setText(nickname);

                // 프로필 이미지 설정
                Glide.with(this)
                        .load(profileImageUrl)
                        .circleCrop() // 원형 모양으로 이미지 보여주기
                        .placeholder(R.drawable.loading) // 로딩 중에 보여질 이미지 설정
                        .error(R.drawable.before_login) // 이미지 로딩 실패시 보여질 이미지 설정
                        .into(profileImageView);

                // 로그인 상태일 때 보여질 뷰들을 표시
                profileImageView.setVisibility(View.VISIBLE);
                nicknameTextView.setVisibility(View.VISIBLE);
            } else {
                // 로그아웃 상태일 때 보여질 뷰들을 숨김
                profileImageView.setVisibility(View.GONE);
                nicknameTextView.setVisibility(View.GONE);
            }
            return null;
        });

        return view;
    }

}