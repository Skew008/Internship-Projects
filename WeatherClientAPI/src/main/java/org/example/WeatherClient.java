package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class WeatherClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WeatherClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Map<String, Object> getWeatherForecast() throws IOException, InterruptedException {
        String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Map.class);
        } else {
            throw new IOException("Error: Received HTTP status code " + response.statusCode());
        }
    }

    public static Map<String, Object> apiCall() {
        WeatherClient client = new WeatherClient();
        try {
//            Map<String, Object> forecast = client.getWeatherForecast();
//            String prettyJson = client.objectMapper.writeValueAsString(forecast);
//            System.out.println("Weather Forecast: \n" + forecast);
            return client.getWeatherForecast();
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to fetch weather data: " + e.getMessage());
        }
        return null;
    }
}
