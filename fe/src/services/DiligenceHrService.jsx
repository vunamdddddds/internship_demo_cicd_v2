import { toast } from "react-toastify";
// The import is changed to a default import to get the new API object
import leaveRequestApi from "../api/diligenceApi";
import attendanceApi from "../api/AttendanceApi";

// This function now maps to the new getAllLeaveRequests API
export const getDiligenceReport = async ({ teamId }) => {
  try {
    const data = await attendanceApi.getAttendance({ teamId });
    return data;
  } catch (err) {
    const errorMessage = err.response?.data?.message || "Lấy báo cáo thất bại";
    toast.error(errorMessage);
    throw err;
  }
};

// This function now maps to getLeaveRequestById
export const getDiligenceDetail = async (id) => {
  try {
    const data = await leaveRequestApi.getLeaveRequestById(id);
    return data;
  } catch (err) {
    const errorMessage = err.response?.data?.message || "Lấy chi tiết thất bại";
    toast.error(errorMessage);
    throw err;
  }
};

// This function, which caused the error, is now mapped to getAllLeaveRequests filtered by intern name
export const getDiligenceLeaveHistory = async (internName) => {
  try {
    const params = { keyword: internName };
    const data = await leaveRequestApi.getAllLeaveRequests(params);
    return data.data; // The response is paginated, return the content
  } catch (err) {
    const errorMessage =
      err.response?.data?.message || "Lấy lịch sử nghỉ phép thất bại";
    toast.error(errorMessage);
    throw err;
  }
};

// HÀM MỚI: LẤY DANH SÁCH KỲ THỰC TẬP (TỪ BÁO CÁO)
export const getInternshipTerms = async () => {
  try {
    const data = await attendanceApi.getAttendance({});
    const terms = [...new Set(data.map((item) => item.internshipTerm))];
    return terms.map((t) => ({ value: t, label: t }));
  } catch (err) {
    toast.error("Lỗi tải kỳ thực tập");
    console.error("Error fetching internship terms:", err);
    return [];
  }
};

// HÀM MỚI: LẤY DANH SÁCH NHÓM (TỪ BÁO CÁO)
export const getGroups = async () => {
  try {
    const data = await attendanceApi.getAttendance({});
    const groups = [
      ...new Set(data.map((item) => item.teamName).filter(Boolean)),
    ];
    return [
      { value: "ALL", label: "Tất cả" },
      ...groups.map((name) => ({ value: name, label: name })),
    ];
  } catch (err) {
    toast.error("Lỗi tải nhóm thực tập");
    console.error("Error fetching groups:", err);
    return [{ value: "ALL", label: "Tất cả" }];
  }
};

// This function has no backend equivalent and is now disabled.
export const exportDiligenceReport = async (format) => {
  
  toast.warn("Chức năng xuất báo cáo chưa được hỗ trợ.:"+ format);
  // try {
  //   const result = await exportFile(format);
  //   toast.success(`Đã xuất báo cáo dưới dạng ${format.toUpperCase()}!`);
  //   return result;
  // } catch (err) {
  //   toast.error("Xuất báo cáo thất bại");
  //   throw err;
  // }
  return Promise.resolve();
};
