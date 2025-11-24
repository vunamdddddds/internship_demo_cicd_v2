// src/api/SupportRequestApi.js
import AxiosClient from "./AxiosClient";

const ROOT = "/support-request";

const SupportRequestApi = {
  createSupportRequest: (formData) => {
    return AxiosClient.post(ROOT, formData, {
      withAuth: true,
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  getAllSupportRequest: ({ search = "", status = "", page = 1, size = 10 }) => {
    return AxiosClient.get(ROOT, {
      withAuth: true,
      params: {
        keyword: search?.trim(),
        status: status?.trim(),
        page: page,
        size: size,
      },
    });
  },

  approveSupportRequest: (supportId) => {
    return AxiosClient.put(`${ROOT}/approve/${supportId}`, null, {
      withAuth: true,
    });
  },

  inProgressSupportRequest: (supportId) => {
    return AxiosClient.put(`${ROOT}/inProgress/${supportId}`, null, {
      withAuth: true,
    });
  },

  rejectSupportRequest: (supportId, hrResponse) => {
    return AxiosClient.put(
      `${ROOT}/reject/${supportId}`,
      { hrResponse }, 
      { withAuth: true }
    );
  },

  cancelSupportRequest: (supportId) => {
    return AxiosClient.delete(`${ROOT}/cancel/${supportId}`, {
      withAuth: true,
    });
  },
};

export default SupportRequestApi;