import AxiosClient from "./AxiosClient";

const AttendanceApi = {
  getAttendance: (params) => {
    return AxiosClient.get("/attendances", {
      params, // query param
      withAuth: true,
    });
  },
  getMyCalendar: () => {
    return AxiosClient.get("/attendances/me", { withAuth: true });
  },

  getTeamCalendar: (teamId) => {
    return AxiosClient.get(`/attendances/${teamId}`, { withAuth: true });
  },

  checkIn: () => {
    return AxiosClient.post("/attendances/check-in", null, { withAuth: true });
  },

  checkOut: () => {
    return AxiosClient.put("/attendances/check-out", null, { withAuth: true });
  },
};

export default AttendanceApi;
