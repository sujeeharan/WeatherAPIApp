package com.example.WeatherService.service;

import com.example.WeatherService.dto.WeatherResponse;
import com.example.WeatherService.provider.IWeatherProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    private IWeatherProvider primaryProvider;
    private IWeatherProvider secondaryProvider;
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        primaryProvider = mock(IWeatherProvider.class);
        secondaryProvider = mock(IWeatherProvider.class);
        weatherService = new WeatherService(primaryProvider, secondaryProvider);
    }

    @Test
    @DisplayName("Returns weather from primary provider when available")
    void returnsWeatherFromPrimaryProvider() {
        WeatherResponse response = new WeatherResponse();
        when(primaryProvider.fetchWeather("London")).thenReturn(response);

        WeatherResponse result = weatherService.getWeather("London");

        assertEquals(response, result);
        verify(primaryProvider, times(1)).fetchWeather("London");
        verifyNoInteractions(secondaryProvider);
    }

    @Test
    @DisplayName("Falls back to secondary provider when primary fails")
    void fallsBackToSecondaryProvider() {
        WeatherResponse response = new WeatherResponse();
        when(primaryProvider.fetchWeather("Paris")).thenThrow(new RuntimeException("Primary failed"));
        when(secondaryProvider.fetchWeather("Paris")).thenReturn(response);

        WeatherResponse result = weatherService.getWeather("Paris");

        assertEquals(response, result);
        verify(primaryProvider, times(1)).fetchWeather("Paris");
        verify(secondaryProvider, times(1)).fetchWeather("Paris");
    }

    @Test
    @DisplayName("Returns cached weather when within cache duration")
    void returnsCachedWeather() {
        WeatherResponse cachedResponse = new WeatherResponse();
        weatherService.getWeather("Berlin"); // Populate cache
        when(primaryProvider.fetchWeather("Berlin")).thenReturn(cachedResponse);

        WeatherResponse result1 = weatherService.getWeather("Berlin");
        WeatherResponse result2 = weatherService.getWeather("Berlin");

        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("Throws exception when all providers fail and no cache exists")
    void throwsExceptionWhenAllProvidersFail() {
        when(primaryProvider.fetchWeather("Rome")).thenThrow(new RuntimeException("Primary failed"));
        when(secondaryProvider.fetchWeather("Rome")).thenThrow(new RuntimeException("Secondary failed"));

        assertThrows(RuntimeException.class, () -> weatherService.getWeather("Rome"));
    }
}