package org.tourBot.ai.orchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.tourBot.ai.agent.RecommendAgent;
import org.tourBot.ai.orchestrator.planner.LlmPlanner;
import org.tourBot.ai.orchestrator.planner.Plan;
import org.tourBot.ai.orchestrator.planner.RuleBasedPlanner;
import org.tourBot.ai.rag.dto.Source;
import org.tourBot.ai.tool.ExchangeRateTool;
import org.tourBot.ai.tool.RagTool;
import org.tourBot.ai.tool.WeatherTool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final RecommendAgent recommendAgent;
    private final WeatherTool weatherTool;
    private final ExchangeRateTool exchangeRateTool;
    private final RagTool ragTool;
    private final RuleBasedPlanner rulePlanner;
    private final LlmPlanner llmPlanner;

    public String handle(List<Message> messages) {

        //마지막 사용자 메시지 추출
        String userInput = extractLastUserMessage(messages);
        log.info("User Input: {}", userInput);

        // Planner
        Plan plan = rulePlanner.plan(userInput);
        if (plan == null) {
            plan = llmPlanner.plan(userInput);
        }

        log.info("Plan: {}", plan);

        // Tool 실행
        List<Source> docs = new ArrayList<>();
        WeatherTool.WeatherResult weather = null;
        ExchangeRateTool.ExchangeResult exchange = null;

        if (plan.isUseRag()) {
            docs = ragTool.search(userInput);
            log.info("RAG docs size: {}", docs.size());
        }

        if (plan.isUseWeather()) {
            weather = weatherTool.getWeather(userInput);
        }

        if (plan.isUseExchangeRate()) {
            exchange = exchangeRateTool.getExchangeRate(userInput);
        }

        // Rerank (간단 버전)
        docs = rerank(userInput, docs);

        return recommendAgent.generate(
                userInput,
                docs,
                weather,
                exchange
        );
    }

    // 마지막 사용자 메시지 추출
    private String extractLastUserMessage(List<Message> messages) {

        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);

            if (message instanceof UserMessage userMessage) {
                return userMessage.getText();
            }
        }

        return "";
    }

    private List<Source> rerank(String query, List<Source> docs) {

        if (docs == null || docs.isEmpty()) {
            return docs;
        }

        // 현재는 길이 기반 간단 정렬 (임시)
        return docs.stream()
                .sorted(Comparator.comparingInt(doc -> doc.getContent().length()))
                .limit(5)
                .toList();
    }

}
