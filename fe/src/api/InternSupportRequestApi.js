import AxiosClient from "./AxiosClient";

const InternSupportRequestApi = {
  getMyRequests: () => {
    return AxiosClient.get("/support-request/me", {
      withAuth: true
    });
  },

  createRequest: (formData) => {
    return AxiosClient.post("/support-request", formData, {
      withAuth: true,
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  downloadFile: (requestId) =>
    AxiosClient.get(`/support-requests/${requestId}/file`, {
      withAuth: true,
      responseType: "blob",
    }),

  updateRequest: (id, formData) => {
    return AxiosClient.put(`/support-request/${id}`, formData, {
      withAuth: true,
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
  
  cancelRequest: (id) => {
    return AxiosClient.delete(`/support-request/cancel/${id}`, { withAuth: true });
  }
};

export default InternSupportRequestApi;