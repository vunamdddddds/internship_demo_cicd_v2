import AxiosClient from "./AxiosClient";

const MajorApi = {
  getAll: () => {
    return AxiosClient.get("/majors", { withAuth: true });
  },
};

export default MajorApi;
