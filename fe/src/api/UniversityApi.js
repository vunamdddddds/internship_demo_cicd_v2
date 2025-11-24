import AxiosClient from "./AxiosClient";

const UnversityApi = {
  getAll: () => {
    return AxiosClient.get("/universities", { withAuth: true });
  },
};

export default UnversityApi;
