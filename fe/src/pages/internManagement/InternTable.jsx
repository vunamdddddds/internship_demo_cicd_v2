import React from "react";
import { Pencil } from "lucide-react";

const InternTable = ({ interns, loading, onEdit }) => (
  <div className="table-container">
    <table className="intern-table">
      <thead>
        <tr>
          <th>Họ và tên</th>
          <th>SĐT</th>
          <th>Email</th>
          <th>Ngành học</th>
          <th>Trường học</th>
          <th>Kì thực tập</th>
          <th>Trạng thái</th>
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
        ) : interns.length === 0 ? (
          <tr>
            <td colSpan="7" className="text-center">
              Không có dữ liệu
            </td>
          </tr>
        ) : (
          interns.map((intern) => (
            <tr key={intern.id}>
              <td>{intern.fullName}</td>
              <td>{intern.phone || "-"}</td>
              <td>{intern.email}</td>
              <td>{intern.major}</td>
              <td>{intern.university}</td>
              <td>{intern.internshipProgram}</td>
              <td>
                <span className={`status-badge ${intern.status.toLowerCase()}`}>
                  {intern.status}
                </span>
              </td>
              <td className="action-col">
                <button className="icon-btn" onClick={() => onEdit(intern)}>
                  <Pencil size={15} />
                </button>
              </td>
            </tr>
          ))
        )}
      </tbody>
    </table>
  </div>
);

export default InternTable;
