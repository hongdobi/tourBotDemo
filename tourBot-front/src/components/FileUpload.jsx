import api from "../api/client";

export default function FileUpload() {
  const upload = async (e) => {
    const file = e.target.files[0];

    const formData = new FormData();
    formData.append("file", file);

    await api.post("/rag/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    alert("업로드 완료");
  };

  return <input type="file" onChange={upload} />;
}