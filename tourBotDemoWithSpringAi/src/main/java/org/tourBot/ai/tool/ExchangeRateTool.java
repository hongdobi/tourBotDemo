package org.tourBot.ai.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ExchangeRateTool {

    private final ChatClient chatClient;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExchangeRateTool(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public ExchangeResult getExchangeRate(String query) {

        log.info("ExchangeTool called: {}", query);

        ExchangeRequest req = extractCurrency(query);

        return callExchangeApi(req);
    }

    // LLM으로 통화 추출
    private ExchangeRequest extractCurrency(String query) {

        String raw = chatClient.prompt()
                .system("""
                    Extract currency conversion info.

                    MUST return JSON only.

                    Format:
                    {
                      "from": "USD",
                      "to": "KRW",
                      "amount": 100
                    }

                    Rules:
                    - currency must be ISO code (USD, KRW, JPY)
                    - amount default = 1 if not provided
                    """)
                .user(query)
                .call()
                .content();

        log.info("통화 추출 RAW: {}", raw);

        try {
            // JSON 보정
            int start = raw.indexOf("{");
            int end = raw.lastIndexOf("}");

            if (start != -1 && end != -1) {
                raw = raw.substring(start, end + 1);
            }

            JsonNode node = objectMapper.readTree(raw);

            String from = node.get("from").asText();
            String to = node.get("to").asText();
            double amount = node.has("amount") ? node.get("amount").asDouble() : 1.0;

            return new ExchangeRequest(from, to, amount);

        } catch (Exception e) {
            log.error("JSON parsing failed", e);
            return new ExchangeRequest("USD", "KRW", 1.0);
        }
    }

    // 환율 API 호출
    private ExchangeResult callExchangeApi(ExchangeRequest req) {

        try {
            String url = String.format(
                    "https://api.frankfurter.app/latest?amount=%f&from=%s&to=%s",
                    req.amount(),
                    req.from().toUpperCase(),
                    req.to().toUpperCase()
            );

            log.info("URL: {}", url);

            String response = restTemplate.getForObject(url, String.class);
            log.info("RAW RESPONSE: {}", response);

            JsonNode node = objectMapper.readTree(response);

            String to = req.to().toUpperCase().trim();
            JsonNode rates = node.get("rates");

            if (rates == null || rates.get(to) == null) {
                throw new RuntimeException("Invalid exchange response");
            }

            double result = rates.get(to).asDouble();

            return new ExchangeResult(
                    req.from(),
                    req.to(),
                    req.amount(),
                    result
            );

        } catch (Exception e) {
            log.error("Exchange API error", e);
            return new ExchangeResult(
                    req.from(),
                    req.to(),
                    req.amount(),
                    -1.0 // 실패 표시
            );
        }
    }

    // DTO
    record ExchangeRequest(String from, String to, double amount) {}

    public record ExchangeResult(String from, String to, double amount, double result) {}
}
