package com.example.spotapp;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.spotapp.client.ApiResponse;
import com.example.spotapp.client.LocationData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.Poi;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import com.kakao.vectormap.label.TrackingManager;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SpotFragment extends Fragment {

    private static final String BASE_URL = "http://192.168.239.152:8080/"; //home "http://210.123.182.64:8080/"; // "http://10.0.2.2:8080/" virtual // "http://172.25.80.1:8080/"

    private RetrofitService retrofitService;
    private KakaoMap kakaoMap;
    private Button showMapButton;
    private Button mylocallAdd;
    private static double longitude;
    private static double latitude;
    private static Label curLabel;
    private static long locationId;

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        SpotFragment.longitude = longitude;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        SpotFragment.latitude = latitude;
    }
    public static long getLocationId(){
        return locationId;
    }
    public static void setLocationId(long locationId){
        SpotFragment.locationId = locationId;
    }

    public static Label getCurLabel() {
        return curLabel;
    }

    public static void setCurLabel(Label curLabel) {
        SpotFragment.curLabel = curLabel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot, container, false);

        MapView mapView = view.findViewById(R.id.map_view);

        showMapButton = view.findViewById(R.id.show_map_button);
        showMapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 길찾기 좌표
                double latitude = getLatitude();
                double longitude = getLongitude();

                // 지도를 열기 위한 URI 생성
                String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(PhotoSpot)";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨

                SpotFragment.this.kakaoMap = kakaoMap; // kakaoMap을 전역 변수에 저장
                //서버에서 마커 받아오기
                fetchDataFromServer();


                // 현재 위치에 마커 추가
                mylocallAdd = view.findViewById(R.id.mylocalAdd);
                mylocallAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addMarkerToCurrentLocation(kakaoMap);
                    }
                });

                /**
                 * 라벨 생성
                 * 0~4까지 blue 마커, 5~16까지도 블루마커, 17~끝까지 cur_pos로
                 */
                LabelStyles styles = kakaoMap.getLabelManager()
                        .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.blue_marker).setZoomLevel(0),
                                LabelStyle.from(R.drawable.blue_marker).setTextStyles(15, Color.BLACK).setZoomLevel(5),
                                LabelStyle.from(R.drawable.cur_pos).setZoomLevel(17)));

                LabelOptions options = LabelOptions.from(LatLng.from(latitude,longitude))
                        .setStyles(styles);
                LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                Label label = layer.addLabel(options);
                curLabel = label;

                // 라벨 추적
                TrackingManager trackingManager = kakaoMap.getTrackingManager();
                trackingManager.startTracking(label);

                // 위치 정보 로그 출력
                Log.i("SpotFragment", "Latitude: " + latitude + ", Longitude: " + longitude);
            }

            @Override
            public LatLng getPosition() {
                return LatLng.from(latitude, longitude);
            }

            @Override
            public int getZoomLevel() {
                // 지도 시작 시 확대/축소 줌 레벨 설정
                return 15;
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void fetchDataFromServer() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(new OkHttpClient())
                .build();
        retrofitService = retrofit.create(RetrofitService.class);

        Call<ApiResponse<List<LocationData>>> call = retrofitService.getLocations(latitude, longitude);
        call.enqueue(new Callback<ApiResponse<List<LocationData>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<LocationData>>> call, Response<ApiResponse<List<LocationData>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<LocationData>> apiResponse = response.body();
                    List<LocationData> locationList = apiResponse.getData();
                    Log.d("마커 불러오기", "성공 : " + locationList.size() + " locations");
                    displayMarkers(kakaoMap, locationList);
                } else {
                    // 서버 응답이 실패했을 때의 처리
                    Log.e("마커 불러오기", "실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<LocationData>>> call, Throwable t) {
                // 네트워크 오류 또는 서버 응답이 실패했을 때의 처리
                Log.e("마커 불러오기", "에러 : " + t.getMessage());
            }
        });


        /*Call<List<LocationData>> call = retrofitService.getAllLocations();
        call.enqueue(new Callback<List<LocationData>>() {
            @Override
            public void onResponse(Call<List<LocationData>> call, Response<List<LocationData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LocationData> locationList = response.body();
                    Log.d("마커 불러오기", "성공 : " + locationList.size() + " locations");
                    displayMarkers(kakaoMap, locationList);
                } else {
                    // 서버 응답이 실패했을 때의 처리
                    Log.e("마커 불러오기", "실패");
                }
            }

            @Override
            public void onFailure(Call<List<LocationData>> call, Throwable t) {
                // 네트워크 오류 또는 서버 응답이 실패했을 때의 처리
                Log.e("마커 불러오기", "에러 : " + t.getMessage());
            }
        });*/
    }

    private void displayMarkers(KakaoMap kakaoMap, List<LocationData> locationList) {
        if (kakaoMap == null) {
            Log.e("SpotFragment", "KakaoMap is null");
            return;
        }

        // 서버에서 받아온 데이터를 사용하여 지도에 마커 표시
        for (LocationData location : locationList) {
            // 각 LocationData에서 위도와 경도를 가져와서 LatLng 객체 생성
            LatLng currentLocation = LatLng.from(location.getLatitude(), location.getLongitude());

            locationId = location.getId();

            // 마커 스타일 설정
            LabelStyle markerStyle = LabelStyle.from(R.drawable.blue_marker)
                    .setTextStyles(15, Color.BLACK);

            // 마커 옵션 설정
            LabelOptions markerOptions = LabelOptions.from(currentLocation)
                    .setStyles(markerStyle).setTag(locationId);

            // 마커 레이어 가져오기
            LabelLayer markerLayer = kakaoMap.getLabelManager().getLayer();

            // 마커 추가
            Label marker = markerLayer.addLabel(markerOptions);

            kakaoMap.setOnLabelClickListener(new KakaoMap.OnLabelClickListener() {
                @Override
                public void onLabelClicked(KakaoMap kakaoMap, LabelLayer layer, Label label) {
                    marker.setClickable(true);
                    Toast.makeText(getContext(), "마커가 클릭되었습니다.", Toast.LENGTH_SHORT).show();

                    Log.d("SpotFragment", "이벤트 호출 시작");
                    Log.d("SpotFragment", "Label clicked at latitude: " + label.getPosition().latitude + ", longitude: " + label.getPosition().longitude);

                    long clickedLocationId = (long) label.getTag();
                    Log.d("SpotFragment", "Label clicked with locationId: " + clickedLocationId);

                    Intent intent = new Intent(getActivity(), LabelDBContext.class);
                    intent.putExtra("locationId", clickedLocationId);

                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("SpotFragment", "실패: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }



    private void addMarkerToCurrentLocation(KakaoMap kakaoMap) {
        // 현재 위치에 마커를 추가하기 위해 LatLng 객체 생성
        LatLng currentLocation = LatLng.from(latitude, longitude);

        // 마커 스타일 설정
        LabelStyle markerStyle = LabelStyle.from(R.drawable.blue_marker)
                .setTextStyles(15, Color.BLACK);

        // 마커 옵션 설정
        LabelOptions markerOptions = LabelOptions.from(currentLocation)
                .setStyles(markerStyle);

        // 마커 레이어 가져오기
        LabelLayer markerLayer = kakaoMap.getLabelManager().getLayer();

        // 마커 추가
        Label marker = markerLayer.addLabel(markerOptions);

        kakaoMap.setOnLabelClickListener(new KakaoMap.OnLabelClickListener() {
            @Override
            public void onLabelClicked(KakaoMap kakaoMap, LabelLayer layer, Label label) {
                marker.setClickable(true);
                Toast.makeText(getContext(), "마커가 클릭되었습니다.", Toast.LENGTH_SHORT).show();

                Log.d("SpotFragment", "이벤트 호출 시작");

                Intent intent = new Intent(getActivity(), LabelContext.class);

                intent.putExtra("latitude", label.getPosition().latitude);
                intent.putExtra("longitude", label.getPosition().longitude);

                try {
                    Log.d("SpotFragment", "성공");
                    Log.d("SpotFragment", "Latitude: " + label.getPosition().latitude + " Longitude " + label.getPosition().longitude);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("SpotFragment", "실패: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });


    }

}