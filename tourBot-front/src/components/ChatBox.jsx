import { useState, useRef, useEffect } from "react";
import api from "../api/client";
import "./ChatBox.css";

export default function ChatBox() {
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const messagesEndRef = useRef(null);
  const userId = "hongdobi";

  // 자동 스크롤
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

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
      });

      const botMsg = {
        role: "bot",
        text: res.data.answer,
      };

      setMessages((prev) => [...prev, botMsg]);
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

  return (
    <div className="container">
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