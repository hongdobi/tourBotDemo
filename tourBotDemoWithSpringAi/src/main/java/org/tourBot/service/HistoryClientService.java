package org.tourBot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.tourBot.domain.Role;
import org.tourBot.dto.HistoryDto;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryClientService {

    @Value("${history.url}")
    private String historyUrl;

    private final WebClient webClient;

    public List<HistoryDto> getHistory(String userId, String sessionId) {

        log.info(">>> [HistoryClient] getHistory 호출 userId={}, sessionId={}", userId, sessionId);

        try {
            List<HistoryDto> result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("history-service")
//                            .host("localhost")
                            .port(8091)
                            .path("/history/getHistory")
                            .queryParam("userId", userId)
                            .queryParam("sessionId", sessionId)
                            .build())
                    .retrieve()
                    .bodyToFlux(HistoryDto.class)
                    .collectList()
                    .block();

            log.info("<<< [HistoryClient] getHistory 결과 size={}", result.size());

            return result;

        } catch (Exception e) {
            log.error("!!! history-service 호출 실패 userId={}, sessionId={}", userId, sessionId, e);
            throw e;
        }

    }

    public void saveHistory(String userId, String sessionId, Role role, String content) {
        webClient.post()
                .uri(historyUrl + "/history/saveHistory")
                .bodyValue(Map.of(
                        "userId", userId,
                        "sessionId", sessionId,
                        "role", role.toValue(),
                        "content", content
                ))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
