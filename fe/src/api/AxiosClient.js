// src/api/axiosClient.js
import axios from "axios";
import { jwtDecode } from "jwt-decode";

const VITE_BASE_URL = import.meta.env.VITE_API_URL;

const AxiosClient = axios.create({
  baseURL: VITE_BASE_URL || "http://localhost:8082/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

// request interceptor
AxiosClient.interceptors.request.use(
  async (config) => {
    let accessToken = localStorage.getItem("AccessToken");
    const refreshToken = localStorage.getItem("RefreshToken");

    if (config.withAuth && accessToken) {
      if (jwtDecode(accessToken).exp * 1000 < Date.now()) {
        if (jwtDecode(refreshToken).exp * 1000 < Date.now()) {
          window.location.href = "/auth/login";
        }
        try {
          const res = await axios.post(
            "http://localhost:8082/api/v1/auth/refresh",
            { refreshToken }
          );
          localStorage.setItem("AccessToken", res.data.accessToken);
          localStorage.setItem("RefreshToken", res.data.refreshToken);
          accessToken = res.data.accessToken;
        } catch {
          window.location.href = "/auth/login";
        }
      }
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

AxiosClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error.response?.status;
    if (
      status === 401 ||
      status === 403 ||
      error.response.data == "Access Denied"
    ) {
      // window.location.href = "/auth/login";
    }
    return Promise.reject(error);
  }
);

export default AxiosClient;
