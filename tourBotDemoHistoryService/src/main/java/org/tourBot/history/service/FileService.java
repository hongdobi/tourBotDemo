package org.tourBot.history.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tourBot.history.client.AiClient;
import org.tourBot.history.dto.FileDto;
import org.tourBot.history.mapper.FileMapper;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper fileMapper;
    private final AiClient aiClient;

    private final String uploadDir = "/data/uploads/";

    // 업로드
    public String upload(MultipartFile file) {

        try {
            String fileId = UUID.randomUUID().toString();

            String originalFilename = Optional.ofNullable(file.getOriginalFilename())
                    .orElse("unknown.pdf");

            String extension = originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                    : "unknown";

            String filePath = uploadDir + fileId + "." + extension;

            // 파일 저장
            File dest = new File(filePath);
            file.transferTo(dest);

            // DB 저장 (MyBatis)
            FileDto dto = new FileDto();
            dto.setFileId(fileId);
            dto.setFileName(originalFilename);
            dto.setFilePath(filePath);
            dto.setExtension(extension);
            dto.setCreatedAt(LocalDateTime.now());

            fileMapper.insertFile(dto);

            // AI 서비스 호출
            aiClient.ingest(fileId, filePath, originalFilename);

            return fileId;

        } catch (Exception e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    // 파일 삭제
    public void delete(String fileId) {

        FileDto file = fileMapper.findById(fileId);

        if (file == null) {
            throw new RuntimeException("File not found");
        }

        // 실제 파일 삭제
        File physicalFile = new File(file.getFilePath());
        if (physicalFile.exists() && !physicalFile.delete()) {
            log.warn("파일 삭제 실패: {}", file.getFilePath());
        }

        // AI 서비스 vector 삭제
        aiClient.delete(fileId);

        // DB 삭제
        fileMapper.deleteById(fileId);
    }
}
