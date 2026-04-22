package org.tourBot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourBot.domain.Role;
import org.tourBot.dto.ChatRequest;
import org.tourBot.dto.ChatResponse;
import org.tourBot.dto.HistoryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiService {

    @Autowired
    private final ChatClient chatClient;
    @Autowired
    private final HistoryClientService historyClientService;

    public ChatResponse chat(ChatRequest request) {

        String sessionId = request.getSessionId();

        // sessionId 없으면 생성
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }

        // 이전 대화 가져오기
        List<HistoryDto> history = historyClientService.getHistory(request.getUserId(), sessionId);

        // LLM 메시지 구성
        List<Message> messages = new ArrayList<>();

        for (HistoryDto h : history) {
            Role role = h.getRoleEnum();

            switch (role) {
                case USER -> messages.add(new UserMessage(h.getContent()));
                case ASSISTANT -> messages.add(new AssistantMessage(h.getContent()));
            }
        }

        // 현재 사용자 메시지 추가
        messages.add(new UserMessage(request.getMessage()));

        // LLM 호출
        String answer = chatClient.prompt()
                .messages(messages)
                .call()
                .content();

        // 히스토리 저장
        historyClientService.saveHistory(request.getUserId(), sessionId, Role.USER, request.getMessage());
        historyClientService.saveHistory(request.getUserId(), sessionId, Role.ASSISTANT, answer);

        // 6. 응답
        return ChatResponse.builder()
                .sessionId(sessionId)
                .answer(answer)
                .build();
    }
}
