package com.example.WeatherService.provider;

import com.example.WeatherService.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component("weatherStackProvider")
public class WeatherStackProvider implements IWeatherProvider{

    @Value("${weatherstack.api.key}")
    private String apiKey;

    @Value("${openweathermap.url}")
    private String url;

    private final RestTemplate restTemplate;

    public WeatherStackProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public WeatherResponse fetchWeather(String city) {
        String uri = UriComponentsBuilder.fromUriString("http://api.weatherstack.com/current")
                .queryParam("access_key", apiKey)
                .queryParam("query", city)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
        Map<String, Object> current = (Map<String, Object>) response.get("current");

        //according to the documentation by default the API returns metric so no conversion needed
        double temp = ((Number) current.get("temperature")).doubleValue();
        double wind = ((Number) current.get("wind_speed")).doubleValue();

        return new WeatherResponse(temp, wind);
    }
}
