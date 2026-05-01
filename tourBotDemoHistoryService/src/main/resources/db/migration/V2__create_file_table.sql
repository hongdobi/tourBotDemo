CREATE TABLE file (
    file_id VARCHAR(255) PRIMARY KEY,
    file_name TEXT,
    file_path TEXT,
    extension VARCHAR(50),
    created_at TIMESTAMP,
    status VARCHAR(50),
    chunk_count INT
);