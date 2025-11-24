import React from "react";
import Select from "react-select";
import { X } from "lucide-react";

const TeamEditModal = ({ formData, mentor, onChange, onSubmit, onClose }) => {
  const mentorOptions = [
    ...mentor.map((m) => ({
      value: m.id,
      fullName: m.fullName,
      email: m.email,
      departmentName: m.departmentName,
    })),
  ];

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Sửa thông tin nhóm</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={onSubmit} className="modal-form">
          <label>Tên nhóm</label>
          <input
            type="text"
            placeholder="Nhập tên nhóm"
            value={formData.name}
            onChange={(e) => onChange("name", e.target.value)}
            required
          />

          <label>Mentor</label>
          <Select
            className="custom-select"
            options={mentorOptions}
            value={mentorOptions.find((opt) => opt.value === formData.mentorId)}
            onChange={(selected) => onChange("mentorId", selected.value)}
            // 👇 Chỉ hiển thị email trong dropdown menu
            formatOptionLabel={(m, { context }) =>
              context === "menu" ? (
                <div>
                  <div>
                    {m.fullName}{" "}
                    {m.departmentName ? `- ${m.departmentName}` : ""}
                  </div>
                  <div
                    style={{
                      fontSize: "10px",
                      marginTop: "1px",
                    }}
                  >
                    {m.email}
                  </div>
                </div>
              ) : (
                <div>
                  {m.fullName}{" "}
                  {m.departmentName ? ` - ${m.departmentName}` : ""}
                </div>
              )
            }
          />

          <div className="modal-actions">
            <button type="submit" className="btn btn-save">
              Lưu
            </button>
            <button type="button" onClick={onClose} className="btn btn-cancel">
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TeamEditModal;
