// src/api/MentorEvaluationApi.js
import axiosClient from "./AxiosClient";

const MentorEvaluationApi = {
  getTeams: () =>
    axiosClient.get("/teams/my-teams", { withAuth: true }),

  getEvaluation: (internId) =>
    axiosClient.get(`/evaluations/interns/${internId}`, { withAuth: true }),

  submitEvaluation: (internId, data) =>
    axiosClient.put(`/evaluations/interns/${internId}`, data, { withAuth: true }),
};

export default MentorEvaluationApi;