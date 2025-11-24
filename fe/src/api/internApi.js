import AxiosClient from "./AxiosClient";

const InternApi = {
  getAll: (params) => {
    return AxiosClient.get("/interns", { params, withAuth: true });
  },
  getMe: () => {
    return AxiosClient.get("/interns/me", { withAuth: true });
  },
  create: (data) => {
    return AxiosClient.post("/interns", data, { withAuth: true });
  },
  edit: (data) => {
    return AxiosClient.put(`/interns/${data.id}`, data, { withAuth: true });
  },
  getInternNoTeam: (teamId) => {
    return AxiosClient.get(`/interns/${teamId}`, { withAuth: true });
  },
};

export default InternApi;
