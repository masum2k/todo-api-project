package com.example.todoapp.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeather(
        double temp_c,
        double feelslike_c,
        int humidity
) {
}