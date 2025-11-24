import React from "react";
import Select from "react-select";

const MentorTable = ({ mentors, loading, updateMentor, departmentOptions }) => {
  return (
    <div className="table-container">
      <table className="intern-table">
        <thead>
          <tr>
            <th>Họ và tên</th>
            <th>Email</th>
            <th>SĐT</th>
            <th>Phòng ban</th>
            <th>Số lượng TTS</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr>
              <td colSpan="6" className="text-center">
                Đang tải...
              </td>
            </tr>
          ) : mentors.length === 0 ? (
            <tr>
              <td colSpan="6" className="text-center">
                Không có dữ liệu
              </td>
            </tr>
          ) : (
            mentors.map((mentor) => (
              <tr key={mentor.id}>
                <td>{mentor.fullName}</td>
                <td>{mentor.email}</td>
                <td>{mentor.phone || "-"}</td>
                <td>
                  <Select
                    value={departmentOptions.find(
                      (d) => d.label === mentor.departmentName
                    )}
                    options={departmentOptions}
                    onChange={(selected) =>
                      updateMentor({
                        departmentId: selected.value,
                        id: mentor.id,
                      })
                    }
                    styles={{
                      menuPortal: (base) => ({
                        ...base,
                        zIndex: 9999,
                      }),
                    }}
                    menuPortalTarget={document.body}
                    menuPlacement="auto"
                  />
                </td>
                <td>{mentor.totalInternOwn ?? 0}</td>
                <td>
                  <span
                    className={`status-badge ${
                      mentor.active ? "active" : "inactive"
                    }`}
                  >
                    {mentor.active ? "active" : "inactive"}
                  </span>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default MentorTable;
