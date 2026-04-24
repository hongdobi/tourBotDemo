package org.tourBot.ai.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.tourBot.ai.prompt.PromptProperties;

@Slf4j
@Component
public class SearchAgent {

    private final ChatClient chatClient;
    private final PromptProperties prompt;

    public SearchAgent(ChatClient.Builder builder,
                       PromptProperties prompt) {

        // SearchAgent 전용 모델 설정
        this.chatClient = builder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.3)  // 검색은 낮게
                        .build())
                .build();
        this.prompt = prompt;
    }

    @Tool(
            name = "search",
            description = """
            Search for real-time or up-to-date information 
            such as weather, news, stock prices, and current events
            """
    )
    public String search(String query) {

        log.info("Search Tool Called: {}", query);

        return chatClient.prompt()
                .system(prompt.getSearchAgent())
                .user(query)
                .call()
                .content();
    }
}
