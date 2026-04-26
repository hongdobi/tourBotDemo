package org.tourBot.dto;

import lombok.Data;

@Data
public class WeatherResponse {

    private Main main;
    private Wind wind;

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
