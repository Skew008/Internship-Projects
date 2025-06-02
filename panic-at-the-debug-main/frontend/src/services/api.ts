import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080", // Your backend API base URL
});

axiosInstance.interceptors.request.use((config) => {
  // Prevent adding Authorization header for public routes
  if (
    config.url === "/auth/register" ||
    config.url === "/auth/login" ||
    config.url?.startsWith("/auth/")
  ) {
    delete config.headers["Authorization"]; // Remove Authorization header for public endpoints
  }

  const token = localStorage.getItem('accessToken'); // Get the token from localStorage
  if (token) {
    config.headers.Authorization = `Bearer ${token}`; // Add Authorization header
  }
  return config;
  
});

export default axiosInstance;