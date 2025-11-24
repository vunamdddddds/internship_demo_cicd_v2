import InternSupportRequestApi from "~/api/InternSupportRequestApi";
import { toast } from "react-toastify";

export const InternSupportRequestService = {
  fetchMyRequests: async () => {
    try {
      const response = await InternSupportRequestApi.getMyRequests();
      return response || response || [];
    } catch (error) {
      console.error("Lỗi lấy danh sách:", error);
      return [];
    }
  },

  submitRequest: async ({ title, description, file }) => {
    try {
      const formData = new FormData();
      formData.append("title", title);
      formData.append("description", description);
      if (file) {
        formData.append("evidenceFile", file);
      }

      const response = await InternSupportRequestApi.createRequest(formData);

      toast.success("Gửi yêu cầu thành công!");
      return response || response;
    } catch (error) {
      console.error("Lỗi gửi yêu cầu:", error);
      const msg = error.response?.message || "Gửi yêu cầu thất bại";
      toast.error(msg);
      throw error;
    }
  },

  updateRequest: async (id, { title, description, file }) => {
    try {
      const formData = new FormData();
      formData.append("title", title);
      formData.append("description", description);
      if (file) formData.append("evidenceFile", file);
      const response = await InternSupportRequestApi.updateRequest(id, formData);

      toast.success("Cập nhật yêu cầu thành công!");
      return response || response;
    } catch (error) {
      const msg = error.response?.message || "Cập nhật thất bại";
      toast.error(msg);
      throw error;
    }
  },

  cancelRequest: async (id) => {
    try {
      await InternSupportRequestApi.cancelRequest(id);
    } catch (error) {
      const msg = error.response?.message || "Hủy yêu cầu thất bại";
      toast.error(msg);
      throw error;
    }
  }
};