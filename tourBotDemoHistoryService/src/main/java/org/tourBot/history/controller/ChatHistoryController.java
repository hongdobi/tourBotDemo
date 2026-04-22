package org.tourBot.history.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tourBot.history.domain.ChatHistory;
import org.tourBot.history.dto.ChatRequest;
import org.tourBot.history.service.ChatHistoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class ChatHistoryController {

    private final ChatHistoryService service;

    @GetMapping("/getHistory")
    public List<ChatHistory> getHistory(@RequestParam String userId, @RequestParam String sessionId) {
        return service.getHistory(userId, sessionId);
    }

    @PostMapping("/saveHistory")
    public void saveHistory(@RequestBody ChatRequest request) {
        service.saveHistory(request);
    }
}
