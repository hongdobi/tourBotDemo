# TourBot Demo - AI Chat Service (Spring AI + MSA)

Spring AI를 활용한 AI 채팅 서비스로,
Microservices Architecture 기반으로 AI 응답 서비스와 
채팅 히스토리 저장 서비스를 분리하여 구성하였습니다.

Docker Compose를 통해 전체 시스템을 한 번에 실행할 수 있습니다.

# Architecture
- AI Service: Spring AI 기반 LLM 응답 처리
- History Service: PostgreSQL 기반 채팅 기록 저장
- sessionId 기반 대화 상태 유지
- 동일 userId + sessionId로 history 저장
- PostgreSQL: 데이터 저장소
- Docker Compose: 전체 서비스 orchestration

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
 

