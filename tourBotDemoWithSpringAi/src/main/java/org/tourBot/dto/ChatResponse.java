package org.tourBot.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatResponse {

    String sessionId;
    String answer;
}
