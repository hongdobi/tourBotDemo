package org.tourBot.ai.rag.service;

import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.tourBot.client.HistoryClient;
import org.tourBot.domain.Role;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RagService {

    private final VectorStore vectorStore;
    private final HistoryClient historyClient;

    public String upload(MultipartFile file) {
        return historyClient.upload(file);
    }

    public void ingest(String fileId, String filePath, String fileName)  {

        try (InputStream is = new FileInputStream(filePath)){
            // file에서 전체 text 추출
            String text = extractText(is);

            // 추출한 text chunking
            List<String> chunks = chunk(text);

            // chunking text를 Document로 변환해서 리스트에 담고,
            // Embedding 생성 -> vector 변환 -> pgvector DB 저장
            vectorStore.add(
                    chunks.stream()
                            .map(chunkedText -> new Document(
                                    chunkedText,
                                    Map.of(
                                            "fileId", fileId,
                                            "source", "file",
                                            "fileName", fileName
                                    )
                            ))
                            .toList()
            );

        } catch(Exception e) {
            throw new RuntimeException("RAG ingest failed", e);
        }
    }

    private String extractText(InputStream is) throws Exception {

        // Apache Tika 파일 parser 생성
        AutoDetectParser parser = new AutoDetectParser();
        // 추출된 text 담는 객체(-1: 길이 무제한)
        BodyContentHandler handler = new BodyContentHandler(-1);

        // 파일 메타데이터 생성(작성자, 파일타입, 페이지 정보 등)
        Metadata metadata = new Metadata();

        //text 추출 실행
        parser.parse(is, handler, metadata);

        // pdf에서 추출된 text 전체 반환
        return handler.toString();
    }

    private List<String> chunk(String text) {

        int chunkSize = 500;
        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < text.length(); i += chunkSize) {
            // 전체 text 길이 vs. chunkSize+1 중에 더 큰 쪽만큼 text 자르기
            chunks.add(text.substring(i, Math.min(text.length(), i + chunkSize)));
        }

        // 잘라낸 text 반환
        return chunks;
    }

    // 파일 삭제 시 vector에서 삭제
    public void delete(String fileId) {

        FilterExpressionBuilder b = new FilterExpressionBuilder();

        vectorStore.delete(b.eq("fileId", fileId).build());
    }
}
