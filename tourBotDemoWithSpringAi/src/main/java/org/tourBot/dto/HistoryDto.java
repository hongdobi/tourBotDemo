package org.tourBot.dto;

import lombok.Getter;
import lombok.Setter;
import org.tourBot.domain.Role;

import java.time.LocalDateTime;

@Getter
@Setter
public class HistoryDto {
    private Long id;
    private String userId;
    private String sessionId;
    private String role;
    private String content;
    private LocalDateTime createdAt;

    public Role getRoleEnum() {
        return Role.from(this.role);
    }
}
