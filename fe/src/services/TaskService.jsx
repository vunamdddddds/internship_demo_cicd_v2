// src/services/TaskService.jsx
import { toast } from "react-toastify";
import TaskApi from "~/api/TaskApi";

export const getTasks = async ({ keyword, status, page }) => {
  try {
    const res = await TaskApi.getAll({ keyword, status, page });
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
    throw err;
  }
};

export const updateTaskStatus = async (id, status) => {
  try {
    const res = await TaskApi.updateStatus(id, status);
    return res;
  } catch (err) {
    throw err;
  }
};

export const addSubtask = async (taskId, subtask) => {
  try {
    const res = await TaskApi.addSubtask(taskId, subtask);
    return res;
  } catch (err) {
    throw err;
  }
};

export const updateSubtask = async (taskId, subId, updates) => {
  try {
    const res = await TaskApi.updateSubtask(taskId, subId, updates);
    return res;
  } catch (err) {
    toast.error(err.message || "Cập nhật thất bại");
    throw err;
  }
};

export const deleteSubtask = async (taskId, subId) => {
  try {
    const res = await TaskApi.deleteSubtask(taskId, subId);
    return res;
  } catch (err) {
    throw err;
  }
};