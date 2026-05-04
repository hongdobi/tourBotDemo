package org.tourBot.ai.rag.service;

import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tourBot.ai.rag.dto.RagRequest;
import org.tourBot.ai.rag.dto.RagResponse;
import org.tourBot.ai.rag.dto.Source;
import org.tourBot.client.HistoryClient;
import org.springframework.ai.chat.client.ChatClient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final VectorStore vectorStore;
    private final HistoryClient historyClient;
    private final ChatClient chatClient;

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

        int chunkSize = 800;
        int overlap = 200;
        List<String> chunks = new ArrayList<>();

        int start = 0;

        while (start < text.length()) {

            // text 전체와 start + chunkSize 중 작은 것 => end
            int end = Math.min(text.length(), start + chunkSize);

            // 문장 끝 기준 찾기(end부터 .까지 길이 반환)
            int lastPeriod = text.lastIndexOf(".", end);
            if (lastPeriod > start) {
                end = lastPeriod + 1;
            }

            chunks.add(text.substring(start, end));

            // overlap 되도록 start index를 다시 세팅
            start += (chunkSize - overlap);
        }

        // 잘라낸 text 반환
        return chunks;
    }

    // 파일 삭제 시 vector에서 삭제
    public void delete(String fileId) {

        FilterExpressionBuilder b = new FilterExpressionBuilder();

        vectorStore.delete(b.eq("fileId", fileId).build());
    }

    // vector search
    public RagResponse ask(RagRequest request) {

        List<Document> docs = similaritySearch(
                request.getQuestion(),
                request.getFileId()
        );

        if (docs.isEmpty()) {
            return new RagResponse(
                    "관련 문서를 찾을 수 없습니다.",
                    List.of()
            );
        }

        List<Source> sources = docs.stream()
                .map(doc -> new Source(
                        String.valueOf(doc.getMetadata().get("fileId")),
                        String.valueOf(doc.getMetadata().get("fileName")),
                        doc.getText()
                ))
                .toList();

        // context 생성
        String context = sources.stream()
                .map(s -> "[출처: " + s.getFileName() + "]\n" + s.getContent())
                .collect(Collectors.joining("\n\n"));

        String prompt = """
                You are a document-based QA system.
                
                [Rules]
                1. You MUST answer ONLY using the provided CONTEXT.
                2. NEVER use external knowledge or make assumptions.
                3. If the answer is not in the context, respond exactly:
                   "Insufficient information in the document."
                4. Each answer must include its source (e.g., [guide.pdf]).
                5. Keep the answer concise and factual.

                [Example Output]
                Answer:
                This feature allows users to upload documents.
                
                Sources:
                - guide.pdf
                
                [Context]
                %s
                
                [Question]
                %s
                
                [Answer Format]
                Answer:
                <answer>
                
                Sources:
                - <fileName>
                
                [Answer]
                """.formatted(context, request.getQuestion());

        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return new RagResponse(answer, sources);
    }

    // vectorDB 조회
    public List<Document> similaritySearch(String question, String fileId) {

        SearchRequest.Builder builder = SearchRequest.builder()
                .query(question)
                .topK(10);

        // metadata filter (정석 방식)
        if (fileId != null && !fileId.isBlank()) {

            FilterExpressionBuilder feb = new FilterExpressionBuilder();

            Filter.Expression filter = feb.eq("fileId", fileId).build();

            builder.filterExpression(filter);
        }

        return vectorStore.similaritySearch(builder.build());
    }
}
