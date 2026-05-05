-- trigger 에서 사용할 function 생성
-- COALESCE: ISNULL
-- to_tsvector: tsvector 자료형으로 변환(문장 -> 토큰화)
-- 변환된 content 를 content_tsv 에 저장
CREATE OR REPLACE FUNCTION vector_store_tsvector_update()
RETURNS trigger AS $$
BEGIN
  NEW.content_tsv := to_tsvector('simple', COALESCE(NEW.content, ''));
RETURN NEW;
END
$$ LANGUAGE plpgsql;

-- trigger 생성
-- EACH ROW INSERT OR UPDATE 마다 vector_store_tsvector_update() 호출
DROP TRIGGER IF EXISTS tsvector_update ON vector_store;

CREATE TRIGGER tsvector_update
BEFORE INSERT OR UPDATE
ON vector_store
FOR EACH ROW
EXECUTE FUNCTION vector_store_tsvector_update();

-- 기존 data 중, content_tsv 가 없는 건만 tsvector 자료형으로 변환하여 update
-- migration 시 한 번만 실행
UPDATE vector_store
SET content_tsv = to_tsvector('simple', COALESCE(content, ''))
WHERE content_tsv IS NULL;