// src/services/SupportRequestService.jsx
import { toast } from "react-toastify";
import SupportRequestApi from "~/api/SupportRequestApi";

const SupportRequestService = {
  // CẬP NHẬT: Nhận thêm page và size
  getAll: async (filters = {}, page = 1, size = 10) => {
    try {
      // Gọi API với object tham số mới
      const response = await SupportRequestApi.getAllSupportRequest({
        search: filters.search || "",
        status: filters.status || "",
        page: page,
        size: size
      });

      // Trả về dữ liệu. AxiosClient thường trả về response.data hoặc response tuỳ cấu hình.
      // Chúng ta cần trả về object chứa { content, totalPages, ... }
      return response.data || response; 
    } catch (err) {
      console.error("Lỗi tải danh sách:", err);
      toast.error("Không thể tải danh sách yêu cầu hỗ trợ!");
      // Trả về object rỗng để UI không bị crash
      return { content: [], totalPages: 0, totalElements: 0 };
    }
  },

  // Hàm này có thể không hoạt động đúng nếu dùng phân trang (vì getAll chỉ trả về 1 trang)
  // Khuyên dùng: Lấy dữ liệu từ row đã chọn ở bảng hoặc gọi API getDetail riêng
  getById: async (id) => {
    try {
        // Nếu backend có API getDetail thì gọi ở đây:
        // const response = await SupportRequestApi.getDetail(id);
        // return response;

        // Logic cũ (Tạm thời): Chỉ tìm được nếu item nằm trong trang đầu tiên
        const response = await SupportRequestApi.getAllSupportRequest({ page: 1, size: 1000 }); 
        const list = response.content || [];
        const found = list.find((item) => item.id === Number(id));
        if (!found) throw new Error("Not found");
        return found;
    } catch (err) {
      toast.error("Không tìm thấy yêu cầu!");
      throw err;
    }
  },

  updateStatus: async (id, status, replyMessage = "") => {
    try {
      let response;

      if (status === "APPROVED") {
        response = await SupportRequestApi.approveSupportRequest(id);
        toast.success("Đã duyệt yêu cầu!");
      } else if (status === "IN_PROGRESS") {
        response = await SupportRequestApi.inProgressSupportRequest(id);
        toast.info("Đơn đã chuyển sang trạng thái đang xử lý!");
      } else if (status === "REJECTED") {
        // Truyền replyMessage vào hàm reject
        response = await SupportRequestApi.rejectSupportRequest(id, replyMessage);
        toast.error("Đã từ chối yêu cầu!");
      }

      return response?.data || response;
    } catch (err) {
      toast.error("Không thể cập nhật trạng thái yêu cầu!");
      throw err;
    }
  },

  cancel: async (id) => {
    try {
      const response = await SupportRequestApi.cancelSupportRequest(id);
      toast.info("Đã hủy yêu cầu hỗ trợ!");
      return response?.data || response;
    } catch (err) {
      toast.error("Không thể hủy yêu cầu!");
      throw err;
    }
  },
};

export default SupportRequestService;