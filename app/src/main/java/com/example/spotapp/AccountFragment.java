package com.example.spotapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.kakao.sdk.user.UserApiClient;

public class AccountFragment extends Fragment {

    private ImageView profileImageView;
    private EditText editIdInput;
    private EditText editEmailInput;
    private EditText editNameInput;

    private Button buttonViewModify;
    private Button buttonWithdraw;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        profileImageView = view.findViewById(R.id.profileImageView);
        editIdInput = view.findViewById(R.id.editIdInput);
        editEmailInput = view.findViewById(R.id.editEmailInput);
        editNameInput = view.findViewById(R.id.editNameInput);
        buttonViewModify = view.findViewById(R.id.buttonViewModify);
        buttonWithdraw = view.findViewById(R.id.buttonWithdraw);

        hiddenKakao();

        buttonWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWithdrawConfirmationDialog();
            }
        });


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
                

                // 프로필 이미지 설정
                Glide.with(requireContext())
                        .load(profileImageUrl)
                        .circleCrop() // 원형 모양으로 이미지 보여주기
                        .placeholder(R.drawable.loading) // 로딩 중에 보여질 이미지 설정
                        .error(R.drawable.before_login) // 이미지 로딩 실패시 보여질 이미지 설정
                        .into(profileImageView);

                // 아이디, 이메일, 이름 설정  -> 아직은 닉네임으로 대체
                editIdInput.setText(nickname);
                editEmailInput.setText(nickname);
                editNameInput.setText(nickname);

                // 로그인 상태일 때 보여질 뷰들을 표시
                profileImageView.setVisibility(View.VISIBLE);
                editIdInput.setVisibility(View.VISIBLE);
                editEmailInput.setVisibility(View.VISIBLE);
                editNameInput.setVisibility(View.VISIBLE);
                buttonViewModify.setVisibility(View.VISIBLE);
                buttonWithdraw.setVisibility(View.VISIBLE);

            } else {
                // 로그아웃 상태일 때 보여질 뷰들을 숨김
                profileImageView.setVisibility(View.GONE);
                editIdInput.setVisibility(View.GONE);
                editEmailInput.setVisibility(View.GONE);
                editNameInput.setVisibility(View.GONE);
                buttonViewModify.setVisibility(View.GONE);
                buttonWithdraw.setVisibility(View.GONE);
            }
            return null;
        });


        return view;
    }

    private void showWithdrawConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("회원 탈퇴").setMessage("정말로 회원 탈퇴하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){

                        deleteAccount();
                        Toast.makeText(requireContext(), "탈퇴되었습니다", Toast.LENGTH_SHORT).show();
                        // 회원 탈퇴 후 로그인 화면으로 이동하거나 앱을 종료하는 등의 작업 수행
                        navigateToSetting();
                    }
                }).setNegativeButton("취소", null).show();
    }

    private void navigateToSetting() {
        // HomeFragment 인스턴스 생성
        //HomeFragment homeFragment = new HomeFragment();  -> 홈으로 가거나 세팅으로가거나
        SettingFragment settingFragment = new SettingFragment();

        // FragmentManager를 통해 트랜잭션 시작
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // HomeFragment를 추가하고 이전에 표시된 프래그먼트를 백 스택에 추가 (선택사항)
        fragmentTransaction.replace(R.id.fragment_container, settingFragment);
        fragmentTransaction.addToBackStack(null);

        // 트랜잭션 커밋
        fragmentTransaction.commit();
    }

    private void deleteAccount() {
        UserApiClient.getInstance().unlink(error ->
        {
            if (error != null) {
                Log.e(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨", error);
            }
            else {
                Log.i(TAG, "연결 끊기 실패");
            }
            return null;
        });
    }


    private void hiddenKakao() {
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
