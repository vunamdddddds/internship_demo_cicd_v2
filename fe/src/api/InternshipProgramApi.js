import AxiosClient from "./AxiosClient";

const InternshipProgramApi = {
  getAll: () => {
    return AxiosClient.get("/internship-programs", { withAuth: true });
  },

  getInternshipProgram: (params) => {
    return AxiosClient.get("/internship-programs/get", {
      params,
      withAuth: true,
    });
  },

  create: (data) => {
    return AxiosClient.post("/internship-programs", data, { withAuth: true });
  },

  update: (data) => {
    return AxiosClient.put(`/internship-programs/${data.id}`, data, {
      withAuth: true,
    });
  },
};

export default InternshipProgramApi;
