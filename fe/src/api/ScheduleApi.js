import AxiosClient from "./AxiosClient";

 const ScheduleApi = {
  getScheduleForTeam: (teamId) => {
    return AxiosClient.get(`/work-schedules/${teamId}`, { withAuth: true });
  },

  updateScheduleForTeam: (teamId, data, date) => {
    return AxiosClient.put(`/work-schedules/${teamId}`, data, {
      params: { date },
      withAuth: true,
    });
  },

  deleteScheduleForDay: (teamId, dayOfWeek, date) => {
    return AxiosClient.delete(`/work-schedules/${teamId}/${dayOfWeek}`, {
      params: { date },
      withAuth: true,
    });
  },

  // New method for intern's team schedule
  getMyTeamSchedule: () => {
    return AxiosClient.get(`/interns/my-team-schedule`, { withAuth: true });
  },
};

export default ScheduleApi;
