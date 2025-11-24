import React, { useState } from "react";
import { X } from "lucide-react";
import Select from "react-select";
import { createUser } from "~/services/UserService";

const AddUserForm = ({ onClose, roleOptions, onAddUser }) => {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    address: "",
    role: "",
  });

  const handleChange = (key, value) => {
    setFormData({ ...formData, [key]: value });
  };

  const clearForm = () => {
    setFormData({
      fullName: "",
      email: "",
      phone: "",
      address: "",
      role: "",
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await createUser(formData);
    if (data) {
      onAddUser(data);
      clearForm();
      onClose();
    }
  };

  const RoleOptions = roleOptions.filter((opt) => opt.value !== "");

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Thêm tài khoản người dùng</h3>
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
          <label>Họ và tên</label>
          <input
            type="text"
            placeholder="Nhập họ và tên"
            value={formData.fullName}
            onChange={(e) => handleChange("fullName", e.target.value)}
            required
          />

          <label>Email</label>
          <input
            type="email"
            placeholder="Nhập email"
            value={formData.email}
            onChange={(e) => handleChange("email", e.target.value)}
            required
          />

          <label>Vai trò</label>
          <Select
            options={RoleOptions}
            onChange={(selected) => handleChange("role", selected.value)}
            placeholder="Chọn vai trò"
          />

          <label>Số điện thoại</label>
          <input
            type="text"
            placeholder="Nhập số điện thoại"
            value={formData.phone}
            onChange={(e) => handleChange("phone", e.target.value)}
          />

          <label>Địa chỉ</label>
          <input
            type="text"
            placeholder="Nhập địa chỉ"
            value={formData.address}
            onChange={(e) => handleChange("address", e.target.value)}
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

export default AddUserForm;
