CREATE EXTENSION IF NOT EXISTS vector;

-- vector_store table 생성
CREATE TABLE vector_store (
    id uuid PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(1536),
    content_tsv tsvector
);

-- vector_store table 내 index 추가(GIN Index 사용해서 content_tsv 에 index)
-- fastupdate = on: insert 시 바로 index 만들지 말고, 메모리에 모아뒀다가 한번에 처리
-- ingest 빠르게 처리
CREATE INDEX IF NOT EXISTS idx_vector_store_tsv
ON vector_store
USING GIN(content_tsv)
WITH (fastupdate = on);

