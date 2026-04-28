SELECT 'CREATE DATABASE chatdb'
    WHERE NOT EXISTS (
  SELECT FROM pg_database WHERE datname = 'chatdb'
)\gexec

SELECT 'CREATE DATABASE vectordb'
    WHERE NOT EXISTS (
  SELECT FROM pg_database WHERE datname = 'vectordb'
)\gexec