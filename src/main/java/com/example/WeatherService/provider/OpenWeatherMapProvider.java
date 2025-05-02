package com.example.WeatherService.provider;

import com.example.WeatherService.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component("openWeatherMapProvider")
public class OpenWeatherMapProvider implements IWeatherProvider {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.url}")
    private String url;

    private final RestTemplate restTemplate;

    public OpenWeatherMapProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public WeatherResponse fetchWeather(String city) {
        String uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("q", city+",AU")
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
        Map<String, Object> main = (Map<String, Object>) response.get("main");
        Map<String, Object> wind = (Map<String, Object>) response.get("wind");

        //metric values are requested so no conversion needed
        double temp = ((Number) main.get("temp")).doubleValue();
        double windSpeed = ((Number) wind.get("speed")).doubleValue();

        return new WeatherResponse(temp, windSpeed);
    }
}
