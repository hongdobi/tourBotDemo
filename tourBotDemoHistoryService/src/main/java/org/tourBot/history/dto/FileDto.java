package org.tourBot.history.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileDto {

    private String fileId;
    private String fileName;
    private String filePath;
    private String extension;
    private LocalDateTime createdAt;
}
