package org.tourBot.ai.orchestrator.planner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmPlanner implements PlannerService {

    private final ChatClient plannerChatClient;

    @Override
    public Plan plan(String query) {

        try {
            Plan plan = plannerChatClient.prompt()
                    .system(buildSystemPrompt())
                    .user(query)
                    .call()
                    .entity(Plan.class);

            log.info("LLM Plan = {}", plan);

            return validate(plan);

        } catch (Exception e) {
            log.error("LlmPlanner failed", e);
            return fallbackPlan();
        }
    }

    private String buildSystemPrompt() {
        return """
        You are an AI planner that decides which tools to use.

        Available tools:
        - weather: for weather, climate, temperature, rainy season
        - exchangeRate: for currency, exchange, money conversion
        - rag: for document-based Q&A
        - recommend: for travel recommendations (places, restaurants, hotels)

        Rules:
        1. You MUST return ONLY valid JSON.
        2. DO NOT explain anything.
        3. Multiple tools can be true.
        4. If unsure, set all to false.

        Output format:
        {
          "useWeather": true/false,
          "useExchangeRate": true/false,
          "useRag": true/false,
          "needRecommend": true/false
        }

        Examples:

        Input: "서울 날씨 어때?"
        Output:
        {
          "useWeather": true,
          "useExchangeRate": false,
          "useRag": false,
          "needRecommend": false
        }

        Input: "도쿄 여행 추천해줘"
        Output:
        {
          "useWeather": false,
          "useExchangeRate": false,
          "useRag": false,
          "needRecommend": true
        }

        Input: "도쿄 날씨랑 환율 알려줘"
        Output:
        {
          "useWeather": true,
          "useExchangeRate": true,
          "useRag": false,
          "needRecommend": false
        }
        """;
    }

    private Plan validate(Plan plan) {
        if (plan == null) return fallbackPlan();

        return Plan.builder()
                .useWeather(plan.isUseWeather())
                .useExchangeRate(plan.isUseExchangeRate())
                .useRag(plan.isUseRag())
                .needRecommend(plan.isNeedRecommend())
                .build();
    }

    private Plan fallbackPlan() {
        return Plan.builder()
                .useWeather(false)
                .useExchangeRate(false)
                .useRag(false)
                .needRecommend(false)
                .build();
    }
}
