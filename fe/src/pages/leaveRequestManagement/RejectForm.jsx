import React, { useState } from "react";
import { X } from "lucide-react";
import { rejectLeaveRequest } from "~/services/LeaveRequestService";
import Swal from "sweetalert2";

const RejectForm = ({ onClose, setLeaveRequests, id }) => {
  const [reasonReject, setReasonReject] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const result = await Swal.fire({
      title: "Xác nhận từ chối yêu cầu",
      html: `
                    <div style="text-align: center; padding: 10px 0;">
                      <p>Bạn có chắc muốn từ chối yêu cầu này?</p>
                    </div>
                  `,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#c00101ff",
      cancelButtonColor: "#6c757d",
      confirmButtonText: "Từ chối yêu cầu",
      cancelButtonText: "Hủy bỏ",
      reverseButtons: true,
      customClass: {
        popup: "swal-wide",
      },
    });

    // Kiểm tra xác nhận
    if (!result.isConfirmed) return;

    const data = await rejectLeaveRequest({ id, reasonReject });
    if (data) {
      setLeaveRequests((prev) =>
        prev.map((leaveRequest) =>
          leaveRequest.id === data.id ? data : leaveRequest
        )
      );
      onClose();
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Từ chối đơn xin phép</h3>
          <button className="modal-close" onClick={() => onClose()}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <label>Lý do</label>
          <textarea
            rows="6"
            placeholder="Nhập lý do..."
            value={reasonReject}
            onChange={(e) => setReasonReject(e.target.value)}
            required
            style={{ resize: "none", width: "100%" }}
          ></textarea>

          <div className="modal-actions">
            <button type="submit" className="btn btn-save">
              Xác nhận
            </button>
            <button
              type="button"
              onClick={() => {
                onClose();
              }}
              className="btn btn-cancel"
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RejectForm;
