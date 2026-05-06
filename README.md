# TourBot Demo - AI Chat Service  
**(Spring AI + Planner + Tool Orchestration + RAG + MSA)**

> LLM을 직접 제어하지 않고,  
> **Planner + Code Orchestration + Agent 구조로 구성된 실무형 AI 챗봇 서비스**

### updated: 2026-05-06 (KST)

---

# Core Architecture

```
                    [ User ]
                        ↓
                ┌───────────────┐
                │   Frontend    │  (React)
                └───────────────┘
                        ↓ (HTTP)

        ┌──────────────────────────────────┐
        │          AI-service              │
        │                                  │
        │   ┌──────────────────────────┐   │
        │   │        Planner           │   │
        │   │  (Rule + LLM Hybrid)    │   │
        │   └──────────────────────────┘   │
        │                ↓                 │
        │        (Execution Plan)          │
        │                ↓                 │
        │   ┌──────────────────────────┐   │
        │   │   Orchestrator (Code)    │   │
        │   │   - Tool Execution       │   │
        │   │   - Flow Control         │   │
        │   └──────────────────────────┘   │
        │        ↓             ↓           │
        │   (RAG Search)   (External API)  │
        │        ↓             ↓           │
        │  ┌──────────────┬──────────────┐ │
        │  │  Weather     │  Exchange    │ │
        │  │   Tool       │   Tool       │ │
        │  └──────────────┴──────────────┘ │
        │              ↓                   │
        │      ┌──────────────────┐        │
        │      │  RAG Retriever   │        │
        │      └──────────────────┘        │
        │              ↓                   │
        │      ┌──────────────────┐        │
        │      │   Reranker       │        │
        │      │  (현재 simple)   │        │
        │      └──────────────────┘        │
        │              ↓                   │
        │   ┌──────────────────────────┐   │
        │   │   Recommend Agent        │   │
        │   │  (LLM Answer Generator) │   │
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

---

# Core Components

## Planner
- Rule + LLM Hybrid
- Tool 사용 여부 결정
```
Plan {
    useWeather: boolean
    useExchangeRate: boolean
    useRag: boolean
}
```

## Orchestrator
- Planner 결과 기반 실행 흐름 제어
- Tool 실행
- 데이터 수집 및 조합
- LLM Agent 호출

## Tools
- Weather API: 외부 API 기반 실시간 날씨 조회
- Exchange Rate API: 환율 정보 조회

## RAG
- PostgreSQL + pgvector 기반 검색
- 문서 임베딩 저장 및 유사도 검색

## RecommendAgent
- 최종 응답 생성
- Context 기반 reasoning
- 추천 / QA / 잡담 모두 처리

---

# Request Flow

1. User Input
2. Planner
3. Orchestrator:
   - Tool execution
   - RAG search
   - Rerank
4. RecommendAgent → Response

---

# Key Features

- Planner 기반 Tool 선택
- Code-based Orchestration (LLM 의존도 감소)
- RAG 기반 Context 강화 (Hallucination 감소)
- 실시간 데이터 연동 (Weather / Exchange)
- Single Agent Multi-role
- Microservices Architecture

---

# Tech Stack

- LLM: gpt-4.1 (or configurable)
- Embedding: text-embedding-3-small
- Backend: Java 21, Spring Boot, Spring AI
- Frontend: React
- Database: PostgreSQL + pgvector
- Infra: Docker Compose

---

# Run

```
git clone https://github.com/hongdobi/tourBotDemo.git
cd tourBotDemo
docker compose up --build
```

---

# Structure

```
tourBotDemo/
 ├── tourBot-front (Frontend UI)
 ├── tourBotDemoWithSpringAi   (AI Service)
 ├── tourBotDemoHistoryService (History Service)
 ├── docker-compose.yml
 ├── .env.example
 └── README.md
```

---

# Essential

"LLM을 사용하는 시스템이 아니라, LLM을 통제하는 시스템"
