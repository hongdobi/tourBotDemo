package org.tourBot.ai.agent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.tourBot.ai.rag.dto.Source;
import org.tourBot.ai.tool.ExchangeRateTool;
import org.tourBot.ai.tool.WeatherTool;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendAgent {

    private final ChatClient recommendChatClient;

    public String generate(
            String query,
            List<Source> docs,
            WeatherTool.WeatherResult weather,
            ExchangeRateTool.ExchangeResult exchange
    ) {

        log.info("RecommendAgent called");

        String context = buildUnifiedContext(docs, weather, exchange);

        String prompt = buildPrompt(query, context);

        try {
            return recommendChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("RecommendAgent failed", e);
            return fallbackResponse(query, context);
        }
    }

    /**
     * 모든 tool 결과를 하나의 context로 통합
     */
    private String buildUnifiedContext(
            List<Source> docs,
            WeatherTool.WeatherResult weather,
            ExchangeRateTool.ExchangeResult exchange
    ) {

        StringBuilder sb = new StringBuilder();

        // Weather
        if (weather != null) {
            sb.append("[WEATHER]\n")
                    .append("City: ").append(weather.city()).append("\n")
                    .append("Temperature: ").append(weather.temperature()).append("°C\n")
                    .append("Humidity: ").append(weather.humidity()).append("%\n")
                    .append("Wind Speed: ").append(weather.windSpeed()).append(" m/s\n\n");
        }

        // Exchange
        if (exchange != null) {
            sb.append("[EXCHANGE]\n")
                    .append(exchange.from()).append(" ")
                    .append(exchange.amount())
                    .append(" → ")
                    .append(exchange.result()).append(" ")
                    .append(exchange.to()).append("\n\n");
        }

        // Documents (RAG)
        if (docs != null && !docs.isEmpty()) {
            sb.append("[DOCUMENTS]\n");

            docs.stream()
                    .limit(5)
                    .forEach(doc -> sb.append("- [")
                            .append(doc.getFileName())
                            .append("]\n")
                            .append(doc.getContent())
                            .append("\n\n"));
        }

        if (sb.isEmpty()) {
            return "No context available.";
        }

        return sb.toString();
    }

    /**
     * Generator Prompt (핵심)
     */
    private String buildPrompt(String query, String context) {

        return """
        You are a smart travel assistant.
        
        [Intent Handling]
        
        - If the user asks a factual question:
          → Answer briefly and clearly
        
        - If the user asks for recommendations:
          → Provide 2~4 suggestions with reasons
        
        - If the user is just chatting (e.g. Hello):
          → Respond naturally like a friendly assistant

        ---------------------
        [User Question]
        %s

        ---------------------
        [Context]
        %s

        ---------------------
        [Instructions]

        1. Use ALL available context:
           - Weather
           - Exchange rate
           - Documents

        2. Combine information naturally.
           (Do NOT prioritize one source blindly)

        3. If recommending:
           - Provide 2~4 specific suggestions
           - Explain briefly WHY
           - Reflect weather conditions
           - Consider budget if exchange exists

        4. If context is insufficient:
           - Use general knowledge
           - Clearly say it is not from provided data

        5. Never hallucinate facts from documents.

        6. Keep answer:
           - Clear
           - Structured
           - Practical
        
        7. If user input is casual (e.g. greeting):
           - Respond naturally like a chatbot
           - Do NOT mention documents

        ---------------------
        [Answer]
        """.formatted(query, context);
    }

    /**
     * fallback (LLM 실패 대비)
     */
    private String fallbackResponse(String query, String context) {

        return """
        Sorry, something went wrong while generating the answer.

        [User Question]
        %s

        [Available Context]
        %s
        """.formatted(query, context);
    }
}
