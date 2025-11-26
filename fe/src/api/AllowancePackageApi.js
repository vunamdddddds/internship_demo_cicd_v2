import AxiosClient from "./AxiosClient";

const ALLOWANCE_PACKAGE_URL = "/hr/allowance-packages";

const AllowancePackageApi = {
  getAllAllowancePackages: (params) => {
    return AxiosClient.get(ALLOWANCE_PACKAGE_URL, { params, withAuth:true});
  },

  getAllowancePackageById: (id) => {
    return AxiosClient.get(`${ALLOWANCE_PACKAGE_URL}/${id}`,{ withAuth: true });
  },

  createAllowancePackage: (data) => {
    return AxiosClient.post(ALLOWANCE_PACKAGE_URL, data,{ withAuth: true });
  },

  updateAllowancePackage: (id, data) => {
    return AxiosClient.put(`${ALLOWANCE_PACKAGE_URL}/${id}`, data,{ withAuth: true });
  },

  deleteAllowancePackage: (id) => {
    return AxiosClient.delete(`${ALLOWANCE_PACKAGE_URL}/${id}`,{ withAuth: true });
  },
};

export default AllowancePackageApi;