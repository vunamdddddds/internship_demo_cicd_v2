// src/services/MentorEvaluationService.jsx
import { toast } from "react-toastify";
import MentorEvaluationApi from "~/api/MentorEvaluationApi";

export const fetchTeams = async () => {
  try {
    const res = await MentorEvaluationApi.getTeams();
    // Thêm logic cảnh báo nếu API trả về thành công nhưng không có dữ liệu
    if (!res || res.length === 0) {
      toast.warn("Tài khoản Mentor của bạn hiện chưa được gán cho nhóm nào.");
      return [];
    }
    return res;
  } catch (error) {
    if (error.response?.status === 401) {
      toast.error(
        "Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại."
      );
      return [];
    }

    if (error.response?.status === 400) {
      const message = error.response.data;
      if (message.includes("Mentor không tồn tại")) {
        toast.warn(
          "Tài khoản của bạn chưa được cấu hình làm Mentor. Vui lòng liên hệ HR."
        );
      } else if (message.includes("không có quyền")) {
        toast.warn("Bạn không có quyền truy cập chức năng này.");
      }
      return [];
    }
    toast.error("Lỗi tải danh sách nhóm!");
    return [];
  }
};

export const fetchEvaluation = async (internId) => {
  try {
    const res = await MentorEvaluationApi.getEvaluation(internId);
    return res || null;
  } catch (error) {
    if (error.response?.status === 401) {
      toast.error("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
    }
    return null;
  }
};

export const submitEvaluation = async (internId, data) => {
  try {
    await MentorEvaluationApi.submitEvaluation(internId, data);
    toast.success("Đánh giá đã được lưu!");
    return true;
  } catch (error) {
    const errMsg = error.response?.data || "Lưu đánh giá thất bại!";
    if (error.response?.status === 401) {
      toast.error("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
    } else {
      toast.error(errMsg);
    }
    throw error;
  }
};
