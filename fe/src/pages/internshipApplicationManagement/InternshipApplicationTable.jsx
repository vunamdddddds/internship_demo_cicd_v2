import React, { useState } from "react";
import InternshipApplicationDetail from "./InternshipApplicationDetail";

const InternshipApplicationTable = ({
  internships,
  loading,
  setIdApplications,
  idApplications,
}) => {
  const [showForm, setShowForm] = useState(false);
  const [selectedIntern, setSelectedIntern] = useState(null);
  return (
    <>
      <div className="table-container">
        <table className="intern-table">
          <thead>
            <tr>
              <th className="checkbox-col"></th>
              <th>Họ và tên</th>
              <th>SĐT</th>
              <th>Email</th>
              <th>Ngành học</th>
              <th>Trường học</th>
              <th>Kì thực tập</th>
              <th>Trạng thái</th>
              <th>Ngày tạo</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="9" className="text-center">
                  Đang tải...
                </td>
              </tr>
            ) : internships.length === 0 ? (
              <tr>
                <td colSpan="9" className="text-center">
                  Không có dữ liệu
                </td>
              </tr>
            ) : (
              internships.map((internship) => (
                <tr
                  style={{ cursor: "pointer" }}
                  key={internship.id}
                  onDoubleClick={(e) => {
                    if (e.target.type === "checkbox") return;
                    setSelectedIntern(internship);
                    setShowForm(true);
                  }}
                >
                  <th>
                    <input
                      type="checkbox"
                      style={{ width: "18px", height: "18px" }}
                      checked={idApplications.has(internship.id)}
                      onChange={(e) => {
                        const newSet = new Set(idApplications);
                        if (e.target.checked) {
                          newSet.add(internship.id);
                        } else {
                          newSet.delete(internship.id);
                        }
                        setIdApplications(newSet);
                      }}
                    />
                  </th>
                  <td>{internship.fullName}</td>
                  <td>{internship.phone || "-"}</td>
                  <td>{internship.email}</td>
                  <td>{internship.majorName}</td>
                  <td>{internship.universityName}</td>
                  <td>{internship.internshipProgram}</td>
                  <td>
                    <span
                      className={`status-badge ${internship.internshipApplicationStatus.toLowerCase()}`}
                    >
                      {internship.internshipApplicationStatus}
                    </span>
                  </td>
                  <td>{internship.createdAt}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showForm && (
        <InternshipApplicationDetail
          intern={selectedIntern}
          onClose={() => setShowForm(false)}
        />
      )}
    </>
  );
};

export default InternshipApplicationTable;
