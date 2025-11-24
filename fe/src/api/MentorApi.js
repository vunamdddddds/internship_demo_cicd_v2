import AxiosClient from "./AxiosClient";

const MentorApi = {
  getAll: (params) => {
    return AxiosClient.get("/mentors", { params, withAuth: true });
  },

  getAllMentor: () => {
    return AxiosClient.get("/mentors/getAll", { withAuth: true });
  },

  create: (data) => {
    return AxiosClient.post("/mentors", data, { withAuth: true });
  },

  edit: (data) => {
    return AxiosClient.put(`/mentors/${data.id}`, data, { withAuth: true });
  },
};

export default MentorApi;
