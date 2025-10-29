package com.example.todoapp.weather.service.impl;

import com.example.todoapp.client.WeatherApiClient;
import com.example.todoapp.weather.dto.WeatherResponse;
import com.example.todoapp.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherApiClient weatherApiClient;

    @Value("${weather.api.key}")
    private String apiKey;

    @Override
    public String getTemperatureForCity(String city) {
        try {
            WeatherResponse response = weatherApiClient.getCurrentWeather(apiKey, city);

            if (response != null && response.getCurrent() != null) {
                double temp = response.getCurrent().getTemp_c();
                double feels_like = response.getCurrent().getFeelslike_c();
                int humidity = response.getCurrent().getHumidity();
                return city + " için şu anki sıcaklık: " + temp + "°C and feels like: " + feels_like + "and humidity is: "+ humidity;
            }
            return city + " için hava durumu bilgisi alınamadı.";
        } catch (Exception e) {
            return city + " için hava durumu alınırken bir hata oluştu: " + e.getMessage();
        }
    }
}