package org.tourBot.history.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatRequest {

    String userId;
    String sessionId;
    String role;
    String content;
}
