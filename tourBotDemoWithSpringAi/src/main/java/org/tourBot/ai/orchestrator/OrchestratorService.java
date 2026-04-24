package org.tourBot.ai.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.stereotype.Service;
import org.tourBot.ai.agent.RecommendAgent;
import org.tourBot.ai.prompt.PromptProperties;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final ChatClient chatClient;
    private final RecommendAgent recommendAgent;
    private final PromptProperties prompt;

    public String handle(List<Message> messages) {

        //prompt(): prompt builder 시작
        //system(): system prompt 설정
        //messages(): 사용자 대화 history 전달
        //tools(): LLM에게 사용할 수 있는 tools 등록(searchTool, etc.)
        //call(): LLM API 호출
        //content(): 최종 text 추출(LLM 응답)
        return chatClient.prompt()
                .system(prompt.getOrchestrator())
                .messages(messages)
                .toolCallbacks(ToolCallbacks.from(recommendAgent))
                .call()
                .content();
    }

}
