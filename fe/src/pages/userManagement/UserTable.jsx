import React from "react";
import { Ban, Unlock } from "lucide-react";
import Select from "react-select";

const UserTable = ({ users, loading, editUser, roleOptions }) => {
  const roleColors = {
    ADMIN: { bg: "#fee2e2", color: "#b91c1c" },
    HR: { bg: "#fef9c3", color: "#a16207" },
    MENTOR: { bg: "#d1fae5", color: "#047857" },
    INTERN: { bg: "#dbeafe", color: "#1d4ed8" },
    VISITOR: { bg: "#f3e8ff", color: "#7e22ce" },
  };

  const customStyles = {
    control: (base) => ({
      ...base,
      borderRadius: "6px",
      minHeight: "32px",
      boxShadow: "none",
      borderColor: "#ccc",
      "&:hover": { borderColor: "#999" },
    }),
    option: (base, { data, isFocused, isSelected }) => {
      const colors = roleColors[data.value] || {};
      return {
        ...base,
        backgroundColor: isSelected
          ? colors.bg
          : isFocused
          ? "#f5f5f5"
          : "white",
        color: isSelected ? colors.color : "#333",
        cursor: "pointer",
      };
    },
    singleValue: (base, { data }) => {
      const colors = roleColors[data.value] || {};
      return {
        ...base,
        backgroundColor: colors.bg,
        color: colors.color,
        padding: "2px 8px",
        borderRadius: "4px",
        fontWeight: 600,
      };
    },
  };

  return (
    <>
      <div className="table-container">
        <table className="intern-table">
          <thead>
            <tr>
              <th>Họ và tên</th>
              <th>Email</th>
              <th>SĐT</th>
              <th>Địa chỉ</th>
              <th>Vai trò</th>
              <th>Ngày cập nhật</th>
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
            ) : users.length === 0 ? (
              <tr>
                <td colSpan="7" className="text-center">
                  Không có dữ liệu
                </td>
              </tr>
            ) : (
              users.map((user) => (
                <tr key={user.id}>
                  <td>{user.fullName}</td>
                  <td>{user.email}</td>
                  <td>{user.phone || "-"}</td>
                  <td title={user.address}>{user.address || "-"}</td>
                  <td>
                    <Select
                      value={roleOptions.find((r) => r.value === user.role)}
                      options={roleOptions}
                      onChange={(selected) =>
                        editUser({
                          id: user.id,
                          role: selected.value,
                          active: user.active,
                        })
                      }
                      styles={{
                        ...customStyles,
                        menuPortal: (base) => ({
                          ...base,
                          zIndex: 9999, // giúp menu luôn nổi
                        }),
                      }}
                      menuPortalTarget={document.body}
                      menuPlacement="auto"
                    />
                  </td>
                  <td>{user.updatedAt}</td>
                  <td className="action-col">
                    <button
                      className="icon-btn"
                      onClick={() =>
                        editUser({
                          id: user.id,
                          role: user.role,
                          active: !user.active,
                        })
                      }
                    >
                      {user.active ? (
                        <Ban size={20} color="red" />
                      ) : (
                        <Unlock size={20} color="#16a34a" />
                      )}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </>
  );
};

export default UserTable;
