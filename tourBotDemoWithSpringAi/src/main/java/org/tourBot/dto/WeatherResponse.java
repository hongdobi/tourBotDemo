package org.tourBot.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeatherResponse {

    private List<Weather> weather;
    private Main main;
    private Wind wind;

    @Data
    public static class Weather {
        private String main;
        private String description;
    }

    @Data
    public static class Main {
        private double temp;
        private int humidity;
    }

    @Data
    public static class Wind {
        private double speed;
    }
}
