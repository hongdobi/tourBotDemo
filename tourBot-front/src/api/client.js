import axios from "axios";

const api = axios.create({
//  baseURL: import.meta.env.VITE_API_URL,
  baseURL: "http://localhost:8090/api",
});

api.interceptors.request.use((config) => {
  config.headers["X-USER-ID"] = "hongdobi";
  return config;
});

export default api;