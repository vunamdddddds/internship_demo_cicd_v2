import AxiosClient from "./AxiosClient";

const WorkScheduleApi = {
  getSchedule: (teamId) => {
    return AxiosClient.get(`/workSchedules/${teamId}`, { withAuth: true });
  },

  createSchedule: (data) => {
    return AxiosClient.post("/workSchedules", data, {
      withAuth: true,
    });
  },

  updateSchedule: (data) => {
    return AxiosClient.put(`/workSchedules/${data.id}`, data, {
      withAuth: true,
    });
  },

  deleteSchedule: (id) => {
    return AxiosClient.delete(`/workSchedules/${id}`, {
      withAuth: true,
    });
  },
};

export default WorkScheduleApi;
