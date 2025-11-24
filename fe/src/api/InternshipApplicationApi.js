import AxiosClient from "./AxiosClient";

const InternshipApplicationApi = {
  getAll: (params) => {
    return AxiosClient.get("/applications", { params, withAuth: true });
  },

  updateStatus: (data) => {
    return AxiosClient.patch("/applications/status", data, { withAuth: true });
  },
};

export default InternshipApplicationApi;
