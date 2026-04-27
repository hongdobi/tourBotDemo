package org.tourBot.ai.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.tourBot.ai.agent.RecommendAgent;
import org.tourBot.ai.prompt.PromptProperties;
import org.tourBot.ai.tool.ExchangeRateTool;
import org.tourBot.ai.tool.WeatherTool;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final ChatClient chatClient;
    private final RecommendAgent recommendAgent;
    private final WeatherTool weatherTool;
    private final ExchangeRateTool exchangeRateTool;
//    private final RagTool ragTool;
//    private final DbTool dbTool;
    private final PromptProperties prompt;

    public String handle(List<Message> messages) {

        //마지막 사용자 메시지 추출
        String userInput = extractLastUserMessage(messages);

        // 추천 키워드 걸리면 바로 recommendAgent 사용
        if(needsRecommendation(userInput)) {
            return recommendAgent.recommend(userInput);
        }

        //prompt(): prompt builder 시작
        //system(): system prompt 설정
        //messages(): 사용자 대화 history 전달
        //tools(): LLM에게 사용할 수 있는 tools 등록(searchTool, etc.)
        //call(): LLM API 호출
        //content(): 최종 text 추출(LLM 응답)

        return chatClient.prompt()
                .system(prompt.getOrchestrator())
                .messages(messages)
                .tools(weatherTool, exchangeRateTool)
                .call()
                .content();
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

    // recommendAgent 로 보낼 키워드 filtering
    private boolean needsRecommendation(String userInput) {

        if (userInput == null) return false;

        String text = userInput.toLowerCase();

        return text.contains("추천") ||
                text.contains("맛집") ||
                text.contains("여행") ||
                text.contains("갈만한") ||
                text.contains("어디") ||

                text.contains("recommend") ||
                text.contains("suggest") ||
                text.contains("restaurant") ||
                text.contains("travel") ||
                text.contains("place to go");
    }

}
