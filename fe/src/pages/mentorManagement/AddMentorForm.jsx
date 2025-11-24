import React, { useState } from "react";
import { X } from "lucide-react";
import Select from "react-select";
import { createMentor } from "~/services/MentorService";

const AddMentorModal = ({ onClose, onAddMentor, departmentOptions }) => {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    departmentName: "",
    address: "",
  });

  const handleChange = (key, value) => {
    setFormData({ ...formData, [key]: value });
  };

  const clearForm = () => {
    setFormData({
      fullName: "",
      email: "",
      phone: "",
      departmentId: 0,
      address: "",
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await createMentor(formData);
    if (data) {
      onAddMentor(data);
      clearForm();
      onClose();
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Thêm Mentor</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          {/* Họ và tên */}
          <div className="form-group">
            <label>Họ và tên</label>
            <input
              type="text"
              value={formData.fullName}
              onChange={(e) => handleChange("fullName", e.target.value)}
              required
            />
          </div>

          {/* Email */}
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => handleChange("email", e.target.value)}
              required
            />
          </div>

          {/* Số điện thoại */}
          <div className="form-group">
            <label>Số điện thoại</label>
            <input
              type="text"
              value={formData.phone}
              onChange={(e) => handleChange("phone", e.target.value)}
            />
          </div>

          {/* Phòng ban (react-select) */}
          <div className="form-group">
            <label>Phòng ban</label>
            <Select
              className="custom-select"
              options={departmentOptions}
              placeholder="Chọn phòng ban..."
              value={departmentOptions.find(
                (opt) => opt.value === formData.departmentId
              )}
              onChange={(selected) =>
                handleChange("departmentId", selected?.value)
              }
            />
          </div>

          {/* Địa chỉ */}
          <div className="form-group">
            <label>Địa chỉ</label>
            <input
              type="text"
              value={formData.address}
              onChange={(e) => handleChange("address", e.target.value)}
            />
          </div>

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

export default AddMentorModal;
