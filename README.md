# TourBot Demo - AI Chat Service  
**(Spring AI + Planner + Tool Orchestration + RAG + MSA)**

```
> "LLM을 사용하는 시스템이 아니라, LLM을 통제하는 시스템"
> **Planner + Code Orchestration + Agent 구조로 구성된 실무형 AI 챗봇 서비스**
```

### updated: 2026-05-12 (KST)

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

# My Contributions

- Spring AI 기반 Custom Orchestrator 설계 및 구현
- Planner + Rule + LLM Hybrid 구조 설계
- pgvector 기반 RAG 검색 시스템 구현
- Vector Search + BM25 Hybrid Search 직접 구현
- Tool 호출 흐름 및 Agent chaining 구조 설계
- Recommend Agent 설계 (multi-role 처리)


## 왜 LangChain을 쓰지 않았는가?
- Java 기반 서비스 환경과의 통합성 문제
- Tool 제어를 LLM에 맡기는 구조의 한계
→ Code-based Orchestration으로 전환

## 왜 Planner를 분리했는가?
- LLM 단일 호출로 Tool 선택 시 불안정성 존재
→ Rule + LLM Hybrid 구조로 안정성 확보

## 왜 Hybrid Search를 사용했는가?
- Vector Search: 의미 기반 강점
- BM25: 키워드 정확도 강점
→ 두 방식 결합으로 검색 품질 개선


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
create .env in reference with .env.example
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
