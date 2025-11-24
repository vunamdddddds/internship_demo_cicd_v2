import AxiosClient from "./AxiosClient";

const TeamApi = {
  getTeams: (params) => {
    return AxiosClient.get("/teams", { params, withAuth: true });
  },

  getTeamsByIP: (internshipProgramId) => {
    return AxiosClient.get(`/teams/by/${internshipProgramId}`, {
      withAuth: true,
    });
  },

  getMyTeams: () => {
    return AxiosClient.get("/teams/my-teams", { withAuth: true });
  },

  getTeamById: (teamId) => {
    return AxiosClient.get(`/teams/${teamId}`, { withAuth: true });
  },

  getAll: () => {
    return AxiosClient.get("/teams/getAll", { withAuth: true });
  },

  create: (data) => {
    return AxiosClient.post("/teams", data, { withAuth: true });
  },

  edit: (data) => {
    return AxiosClient.put(`/teams/${data.id}`, data, { withAuth: true });
  },

  addMember: ({ teamId, internIds }) => {
    return AxiosClient.post(
      `/teams/${teamId}/members`,
      { internIds },
      {
        withAuth: true,
      }
    );
  },

  removeMember: (id) => {
    return AxiosClient.patch(`/teams/remove/${id}`, null, {
      withAuth: true,
    });
  },
};

export default TeamApi;
