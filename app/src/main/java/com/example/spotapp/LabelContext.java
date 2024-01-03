package com.example.spotapp;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.example.spotapp.client.LocationData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LabelContext extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.239.152:8080/"; //210.123.182.64
    private RetrofitService retrofitService;
    private double latitude;
    private double longitude;

    private EditText titleTextView;
    private ImageView profileImageView;
    private TextView writerTextView;
    private EditText contentTextView;

    private Button imageUploadBtn;

    private List<MultipartBody.Part> files;
    private ActivityResultLauncher<Intent> startActivityResult;
    private String uploadedImageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lable_context);

        // Intent에서 위치 정보를 받아옴
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);

        // 로그에 위치 정보 출력
        Log.d("LabelContext", "Latitude: " + latitude + " Longitude " + longitude);

        // UI 요소 초기화
        titleTextView = findViewById(R.id.title);
        contentTextView = findViewById(R.id.text);
        writerTextView = findViewById(R.id.user_name);
        //dateTextView = findViewById(R.id.view_post_time);
        profileImageView = findViewById(R.id.imageView);
        imageUploadBtn = findViewById(R.id.imageUploadBtn);

        kakaoProfile();
        redoButton();

        //이미지 보내기
        files = new ArrayList<>();
        startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                uploadImageToServer(imageUri);
            }
        });

        uploadImageBtn();
        doneButton();
    }

    private void uploadImageBtn() {
        imageUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button imageSubmit = (Button) findViewById(R.id.imageUploadBtn);

                imageSubmit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        System.out.println("imageSubmit Click!");

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityResult.launch(intent);
                    }
                });
            }
        });
    }

    public void uploadImageToServer(Uri imageUri) {
        System.out.println("imageUri = " + imageUri);
        System.out.println("getRealPathFromURI(imageUri) = " + getRealPathFromURI(imageUri));
        File file = new File(getRealPathFromURI(imageUri));

        // 파일이 존재하는지 확인
        if (file.exists()) {
            RequestBody requestBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
            MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
            files = new ArrayList<>();
            files.add(part);
            Log.e("UploadImage", "File exist");

            // 이미지 파일의 이름을 저장
            uploadedImageFileName = file.getName();
        } else {
            // 파일이 존재하지 않을 경우 로그 또는 예외 처리
            Log.e("UploadImage", "File does not exist");
        }
    }


    // Uri에서 실제 파일 경로 가져오기
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void doneButton() {
        Button doneButton = findViewById(R.id.done_button);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new GsonBuilder().setLenient().create();
                Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson)).client(new OkHttpClient()).build();
                retrofitService = retrofit.create(RetrofitService.class);

                // 사용자가 입력한 제목과 내용을 가져오기
                EditText titleEditText = findViewById(R.id.title);
                EditText contentEditText = findViewById(R.id.text);
                String title = titleEditText.getText().toString();
                String description = contentEditText.getText().toString();
                String image = uploadedImageFileName;
                String address = "null";

                // LocationData 객체 생성
                LocationData locationData = new LocationData(latitude, longitude, title, address, description, image);

                // Retrofit을 사용하여 서버로 POST 요청 보내기
                Call<Void> call = retrofitService.addLocation(locationData);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();


                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("API", "Data sent successfully");
                        } else {
                            Log.e("API", "Failed to send data");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("API", "Network Error : " + t.getMessage());
                    }
                });

            }
        });
    }

    private void redoButton(){
        FloatingActionButton redoButton = findViewById(R.id.redoButton);
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SpotFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }
        });
    }
    private void kakaoProfile() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {

                return null;
            }
            if (user != null) {
                // 사용자 정보를 성공적으로 가져온 경우 프로필 이미지와 닉네임 업데이트
                String nickname = user.getKakaoAccount().getProfile().getNickname();
                String profileImageUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();

                // 닉네임 설정
                writerTextView.setText(nickname);

                // 프로필 이미지 설정
                Glide.with(this)
                        .load(profileImageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.before_login)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("Glide", "Image loading failed: " + e.getMessage());
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Glide", "Image loading successful");
                                return false;
                            }
                        })
                        .into(profileImageView);

                // 로그인 상태일 때 보여질 뷰들을 표시
                profileImageView.setVisibility(View.VISIBLE);
                writerTextView.setVisibility(View.VISIBLE);
            } else {
                // 로그아웃 상태일 때 보여질 뷰들을 숨김
                profileImageView.setVisibility(View.GONE);
                writerTextView.setVisibility(View.GONE);
            }
            return null;
        });
    }
}