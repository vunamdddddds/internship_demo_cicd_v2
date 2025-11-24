import AxiosClient from "./AxiosClient";

const DepartmentApi = {
  getAll: () => {
    return AxiosClient.get("/departments", { withAuth: true });
  },
};

export default DepartmentApi;
