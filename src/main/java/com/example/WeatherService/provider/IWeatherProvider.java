package com.example.WeatherService.provider;

import com.example.WeatherService.dto.WeatherResponse;

//Interface is used so that if there's a new provider it can implement the interface.
public interface IWeatherProvider {

    WeatherResponse fetchWeather(String city);
}
