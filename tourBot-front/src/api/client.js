import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

api.interceptors.request.use((config) => {
  config.headers["X-USER-ID"] = "hongdobi";
  return config;
});

export default api;