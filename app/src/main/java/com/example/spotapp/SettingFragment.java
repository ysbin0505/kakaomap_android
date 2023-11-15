package com.example.spotapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.kakao.sdk.user.UserApiClient;

public class SettingFragment extends Fragment {

    private ImageView profileImageView;
    private TextView nicknameTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public SettingFragment() {
        // Required empty public constructor
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        kakaoProfile();
        settingsButton();

        profileImageView = view.findViewById(R.id.profileImageView);
        nicknameTextView = view.findViewById(R.id.nicknameTextView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        ImageButton settingsButton = view.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = new MyInformationFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

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

        // 새로고침 이벤트 처리
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 카카오톡 로그인 후 사용자 정보 가져오기
            UserApiClient.getInstance().me((user, error) -> {
                if (error != null) {
                    // 사용자 정보를 가져오는 도중 에러가 발생한 경우 처리
                    // 에러 처리 로직 추가하기 (예: 토스트 메시지를 통한 사용자에게 알림)
                    swipeRefreshLayout.setRefreshing(false); // 새로고침 완료 처리
                    return null;
                }
                if (user != null) {
                    // 사용자 정보를 성공적으로 가져온 경우 프로필 이미지와 닉네임 업데이트
                    String nickname = user.getKakaoAccount().getProfile().getNickname();
                    String profileImageUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();


                    // 닉네임 설정
                    nicknameTextView.setText(nickname);

                    // 프로필 이미지 설정
                    Glide.with(requireContext())
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
                // 새로고침 완료 처리
                swipeRefreshLayout.setRefreshing(false);
                return null;
            });
        });

        return view;
    }

    private void settingsButton() {
    }

    private void kakaoProfile() {


    }


}