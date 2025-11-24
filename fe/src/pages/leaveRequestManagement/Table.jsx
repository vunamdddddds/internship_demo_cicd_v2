import React, { useState } from "react";
import { X, Check } from "lucide-react";
import { approveLeaveRequest } from "~/services/LeaveRequestService";
import Swal from "sweetalert2";
import RejectForm from "./RejectForm";

const Table = ({
  leaveRequests,
  loading,
  leaveRequestOptions,
  setLeaveRequests,
}) => {
  const [showForm, setShowForm] = useState(false);
  const [id, setId] = useState(0);

  const renderStatus = (approved, id) => {
    if (approved === true) {
      return <span style={{ color: "green", fontWeight: 600 }}>Đã duyệt</span>;
    }

    if (approved === false) {
      return <span style={{ color: "red", fontWeight: 600 }}>Đã từ chối</span>;
    }

    return (
      <>
        <button className="icon-btn" onClick={() => approve(id)}>
          <Check size={15} color="green" />
        </button>
        <button
          className="icon-btn"
          onClick={() => {
            setShowForm(true);
            setId(id);
          }}
        >
          <X size={15} color="red" />
        </button>
      </>
    );
  };

  const approve = async (id) => {
    const result = await Swal.fire({
      title: "Xác nhận duyệt yêu cầu",
      html: `
                <div style="text-align: center; padding: 10px 0;">
                  <p>Bạn có chắc muốn duyệt yêu cầu này?</p>
                </div>
              `,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "green",
      cancelButtonColor: "#6c757d",
      confirmButtonText: "Duyệt yêu cầu",
      cancelButtonText: "Hủy bỏ",
      reverseButtons: true,
      customClass: {
        popup: "swal-wide",
      },
    });

    // Kiểm tra xác nhận
    if (!result.isConfirmed) return;

    const data = await approveLeaveRequest(id);
    if (data) {
      setLeaveRequests((prev) =>
        prev.map((leaveRequest) =>
          leaveRequest.id === data.id ? data : leaveRequest
        )
      );
    }
  };

  return (
    <>
      <div className="table-container">
        <table className="intern-table">
          <thead>
            <tr>
              <th>Họ và tên</th>
              <th>Ngày xin phép</th>
              <th>Loại đơn</th>
              <th>Lí do</th>
              <th>File minh chứng</th>
              <th>Lí do từ chối</th>
              <th className="action-col"></th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="7" className="text-center">
                  Đang tải...
                </td>
              </tr>
            ) : leaveRequests.length === 0 ? (
              <tr>
                <td colSpan="7" className="text-center">
                  Không có dữ liệu
                </td>
              </tr>
            ) : (
              leaveRequests.map((leaveRequest) => (
                <tr key={leaveRequest.id}>
                  <td>{leaveRequest.internName}</td>
                  <td>{leaveRequest.date}</td>
                  <td>
                    {leaveRequestOptions.find(
                      (opt) => opt.value === leaveRequest.type
                    )?.label || leaveRequest.type}
                  </td>

                  <td>{leaveRequest.reason}</td>
                  <td>
                    {leaveRequest.attachedFileUrl ? (
                      <a
                        href={leaveRequest.attachedFileUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        style={{
                          textDecoration: "none",
                        }}
                        className="btn-save"
                      >
                        Xem file
                      </a>
                    ) : (
                      "-"
                    )}
                  </td>
                  <td>{leaveRequest.reasonReject || "-"}</td>
                  <td className="action-col">
                    {renderStatus(leaveRequest.approved, leaveRequest.id)}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
      {showForm && (
        <RejectForm
          onClose={() => setShowForm(false)}
          setLeaveRequests={setLeaveRequests}
          id={id}
        />
      )}
    </>
  );
};

export default Table;
