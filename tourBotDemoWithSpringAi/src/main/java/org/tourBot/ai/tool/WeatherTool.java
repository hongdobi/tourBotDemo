package org.tourBot.ai.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tourBot.dto.WeatherResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class WeatherTool {

    private final ChatClient chatClient;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.api-key}")
    private String apiKey;

    public WeatherTool(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public WeatherResult getWeather(String query) {

        log.info("WeatherTool called: {}", query);

        String city = extractCityWithLLM(query);

        return callWeatherApi(city);
    }

    // 도시 추출
    private String extractCityWithLLM(String query) {

        String raw = chatClient.prompt()
                .system("""
                    Extract the city name from the user query.
                    
                    MUST return JSON only.

                    Rules:
                    - Return English city name
                    - No explanation
                    
                    Format:
                    { "city": "Seoul" }
                    """)
                .user(query)
                .call()
                .content();

        log.info("Extracted city: {}", raw);

        try {
            int start = raw.indexOf("{");
            int end = raw.lastIndexOf("}");

            if (start != -1 && end != -1) {
                raw = raw.substring(start, end + 1);
            }

            JsonNode node = objectMapper.readTree(raw);

            String city = node.get("city").asText();

            if (city == null || city.isBlank()) {
                return "Seoul";
            }

            return city;

        } catch (Exception e) {
            log.error("City parsing failed", e);
            return "Seoul";
        }
    }

    // OpenWeather API 호출
    private WeatherResult callWeatherApi(String city) {

        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                    encodedCity,
                    apiKey
            );

            log.info("URL: {}", url);

            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);

            log.info("WeatherResponse = {}", response);

            String condition = "UNKNOWN";
            String description = "UNKNOWN";

            if (response.getWeather() != null && !response.getWeather().isEmpty()) {
                condition = response.getWeather().get(0).getMain();
                description = response.getWeather().get(0).getDescription();
            }

            return new WeatherResult(
                    city,
                    response.getMain().getTemp(),
                    response.getMain().getHumidity(),
                    response.getWind().getSpeed(),
                    condition,
                    description
            );

        } catch (Exception e) {
            log.error("Weather API error", e);

            return new WeatherResult(
                    city,
                    -1,
                    -1,
                    -1,
                    "UNKNOWN",
                    "Failed to fetch weather data"
            );
        }
    }

    public record WeatherResult(
            String city,
            double temperature,
            int humidity,
            double windSpeed,
            String condition,
            String description
    ) {}
}
