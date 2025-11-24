import AxiosClient from "./AxiosClient";

const AllowanceApi = {
 
  getAllowances : (filters) => {
    const url = "allowances";
    return AxiosClient.get(url, { params: filters, withAuth: true });
  },

  transferAllowance : (id) => {
    const url = `allowances/${id}/transfer`;
    return AxiosClient.put(url, {}, { withAuth: true });
  },

  getMyHistory: (params) => {
    const url = "allowances/my-history";
    return AxiosClient.get(url, { params: params, withAuth: true });
  },

  createAllowance: (data) => {
    const url = "allowances";
    return AxiosClient.post(url, data, { withAuth: true });
  },

  cancelAllowance: (id) => {
    const url = `allowances/${id}`;
    return AxiosClient.delete(url, { withAuth: true });
  }
};


export default AllowanceApi;
