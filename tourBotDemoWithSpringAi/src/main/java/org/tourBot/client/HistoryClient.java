package org.tourBot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.tourBot.dto.HistoryDto;

import java.util.List;

@Component
public class HistoryClient {

    @Value("${history.url}")
    private String historyUrl;

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

    // 파일 업로드
    public String upload(MultipartFile file) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("file", file.getResource())
                .filename(file.getOriginalFilename());

        return webClient.post()
                .uri(historyUrl + "/files/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
