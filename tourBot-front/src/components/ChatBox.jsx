import { useState, useRef, useEffect } from "react";
import api from "../api/client";

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
    <div className="bg-white rounded-2xl shadow flex flex-col h-[600px]">

      {/* 메시지 영역 */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((msg, idx) => (
          <div
            key={idx}
            className={`flex ${
              msg.role === "user" ? "justify-end" : "justify-start"
            }`}
          >
            <div
              className={`
                px-4 py-3 rounded-2xl max-w-[75%] whitespace-pre-wrap
                ${
                  msg.role === "user"
                    ? "bg-green-400 text-white"
                    : "bg-gray-200 text-gray-800"
                }
              `}
            >
              {msg.text}
            </div>
          </div>
        ))}

        {/* 로딩 */}
        {loading && (
          <div className="flex">
            <div className="bg-gray-200 text-gray-500 px-4 py-3 rounded-2xl animate-pulse">
              AI가 답변 작성 중...
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* 입력 영역 */}
      <div className="border-t p-3 flex gap-2">
        <input
          className="flex-1 border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="메시지 입력..."
        />
        <button
          onClick={send}
          className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg"
        >
          전송
        </button>
      </div>
    </div>
  );
}