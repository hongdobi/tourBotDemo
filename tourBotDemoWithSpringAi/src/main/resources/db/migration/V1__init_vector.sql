CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS vector_store (
    id uuid PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(1536)
);