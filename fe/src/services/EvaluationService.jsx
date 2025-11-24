// src/services/evaluationService.js
import { toast } from "react-toastify";
import ReportApi from "~/api/ReportApi";
import InternshipProgramApi from "~/api/InternshipProgramApi";
import UniversityApi from "~/api/UniversityApi";
// Giả định có API lấy thông tin User/Intern để hiển thị tên
// import InternApi from "~/api/InternApi"; 

const evaluationService = {
  // 1. LẤY DANH SÁCH BÁO CÁO + LỌC
  getReports: async (filters = {}) => {
    try {
      const res = await ReportApi.getFinalReport(
        filters.internshipProgramId,
        filters.universityId,
      );

      // Kiểm tra nếu res.content không tồn tại (đề phòng API trả về khác cấu trúc Page)
      const content = res.content || [];

      const reportsData = content.map((r) => ({
        ...r,
        // Parse số an toàn
        expertiseScore: parseFloat(r.expertiseScore || 0),
        qualityScore: parseFloat(r.qualityScore || 0),
        problemSolvingScore: parseFloat(r.problemSolvingScore || 0),
        technologyLearningScore: parseFloat(r.technologyLearningScore || 0),
        
        // Tính điểm trung bình
        avgScore:
          (parseFloat(r.expertiseScore || 0) +
            parseFloat(r.qualityScore || 0) +
            parseFloat(r.problemSolvingScore || 0) +
            parseFloat(r.technologyLearningScore || 0)) /
          4,
        
        comment: r.assessment,
        // TODO: Backend FinalReportResponse chưa có attendanceRate. 
        // Tạm thời để 0 để UI không bị lỗi.
        attendanceRate: 0, 
      }));

      return { reports: reportsData, pagination: res };
    } catch (err) {
      console.error(err);
      toast.error(err?.response?.data || "Không thể tải báo cáo đánh giá");
      return { reports: [], pagination: {} };
    }
  },

  // 2. LẤY DANH SÁCH KỲ THỰC TẬP
  getInternshipTerms: async () => {
    try {
      const res = await InternshipProgramApi.getAll();
      // Xử lý trường hợp res là array hoặc res.data là array
      const data = Array.isArray(res) ? res : (res.data || []);
      
      return data.map((t) => ({
        value: t.id,
        label: t.name,
      }));
    } catch (err) {
      // Không toast lỗi này để tránh spam thông báo khi load trang
      console.error("Lỗi tải kỳ thực tập:", err);
      return [];
    }
  },

  // 3. LẤY DANH SÁCH TRƯỜNG ĐẠI HỌC
  getUniversities: async () => {
    try {
      const res = await UniversityApi.getAll();
      const data = Array.isArray(res) ? res : (res.data || []);

      return data.map((u) => ({
        value: u.id,
        label: u.name,
      }));
    } catch (err) {
      console.error("Lỗi tải trường đại học:", err);
      return [];
    }
  },

  // 4. LẤY CHI TIẾT CHUYÊN CẦN
  getDiligenceDetail: async (internId, internshipProgramId) => {
    try {
      // Gọi song song API lấy lịch sử và API lấy thông tin sinh viên (để lấy tên)
      // Nếu chưa có API getDetailIntern, bạn có thể bỏ promise thứ 2 đi
      const [historyRes, internInfo] = await Promise.all([
         ReportApi.getInternFullAttendanceHistory(internId, internshipProgramId),
         // Giả sử có API này để lấy tên. Nếu không, internName sẽ bị trống.
         // InternApi.getById(internId).catch(() => ({ fullName: "N/A" })) 
         Promise.resolve({ fullName: "Đang cập nhật..." }) 
      ]);

      // Lấy thông tin tổng hợp từ phần tử đầu tiên của list lịch sử (nếu có)
      const summary = historyRes && historyRes.length > 0 ? historyRes[0] : {};

      // ⚠️ QUAN TRỌNG: Sử dụng || 0 để tránh lỗi NaN khi undefined
      const workedDays = summary.totalWorkingDays || 0;
      const leaveDays = summary.totalOnLeaveDays || 0;
      const absentDays = summary.totalAbsentDays || 0;
      const totalDays = workedDays + leaveDays + absentDays;

      const details = historyRes.map((item) => ({
        date: item.date, 
        status: item.status,
        reason: item.reason,
      }));

      return {
        internName: internInfo.fullName || "Thực tập sinh", // Lấy từ API info hoặc fallback
        totalDays: totalDays,
        workedDays: workedDays,
        leaveDays: leaveDays,
        absentDays: absentDays,
        details: details,
      };
    } catch (err) {
      toast.error(err?.response || "Không tìm thấy dữ liệu chuyên cần!");
      throw err;
    }
  },

  // 5. XUẤT FILE
  exportEvaluationReport: async (format) => {
    // TODO: Thay bằng API thực tế
    await new Promise((r) => setTimeout(r, 1000));
    toast.success(`Đã xuất báo cáo dưới dạng ${format.toUpperCase()}!`);
  },
};

export default evaluationService;