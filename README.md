# TourBot Demo - AI Chat Service (Spring AI + MSA)

Spring AI를 활용한 AI 채팅 서비스로,
Microservices Architecture 기반으로 AI 응답 서비스와 
채팅 히스토리 저장 서비스를 분리하여 구성하였습니다.

Docker Compose를 통해 전체 시스템을 한 번에 실행할 수 있습니다.

# Architecture
- Orchestrator LLM: 사용자 질문을 분석하여 필요한 tool을 판단하는 LLM 
- Search Tool: 외부 데이터 검색 Tool(whether API, exchange rate API and etc.)
- Rag Tool: Vector DB 조회 Tool
- DB Tool: 내부 DB 조회 Tool
- Recommend Agent: 조합/추천 담당 AI

```
                [User]
                   ↓
        ┌────────────────────┐
        │  Orchestrator LLM  │  ← (Planner)
        └────────────────────┘
             ↓        ↓
     (tool 선택)   (직접 응답)

   ┌───────────────┬───────────────┬───────────────┐
   │ Search Tool   │  RAG Tool     │  DB Tool      │
   │ (외부데이터)  │ (Vector DB)   │ (내부데이터)  │
   └───────────────┴───────────────┴───────────────┘
             ↓
   ┌────────────────────┐
   │  Recommend Agent   │ ← (LLM)
   └────────────────────┘
             ↓
   ┌────────────────────┐
   │  Orchestrator LLM  │ ← (최종 정리)
   └────────────────────┘
             ↓
           응답
```

# Tech Stack
- Java 21
- Spring Boot
- Spring AI
- MyBatis
- PostgreSQL
- LLM
- Docker / Docker Compose

# Run with Docker
- git clone https://github.com/hongdobi/tourBotDemo.git
- cd tourBotDemo
- docker compose up --build

# Service
- AI 기반 채팅 응답 생성
- 세션 기반 대화 기록 저장
- Microservices 구조 분리
- Docker 기반 통합 실행

# Project Structure
```
tourBotDemo/
 ├── tourBotDemoWithSpringAi   (AI Service)
 ├── tourBotDemoHistoryService (History Service)
 ├── docker-compose.yml
 ├── .env.example
 └── README.md
```
 

