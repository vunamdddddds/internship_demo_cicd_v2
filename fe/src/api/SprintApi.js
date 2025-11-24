import AxiosClient from "./AxiosClient";

const SprintApi = {
  getSprintsByTeam: (teamId) => {
    return AxiosClient.get(`/teams/${teamId}/sprints`, { withAuth: true });
  },

  create: (teamId, data) => {
    return AxiosClient.post(`/teams/${teamId}/sprints`, data, { withAuth: true });
  },

  update: (sprintId, data) => {
    return AxiosClient.put(`/sprints/${sprintId}`, data, { withAuth: true });
  },

  delete: (sprintId) => {
    return AxiosClient.delete(`/sprints/${sprintId}`, { withAuth: true });
  },

  evaluateSprint: (sprintId, feedback) => {
    return AxiosClient.put(`/sprints/evaluate/${sprintId}`, feedback, { withAuth: true });
  },
};

export default SprintApi;
