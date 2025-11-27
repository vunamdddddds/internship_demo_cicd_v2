import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEdit, faTrash } from "@fortawesome/free-solid-svg-icons";

const AllowancePackageTable = ({ data, loading, onEdit, onDelete }) => {
  if (loading) {
    return <div>Loading allowance packages...</div>;
  }

  const getStatusBadge = (status) => {
    if (status === "ACTIVE") {
      return <span className="status-badge approved">Hoạt động</span>;
    }
    return <span className="status-badge rejected">Không hoạt động</span>;
  };

  return (
    <div className="table-container">
      <table className="intern-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên gói</th>
            <th>Số tiền</th>
            <th>Số ngày làm việc tối thiểu</th>
            <th>Chương trình thực tập</th>
            <th>Trạng thái</th>
            <th className="action-col">Hành động</th>
          </tr>
        </thead>
        <tbody>
          {data && data.length > 0 ? (
            data.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{item.name}</td>
                <td>{item.amount.toLocaleString("vi-VN")} VND</td>
                <td>{item.requiredWorkDays} ngày</td>
                <td>{item.internshipProgram?.name}</td>
                <td>{getStatusBadge(item.status)}</td>
                <td className="action-col">
                  <button
                    className="btn btn-warning btn-sm me-2"
                    onClick={() => onEdit(item)}
                  >
                    <FontAwesomeIcon icon={faEdit} /> Sửa
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => onDelete(item.id)}
                  >
                    <FontAwesomeIcon icon={faTrash} /> Hủy gói phụ cấp
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7" className="text-center">
                Không có gói phụ cấp nào.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default AllowancePackageTable;