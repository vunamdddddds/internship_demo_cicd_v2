import React, { useState } from "react";
import { X } from "lucide-react";
import Select from "react-select";
import { createTeam } from "~/services/TeamService";

const AddTeamModal = ({ internshipPrograms, mentors, onClose, onAddTeam }) => {
  const [formData, setFormData] = useState({
    name: "",
    internshipProgramId: 0,
    mentorId: 0,
  });

  const handleChange = (key, value) => {
    setFormData({ ...formData, [key]: value });
  };

  const clearForm = () => {
    setFormData({
      name: "",
      internshipProgramId: 0,
      mentorId: 0,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await createTeam(formData);
    if (data) {
      onAddTeam(data);
      clearForm();
      onClose();
    }
  };

  const internshipProgramOptions = internshipPrograms.map((d) => ({
    value: d.id,
    label: d.name,
  }));

  const mentorOptions = mentors.map((m) => ({
    value: m.id,
    fullName: m.fullName,
    email: m.email,
    departmentName: m.departmentName,
  }));

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Thêm nhóm</h3>
          <button
            className="modal-close"
            onClick={() => {
              clearForm();
              onClose();
            }}
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <label>Tên nhóm</label>
          <input
            type="text"
            placeholder="Nhập tên nhóm"
            value={formData.name}
            onChange={(e) => handleChange("name", e.target.value)}
            required
          />

          <label>Kì thực tập</label>
          <Select
            options={internshipProgramOptions}
            value={internshipProgramOptions.find(
              (opt) => opt.value === formData.internshipProgramId
            )}
            onChange={(selected) =>
              handleChange("internshipProgramId", selected.value)
            }
            placeholder="Chọn kì thực tập"
          />

          <label>Mentor</label>
          <Select
            className="custom-select"
            options={mentorOptions}
            value={mentorOptions.find((opt) => opt.value === formData.mentorId)}
            onChange={(selected) => handleChange("mentorId", selected.value)}
            placeholder="Chọn mentor"
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
            <button
              type="button"
              onClick={() => {
                clearForm();
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

export default AddTeamModal;
