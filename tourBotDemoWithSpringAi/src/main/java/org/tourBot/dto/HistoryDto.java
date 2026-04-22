package org.tourBot.dto;

import lombok.Getter;
import lombok.Setter;
import org.tourBot.domain.Role;

@Getter
@Setter
public class HistoryDto {
    private String role;
    private String content;

    public Role getRoleEnum() {
        return Role.from(this.role);
    }
}
