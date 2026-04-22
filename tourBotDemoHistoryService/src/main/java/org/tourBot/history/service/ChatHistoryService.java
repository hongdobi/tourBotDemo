package org.tourBot.history.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tourBot.history.domain.ChatHistory;
import org.tourBot.history.dto.ChatRequest;
import org.tourBot.history.mapper.ChatHistoryMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatHistoryMapper mapper;

    public List<ChatHistory> getHistory(String userId, String sessionId) {
        return mapper.getHistory(userId, sessionId);
//        List<ChatHistory> blank = new ArrayList<>();
//        return blank;
    }

    public void saveHistory(ChatRequest request) {
        ChatHistory chatHistory = ChatHistory.builder()
            .userId(request.getUserId())
            .sessionId(request.getSessionId())
            .role(request.getRole())
            .content(request.getContent())
            .build();

        mapper.saveHistory(chatHistory);
    }
}
