package org.tourBot.ai.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.tourBot.ai.prompt.PromptProperties;

@Slf4j
@Component
public class RecommendAgent {

    private final ChatClient chatClient;
    private final PromptProperties prompt;

    public RecommendAgent(ChatClient.Builder builder,
                          PromptProperties prompt) {

        // RecommendAgent 전용 모델 설정
        this.chatClient = builder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.3)  // 검색은 낮게
                        .build())
                .build();
        this.prompt = prompt;
    }

    @Tool(
            name = "recommend",
            description = "Sum up and filter the data to make recommendations."
            + "come up with best suitable recommendation in relation to the context and the user's preference."
    )
    public String recommend(String query) {

        log.info("Recommend Agent Called: {}", query);

        return chatClient.prompt()
                .system(prompt.getRecommendAgent())
                .user(query)
                .call()
                .content();
    }
}
