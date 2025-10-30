package com.example.todoapp.weather.controller;

import com.example.todoapp.weather.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Validated
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    public String getWeather(
            @RequestParam
            @NotBlank(message = "city parametresi boş olamaz.")
            @Size(min = 2, max = 50, message = "Şehir adı 2 ile 50 karakter arasında olmalıdır.")
            String city
    ) {
        return weatherService.getTemperatureForCity(city);
    }
}