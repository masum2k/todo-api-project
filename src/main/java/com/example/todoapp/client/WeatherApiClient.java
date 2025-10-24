package com.example.todoapp.client;

import com.example.todoapp.weather.dto.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weather-api", url = "${weather.api.base-url}")
public interface WeatherApiClient {

    @GetMapping("/current.json")
    WeatherResponse getCurrentWeather(
            @RequestParam("key") String apiKey,
            @RequestParam("q") String location
    );
}