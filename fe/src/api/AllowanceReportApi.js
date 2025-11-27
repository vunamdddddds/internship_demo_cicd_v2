import AxiosClient from "./AxiosClient";

const AllowanceReportApi = {
  getAllowanceReports: (page = 0, size = 10) => {
    const url = `/hr/allowance/reports?page=${page}&size=${size}`;
    return AxiosClient.get(url,{ withAuth: true });
  },

  downloadDetailedReport: (yearMonth) => {
    const url = `/hr/allowance/reports/export?month=${yearMonth}`;
    return AxiosClient.get(url, {
      responseType: "blob", // Important for file downloads
    },{ withAuth: true });
  },

  getAllowanceDetails: (yearMonth) => {
    const url = `/hr/allowance/reports/details?month=${yearMonth}`;
    return AxiosClient.get(url,{ withAuth: true });
  },
};

export default AllowanceReportApi;
