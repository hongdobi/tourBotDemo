package org.tourBot.history.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tourBot.history.domain.ChatHistory;
import org.tourBot.history.dto.ChatRequest;
import org.tourBot.history.mapper.ChatHistoryMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatHistoryMapper mapper;

    public List<ChatHistory> getHistory(String userId, String sessionId) {

        List<ChatHistory> historyList = mapper.getHistory(userId, sessionId);
        log.debug("History List: {}", historyList);

        return historyList;
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
