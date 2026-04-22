package org.tourBot.history.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatHistory {

    private Long id;
    private String userId;
    private String sessionId;
    private String role;
    private String content;
    private LocalDateTime createdAt;
}
