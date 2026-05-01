package org.tourBot.ai.rag.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tourBot.ai.rag.dto.RagRequest;
import org.tourBot.ai.rag.dto.RagResponse;
import org.tourBot.ai.rag.service.RagService;
import org.tourBot.dto.IngestRequest;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) {
        return ragService.upload(file);
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingest(@RequestBody IngestRequest req) {

            ragService.ingest(
                    req.getFileId(),
                    req.getFilePath(),
                    req.getFileName()
            );
        return ResponseEntity.ok("FILE INGESTED OK");
    }

    @DeleteMapping("/{fileId}")
    public String delete(@PathVariable String fileId) {

        ragService.delete(fileId);

        return "deleted";
    }

    @PostMapping("/ask")
    public ResponseEntity<RagResponse> ask(@RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.ask(request));
    }
}
