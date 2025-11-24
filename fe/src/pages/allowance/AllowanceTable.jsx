// src/pages/allowance/AllowanceTable.jsx
import React from "react";
import { CheckCircle, Clock, XCircle, Trash2 } from "lucide-react";

const AllowanceTable = ({ data, loading, onTransfer, onCancel }) => {
  const formatCurrency = (amount) =>
    new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount);

  const formatDateTime = (dt) => (dt ? dt : "-");

  const getStatusBadge = (status) => {
    if (status === "PAID") {
      return <span className="status-badge approved">Đã chuyển</span>;
    }
    if (status === "CANCELED") {
      return <span className="status-badge rejected">Đã hủy</span>;
    }
    return <span className="status-badge under_review">Chưa chuyển</span>;
  };

  return (
    <div className="table-container">
      <table className="intern-table">
        <thead>
          <tr>
            <th>Tên thực tập sinh</th>
            <th>Email</th>
            <th>Kỳ thực tập</th>
            <th>Số tiền</th>
            <th>Người chuyển</th>
            <th>Thời gian chuyển</th>
            <th>Trạng thái</th>
            <th className="action-col">Hành động</th>
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr>
              <td colSpan="8" className="text-center">Đang tải...</td>
            </tr>
          ) : data.length === 0 ? (
            <tr>
              <td colSpan="8" className="text-center">Không có dữ liệu</td>
            </tr>
          ) : (
            data.map((item) => (
              <tr key={item.id}>
                <td>{item.internName}</td>
                <td>{item.email}</td>
                <td>{item.internshipProgramName}</td>
                <td>{formatCurrency(item.amount)}</td>
                <td>{item.remiter || "-"}</td>
                <td><strong>{formatDateTime(item.paidAt)}</strong></td>
                <td>{getStatusBadge(item.status)}</td>
                <td className="action-col">
                  {item.status === "PAID" ? (
                    <div title="Đã chuyển phụ cấp" style={{ display: "flex", justifyContent: "center" }}>
                      <CheckCircle size={22} style={{ color: "#16a34a" }} />
                    </div>
                  ) : item.status === "CANCELED" ? (
                     <div title="Đã hủy" style={{ display: "flex", justifyContent: "center" }}>
                      <XCircle size={22} style={{ color: "#a1a1aa" }} />
                    </div>
                  ) : (
                    /* PENDING status */
                    <div className="action-buttons">
                      <button
                        className="icon-btn"
                        onClick={() => onTransfer(item)}
                        title="Chuyển phụ cấp ngay"
                      >
                        <Clock size={22} style={{ color: "#fbbf24" }} />
                      </button>
                      <button
                        className="icon-btn"
                        onClick={() => onCancel(item)}
                        title="Hủy khoản phụ cấp"
                      >
                        <Trash2 size={22} style={{ color: "#ef4444" }} />
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default AllowanceTable;