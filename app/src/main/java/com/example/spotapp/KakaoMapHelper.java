package com.example.spotapp;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;

public class KakaoMapHelper {

    private static double cur_lat;
    private static double cur_lon;

    public static void setCoordinates(double latitude, double longitude) {
        cur_lat = latitude;
        cur_lon = longitude;
    }

    public static void initializeMap(MapView mapView) {
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
            }

        }, new KakaoMapReadyCallback() {

            @Override
            public void onMapReady(KakaoMap kakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                //LabelStyles styles = kakaoMap.getLabelManager().addLabelStyles(LabelStyle.from(LabelStyle.from(R.drawable.)))//

            }

            @Override
            public LatLng getPosition() {
                return LatLng.from(cur_lat, cur_lon); // 함안
            }

            @Override
            public int getZoomLevel() {
                // 지도 시작 시 확대/축소 줌 레벨 설정
                return 20;
            }

            @Override
            public String getViewName() {
                // KakaoMap 의 고유한 이름을 설정
                return "PhotoSpot";
            }
        });
    }
}
