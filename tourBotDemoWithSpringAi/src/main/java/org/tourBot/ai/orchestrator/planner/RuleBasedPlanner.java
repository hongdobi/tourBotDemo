package org.tourBot.ai.orchestrator.planner;

import org.springframework.stereotype.Component;

@Component
public class RuleBasedPlanner implements PlannerService {


    @Override
    public Plan plan(String query) {
        String q = query.toLowerCase();

        boolean useWeather = containsAny(q,
                "날씨", "기온", "온도", "비", "눈", "우기", "건기",
                "기후", "더워", "추워", "습도", "바람"
        );

        boolean useExchangeRate = containsAny(q,
                "환율", "환전", "달러", "엔화", "유로", "usd", "jpy", "eur", "krw"
        );

        boolean useRag = containsAny(q,
                "문서", "자료", "파일", "pdf", "규정", "정책"
        );

        boolean needRecommend = containsAny(q,
                "추천", "어디", "갈만한", "여행", "코스", "맛집", "호텔"
        );

        // 아무것도 해당 안되면 → LLM planner로 넘김
        if (!useWeather && !useExchangeRate && !useRag && !needRecommend) {
            return null;
        }

        return Plan.builder()
                .useWeather(useWeather)
                .useExchangeRate(useExchangeRate)
                .useRag(useRag)
                .needRecommend(needRecommend)
                .build();
    }

    private boolean containsAny(String query, String... keywords) {
        for (String keyword : keywords) {
            if (query.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
