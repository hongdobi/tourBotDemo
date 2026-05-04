-- 1. trigger function
CREATE OR REPLACE FUNCTION vector_store_tsvector_update()
RETURNS trigger AS $$
BEGIN
  NEW.content_tsv := to_tsvector('simple', COALESCE(NEW.content, ''));
RETURN NEW;
END
$$ LANGUAGE plpgsql;

-- 2. trigger
DROP TRIGGER IF EXISTS tsvector_update ON vector_store;

CREATE TRIGGER tsvector_update
BEFORE INSERT OR UPDATE
ON vector_store
FOR EACH ROW
EXECUTE FUNCTION vector_store_tsvector_update();

-- backfill
UPDATE vector_store
SET content_tsv = to_tsvector('simple', COALESCE(content, ''))
WHERE content_tsv IS NULL;