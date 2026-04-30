# TourBot Demo - AI Chat Service (Spring AI + LLM + RAG + MSA)

> Spring AI 기반으로 Orchestrator LLM이 Tool, RAG, Agent를 동적으로 조합하는  
> Microservices Architecture 기반 AI 챗봇 서비스

### updated: 2026-04-30 (KST)

## Service Architecture
```
                    [ User ]
                        ↓
                ┌───────────────┐
                │   Frontend    │  (React)
                └───────────────┘
                        ↓ (HTTP)

        ┌──────────────────────────────────┐
        │          AI-service              │
        │  (Spring AI / Orchestration)     │
        │                                  │
        │   ┌──────────────────────────┐   │
        │   │   Orchestrator LLM       │   │
        │   │ (Planner & Controller)   │   │
        │   └──────────────────────────┘   │
        │        ↓             ↓           │
        │   (tool 호출)    (직접 응답)     │
        │        ↓                         │
        │  ┌──────────────┬──────────────┐ │
        │  │ Weather Tool │ Exchange Tool│ │
        │  │ (외부 API)   │ (외부 API)   │ │
        │  └──────────────┴──────────────┘ │
        │              ↓                   │
        │     ┌──────────────────────┐     │
        │     │   Recommend Agent    │     │
        │     │      (LLM)           │     │
        │     └──────────────────────┘     │
        │              ↓                   │
        │   ┌──────────────────────────┐   │
        │   │   Orchestrator LLM       │   │
        │   │   (Final Response)       │   │
        │   └──────────────────────────┘   │
        └──────────────────────────────────┘
                        ↓ (HTTP)

        ┌──────────────────────────────────┐
        │        History-service           │
        │                                  │
        │   ┌──────────────────────────┐   │
        │   │       DB Service         │   │
        │   │     (PostgreSQL)         │   │
        │   └──────────────────────────┘   │
        │                                  │
        │   ┌──────────────────────────┐   │
        │   │       RAG Service        │   │
        │   │   (PostgreSQL Vector)    │   │
        │   └──────────────────────────┘   │
        └──────────────────────────────────┘

                (Docker Compose로 전체 실행)
```


### 1. Frontend
- React 기반 챗봇 UI

### 2. AI-service
- Orchestrator LLM (Planner & Controller)
  - 사용자 질문을 분석하여 Tool / RAG / Agent 호출 여부를 결정
  - 전체 응답 흐름을 관리하는 핵심 컴포넌트
- Weather Tool (외부 API)
- Exchange Rate Tool (외부 API)
- Recommend Agent (추천/조합 AI)

### 3. History-service
- DB Service (PostgreSQL)
  - 대화 히스토리 및 사용자 데이터 저장
- RAG Service (PostgreSQL + pgvector)
  - 문서 임베딩 저장 및 유사도 검색
  - LLM 응답 정확도 향상


## Key Features
- Orchestrator LLM 기반 동적 Tool / Agent 선택
- RAG 기반 컨텍스트 강화 응답 (Hallucination 감소)
- 외부 API 연동 (날씨, 환율 등 실시간 데이터)
- 세션 기반 대화 히스토리 관리
- Microservices Architecture + Docker Compose 기반 통합 실행

## Tech Stack
- LLM: gpt-4o-mini
- Embedding: text-embedding-3-small
- Backend: Java 21, Spring Boot, Spring AI
- Frontend: React
- Database: PostgreSQL (RDB + pgvector)
- Infra: Docker Compose

# Run with Docker
- git clone https://github.com/hongdobi/tourBotDemo.git
- cd tourBotDemo
- docker compose up --build

# Service
- AI 기반 채팅 응답 생성
- 세션 기반 대화 기록 저장
- Real time data 외부 api 조회
- Postgres Vector 기반 문서 임베딩 저장 및 조회
- 조회된 데이터 ranking
- Microservices 구조 분리 및 Docker 기반 통합 실행

# Project Structure
```
tourBotDemo/
 ├── tourBot-front (Frontend UI)
 ├── tourBotDemoWithSpringAi   (AI Service)
 ├── tourBotDemoHistoryService (History Service)
 ├── docker-compose.yml
 ├── .env.example
 └── README.md
```
 

