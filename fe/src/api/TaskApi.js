import AxiosClient from "./AxiosClient";

const TaskApi = {
  getTasksBySprint: (sprintId, params) => {
    // params can be { status, assigneeId }
    return AxiosClient.get(`/sprints/${sprintId}/tasks`, { params, withAuth: true });
  },

  createTask: (sprintId, data) => {
    return AxiosClient.post(`/sprints/${sprintId}/tasks`, data, { withAuth: true });
  },

  updateTask: (taskId, data) => {
    return AxiosClient.put(`/tasks/${taskId}`, data, { withAuth: true });
  },

  getTaskById: (taskId) => {
    return AxiosClient.get(`/tasks/${taskId}`, { withAuth: true });
  },

  getTasksByTeam: (teamId) => {
    return AxiosClient.get(`/teams/${teamId}/tasks`, { withAuth: true });
  },

  deleteTask: (taskId) => {
    return AxiosClient.delete(`/tasks/${taskId}`, { withAuth: true });
  },

  batchUpdateTasks: (payload) => {
    // payload is an object like { action, taskIds, targetSprintId }
    return AxiosClient.post(`/tasks/batch-update`, payload, { withAuth: true });
  },
};

export default TaskApi;
