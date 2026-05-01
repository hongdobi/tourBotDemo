package org.tourBot.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RagRequest {

    private String question;

    // optional (특정 파일만 검색할 때)
    private String fileId;
}
