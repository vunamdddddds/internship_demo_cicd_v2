import AxiosClient from "./AxiosClient";

const UserApi = {
  getAll: (params) => {
    return AxiosClient.get("/users", { params, withAuth: true });
  },

  getInfo: () => {
    return AxiosClient.get("/users/info", { withAuth: true });
  },

  getHrList: () => {
    return AxiosClient.get("/users/hr", { withAuth: true });
  },

  create: (data) => {
    return AxiosClient.post("/users", data, { withAuth: true });
  },

  edit: (data) => {
    return AxiosClient.put(`/users/${data.id}`, data, { withAuth: true });
  },

  editInfo: (formData) => {
    return AxiosClient.put(`/users/info`, formData, {
      withAuth: true,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
};

export default UserApi;
