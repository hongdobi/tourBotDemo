package org.tourBot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tourBot.client.HistoryClient;
import org.tourBot.dto.ChatRequest;
import org.tourBot.dto.ChatResponse;
import org.tourBot.dto.HistoryDto;
import org.tourBot.service.AiService;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final HistoryClient historyClient;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return aiService.chat(request);
    }

    @GetMapping("/history")
    public List<HistoryDto> getHistory(
            @RequestParam String userId,
            @RequestParam String sessionId
    ) {
        return historyClient.getHistory(userId, sessionId);
    }
}
