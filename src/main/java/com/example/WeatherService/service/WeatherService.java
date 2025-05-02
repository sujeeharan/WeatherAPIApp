package com.example.WeatherService.service;

import com.example.WeatherService.dto.WeatherResponse;
import com.example.WeatherService.provider.IWeatherProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class WeatherService {

    private final IWeatherProvider primaryProvider;
    private final IWeatherProvider secondaryProvider;
    private final AtomicReference<CachedWeather> cache = new AtomicReference<>();


    public WeatherService(@Qualifier("weatherStackProvider") IWeatherProvider primaryProvider,
                          @Qualifier("openWeatherMapProvider") IWeatherProvider secondaryProvider) {
        this.primaryProvider = primaryProvider;
        this.secondaryProvider = secondaryProvider;
    }

    public WeatherResponse getWeather(String city){
        //Used shared cache using atomic variable to update and be accessed by several threads but restricts concurrent access
        CachedWeather current = cache.get();
        if (current != null && Instant.now().isBefore(current.timestamp.plusSeconds(3))) {
            return current.response;
        }

        try {
            WeatherResponse response = primaryProvider.fetchWeather(city);
            cache.set(new CachedWeather(response));
            return response;
        } catch (Exception e) {
            //Fallback
            try {
                WeatherResponse response = secondaryProvider.fetchWeather(city);
                cache.set(new CachedWeather(response));
                return response;
            } catch (Exception ex) {
                if (current != null) {
                    return current.response;
                }
                throw new RuntimeException("All providers failed");
            }
        }
    }

    private static class CachedWeather {
        WeatherResponse response;
        Instant timestamp;

        CachedWeather(WeatherResponse response) {
            this.response = response;
            this.timestamp = Instant.now();
        }
    }
}
