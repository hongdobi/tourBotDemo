package org.tourBot.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.tourBot.dto.HistoryDto;

import java.util.List;

@Component
public class HistoryClient {

    private final WebClient webClient;

    public HistoryClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<HistoryDto> getHistory(String userId, String sessionId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("history-service")
                        .port(8091)
                        .path("/history/getHistory")
                        .queryParam("userId", userId)
                        .queryParam("sessionId", sessionId)
                        .build())
                .retrieve()
                .bodyToFlux(HistoryDto.class)
                .collectList()
                .block();
    }
}
