CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS vector_store (
    id uuid PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(1536),
    content_tsv tsvector
);

CREATE INDEX IF NOT EXISTS idx_vector_store_tsv
ON vector_store
USING GIN(content_tsv)
WITH (fastupdate = on);

