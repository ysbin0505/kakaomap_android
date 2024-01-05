package com.example.spotapp.client;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("weather")
    private WeatherInfo[] weatherInfo;

    public WeatherInfo[] getWeatherInfo() {
        return weatherInfo;
    }

    public static class WeatherInfo {
        @SerializedName("main")
        private String condition;

        @SerializedName("icon")
        private String iconUrl;

        public String getCondition() {
            return condition;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }
}
