package com.example.WeatherService.controller;

import com.example.WeatherService.dto.WeatherResponse;
import com.example.WeatherService.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam(defaultValue = "Melbourne") String city) {
        WeatherResponse response = weatherService.getWeather(city);
        return ResponseEntity.ok(response);
    }

}
