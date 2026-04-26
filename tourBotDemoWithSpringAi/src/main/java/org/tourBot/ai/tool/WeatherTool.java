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

    @Value("${weather.api-key}")
    private String apiKey;

    public WeatherTool(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Tool(
            name = "getWeather",
            description = "Get current weather for a city" +
                    "get weather information of specific duration if possible when the user asks"
    )
    public String getWeather(String query) {

        log.info("WeatherTool called: {}", query);

        String city = extractCityWithLLM(query);

        return callWeatherApi(city);
    }

    // 도시 추출
    private String extractCityWithLLM(String query) {

        String result = chatClient.prompt()
                .system("""
                    Extract the city name from the user query.

                    Rules:
                    - MUST return valid JSON
                    - DO NOT return plain text
                    - No explanation
                    
                    Format:
                    { "city": "Seoul" }
                    
                    Example:
                    Input: "서울 날씨 어때?"
                    Output: { "city": "Seoul" }
                    """)
                .user(query)
                .call()
                .content();

        log.info("Extracted city: {}", result);

        //json 형식 강제 방어로직
        int start = result.indexOf("{");
        int end = result.lastIndexOf("}");

        if (start != -1 && end != -1) {
            String json = result.substring(start, end + 1);
            return parseCity(json);  // 🔥 반드시 parse
        }

        return parseCity(result);
    }

    //json parsing 추가
    private String parseCity(String cityJson) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(cityJson);

            JsonNode cityNode = node.get("city");

            if (cityNode == null || cityNode.asText().isBlank()) {
                return "Seoul";
            }

            return cityNode.asText();

        } catch (Exception e) {
            log.error("JSON parsing failed: {}", cityJson, e);
            return "Seoul";
        }
    }

    // OpenWeather API 호출
    private String callWeatherApi(String city) {

        try {
            log.info("CITY = {}", city);

            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                    encodedCity,
                    apiKey
            );

            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            log.info("WeatherResponse = {}", response);

            return String.format(
                    "Current weather in %s:\nTemperature: %.1f°C\nHumidity: %d%%\nWind Speed: %.1f m/s",
                    city,
                    response.getMain().getTemp(),
                    response.getMain().getHumidity(),
                    response.getWind().getSpeed()
            );

        } catch (Exception e) {
            log.error("Weather API error", e);
            return "Failed to fetch weather data for " + city;
        }
    }
}
