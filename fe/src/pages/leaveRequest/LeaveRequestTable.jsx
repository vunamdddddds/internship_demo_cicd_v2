import React from "react";
import { X } from "lucide-react";
import { cancelLeaveRequest } from "~/services/LeaveRequestService";
import Swal from "sweetalert2";

const LeaveRequestTable = ({
  leaveRequests,
  loading,
  typeOptions,
  onDeleteLeaveRequest,
}) => {
  const getStatusText = (approved) => {
    if (approved === true) return "Đã duyệt";
    if (approved === false) return "Từ chối";
    return "Chờ duyệt";
  };

  const getTypeText = (type) => {
    const found = typeOptions.find((opt) => opt.value === type);
    return found ? found.label : "";
  };

  const cancalRequest = async (id) => {
    const result = await Swal.fire({
      title: "Xác nhận hủy yêu cầu",
      html: `
            <div style="text-align: center; padding: 10px 0;">
              <p>Bạn có chắc muốn hủy yêu cầu này?</p>
            </div>
          `,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#dc3545",
      cancelButtonColor: "#6c757d",
      confirmButtonText: "Hủy yêu cầu",
      cancelButtonText: "Hủy bỏ",
      reverseButtons: true,
      customClass: {
        popup: "swal-wide",
      },
    });

    // Kiểm tra xác nhận
    if (!result.isConfirmed) return;

    await cancelLeaveRequest(id);
    onDeleteLeaveRequest(id);
  };

  return (
    <div className="table-container">
      <table className="intern-table">
        <thead>
          <tr>
            <th>Loại xin phép</th>
            <th>Lí do</th>
            <th>Ngày xin phép</th>
            <th>Tình trạng</th>
            <th className="action-col"></th>
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr>
              <td colSpan="4" className="text-center">
                Đang tải...
              </td>
            </tr>
          ) : leaveRequests == null || leaveRequests.length === 0 ? (
            <tr>
              <td colSpan="4" className="text-center">
                Không có dữ liệu
              </td>
            </tr>
          ) : (
            leaveRequests.map((leaveRequest) => (
              <tr key={leaveRequest.id}>
                <td>{getTypeText(leaveRequest.type)}</td>
                <td>{leaveRequest.reason}</td>
                <td>{leaveRequest.date}</td>
                <td>
                  <span
                    className={`status-badge ${
                      leaveRequest.approved === true
                        ? "approved"
                        : leaveRequest.approved === false
                        ? "rejected"
                        : "pending"
                    }`}
                  >
                    {getStatusText(leaveRequest?.approved)}
                  </span>
                </td>
                <td className="action-col">
                  <button
                    className="icon-btn"
                    onClick={() => cancalRequest(leaveRequest.id)}
                  >
                    <X size={18} color="red" />
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default LeaveRequestTable;
