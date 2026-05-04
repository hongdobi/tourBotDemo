import { useRef, useState } from "react";
import api from "../api/client";

export default function FileUpload() {
  const fileInputRef = useRef(null);
  const [fileName, setFileName] = useState("");

  const handleClick = () => {
    fileInputRef.current.click();
  };

  const upload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setFileName(file.name);

    const formData = new FormData();
    formData.append("file", file);

    await api.post("/rag/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    alert("업로드 완료");
  };

  return (
    <div className="bg-white rounded-2xl shadow p-4 flex items-center justify-between">

      <input
        type="file"
        ref={fileInputRef}
        onChange={upload}
        className="hidden"
      />

      <span className="text-sm text-gray-600">
        {fileName || "업로드 할 파일을 선택해주세요."}
      </span>

      <button
        onClick={handleClick}
        className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg"
      >
        업로드
      </button>
    </div>
  );
}