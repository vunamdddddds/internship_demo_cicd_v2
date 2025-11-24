// src/api/mockApi.js
import AxiosClient from "./AxiosClient";

const ReportApi = {
  sendReport: (sprintId, file) => {
    const formData = new FormData();
    formData.append("file", file);

    return AxiosClient.post(`/sprints/${sprintId}/report`, formData, {
      withAuth: true,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  getMentorFeedback: (sprintId) => {
    return AxiosClient.get(`/sprints/evaluate/${sprintId}`, { withAuth: true });
  },

  getInternAttendanceDetail: async (internId, internshipProgramId) => {
    const res = await AxiosClient.get(`/reports/interns/${internId}/attendance`, {
      params: { internshipProgramId }, // ✅ phải để trong object
      withAuth: true, // nếu AxiosClient hỗ trợ option này
    });

    // ✅ log data cho dễ debug
    console.log("Intern attendance detail:", res);

    return res; // ✅ luôn return data, không return promise gốc
  },

  getFinalReport: (internshipProgramId, universityId, page = 0, size = 10) => {
    return AxiosClient.get("/reports/final-report", {
      params: { internshipProgramId, universityId, page, size },
      withAuth: true,
    });
  },

  getInternFullAttendanceHistory: (internId, internshipProgramId) => {
    return AxiosClient.get(`/reports/interns/${internId}/full-history`, {
      params: { internshipProgramId },
      withAuth: true,
    });
  },
};

export default ReportApi;
