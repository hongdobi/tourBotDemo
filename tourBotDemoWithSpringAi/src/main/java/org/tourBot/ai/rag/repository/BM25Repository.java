package org.tourBot.ai.rag.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BM25Repository {

    private final JdbcTemplate jdbcTemplate;

    public List<Document> search(String query, String fileId, int topK) {

        String sql = """
            SELECT content, file_id, file_name,
                   ts_rank(content_tsv, plainto_tsquery(?)) AS score
            FROM vector_store
            WHERE content_tsv @@ plainto_tsquery(?)
            %s
            ORDER BY score DESC
            LIMIT ?
        """.formatted(
                (fileId != null && !fileId.isBlank())
                        ? "AND file_id = ?"
                        : ""
        );

        List<Object> params = new ArrayList<>();
        params.add(query);
        params.add(query);

        if (fileId != null && !fileId.isBlank()) {
            params.add(fileId);
        }

        params.add(topK);

        return jdbcTemplate.query(
            sql,
            ps -> {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
            },
            (rs, rowNum) -> {
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("fileId", rs.getString("file_id"));
                metadata.put("source", "file");
                metadata.put("fileName", rs.getString("file_name"));

            return new Document(
                    rs.getString("content"),
                    metadata
            );
        });
    }
}
