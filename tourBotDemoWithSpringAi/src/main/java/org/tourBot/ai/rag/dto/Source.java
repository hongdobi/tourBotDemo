package org.tourBot.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Source {

    private String fileId;
    private String fileName;
    private String content;
}
