package org.tourBot.history.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiClient {

    private final WebClient webClient;

    // ingest 실패 시 자동 재시도 => 3회 실패 시 @Recover 호출
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,  // 최대 3번
            backoff = @Backoff(delay = 1000)  // 1초 간격
    )
    public void ingest(String fileId, String filePath, String fileName) {

        log.info("AI ingest 요청 (fileId={})", fileId);

        webClient.post()
                .uri("/api/rag/ingest")
                .bodyValue(Map.of(
                        "fileId", fileId,
                        "filePath", filePath,
                        "fileName", fileName
                ))
                .retrieve()
                .bodyToMono(String.class)  // 응답을 Mono로 받음(reactive)
                .timeout(Duration.ofSeconds(3)) // 요청별 timeout 3초
                .block(); // 동기 처리
    }

    // retry 실패 후 fallback
    @Recover
    public void recover(Exception e, String fileId, String filePath, String fileName) {

        log.error("AI ingest 최종 실패 (fileId={})", fileId, e);

        // TODO: DB에 상태 저장 or 나중에 재처리 큐
    }

    // delete 실패 시 자동 재시도 => 3회 실패 시 @Recover 호출
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void delete(String fileId) {

        log.info("AI delete 요청 (fileId={})", fileId);

        webClient.delete()
                .uri("/rag/{fileId}", fileId)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    @Recover
    public void recoverDelete(Exception e, String fileId) {

        log.error("AI delete 최종 실패 (fileId={})", fileId, e);

        // TODO: 재시도 큐 or 로그 적재
    }
}
