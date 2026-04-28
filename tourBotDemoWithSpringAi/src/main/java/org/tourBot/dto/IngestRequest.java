package org.tourBot.dto;

import lombok.Data;

@Data
public class IngestRequest {

    private String fileId;
    private String filePath;
    private String fileName;
}
