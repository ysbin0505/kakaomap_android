package com.example.spotapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

public class SpotFragment extends Fragment {

    private Button showMapButton;
    private Button mylocallAdd;
    private static double longitude;
    private static double latitude;
    private static Label curLabel;

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
                return 20;
            }
        });
        // Inflate the layout for this fragment
        return view;
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

                Log.d("SpotFragment", "Label 클릭 이벤트 발생, Intent를 통한 화면 전환 시작");

                Intent intent = new Intent(getActivity(), LabelContext.class);

                // 필요한 정보를 Intent에 추가해야 함

                try {
                    startActivity(intent);
                    Log.d("SpotFragment", "Intent를 통한 화면 전환 성공");
                } catch (Exception e) {
                    Log.e("SpotFragment", "Intent를 통한 화면 전환 실패: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });


    }

}