import { useState, useRef, useEffect } from "react";
import api from "../api/client";
import "./ChatBox.css";

export default function ChatBox() {
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [sessionId, setSessionId] = useState(null);

  const messagesEndRef = useRef(null);
  const userId = "hongdobi";

  // 자동 스크롤
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  // sessionId localStorage에서 가져옴
  useEffect(() => {
    const savedSessionId = localStorage.getItem("sessionId");
    if (savedSessionId) {
      setSessionId(savedSessionId);
    }
  }, []);

  // sessionId 있으면, history api조회하여 대화기록 가져오기
  useEffect(() => {
    if (!sessionId) return;

    const loadHistory = async () => {
      try {
        const res = await api.get(`/ai/history`, {
            params: {
                userId,
                sessionId,
            }
        });

        const formatted = res.data.map((item) => ({
           role: item.role,
           text: item.content
        }));
        setMessages(formatted);
      } catch (e) {
        console.error("히스토리 로드 실패", e);
      }
    };

    loadHistory();
  }, [sessionId]);

  const send = async () => {
    if (!message.trim() || loading) return;

    const userMsg = { role: "user", text: message };
    setMessages((prev) => [...prev, userMsg]);

    setMessage("");
    setLoading(true);

    try {
      const res = await api.post("/ai/chat", {
        userId,
        message,
        sessionId,
      });

      const botMsg = {
        role: "bot",
        text: res.data.answer,
      };

      setMessages((prev) => [...prev, botMsg]);
      setSessionId(res.data.sessionId);
      localStorage.setItem("sessionId", res.data.sessionId);

    } catch (e) {
      setMessages((prev) => [
        ...prev,
        { role: "bot", text: "에러가 발생했습니다" },
      ]);
    } finally {
      setLoading(false);
    }
  };

  // Enter 전송
  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      send();
    }
  };

  // 새 채팅 버튼
  const newChat = () => {
    setSessionId(null);
    setMessages([]);
    localStorage.removeItem("sessionId");
  };

  return (
    <div className="container">
      <button onClick={newChat}>새 채팅</button>
      <div className="chat-area">
        {messages.map((msg, idx) => (
          <div
            key={idx}
            className={`message ${msg.role === "user" ? "user" : "bot"}`}
          >
            {msg.text}
          </div>
        ))}

        {/* 로딩 UI */}
        {loading && (
          <div className="message bot typing">
            AI가 답변 작성 중...
          </div>
        )}

        {/* 스크롤 anchor */}
        <div ref={messagesEndRef} />
      </div>

      <div className="input-area">
        <input
          className="input"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="메시지 입력..."
        />
        <button className="button" onClick={send}>
          전송
        </button>
      </div>
    </div>
  );
}