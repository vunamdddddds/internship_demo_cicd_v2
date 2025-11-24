import React, { useState } from "react";
import { X } from "lucide-react";
import Select from "react-select";
import { createIntern } from "~/services/InternService";

const AddInternModal = ({
  universities,
  majors,
  onClose,
  onAddIntern,
  internshipProgram,
}) => {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    address: "",
    majorId: null,
    universityId: null,
    internshipProgramId: null,
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
      majorId: null,
      universityId: null,
      internshipProgramId: null,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await createIntern(formData);
    if (data) {
      onAddIntern(data);
      clearForm();
      onClose();
    }
  };

  const universityOptions = universities
    .filter((u) => u.id !== 0)
    .map((u) => ({ value: u.id, label: u.name }));

  const majorOptions = majors
    .filter((m) => m.id !== 0)
    .map((m) => ({ value: m.id, label: m.name }));

  const internshipProgramOptions = internshipProgram
    .filter((m) => m.id !== 0)
    .map((m) => ({ value: m.id, label: m.name }));

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Thêm tài khoản thực tập</h3>
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

          <label>Chuyên ngành</label>
          <Select
            options={majorOptions}
            value={majorOptions.find((opt) => opt.value === formData.majorId)}
            onChange={(selected) => handleChange("majorId", selected.value)}
            placeholder="Chọn chuyên ngành"
          />

          <label>Trường học</label>
          <Select
            options={universityOptions}
            value={universityOptions.find(
              (opt) => opt.value === formData.universityId
            )}
            onChange={(selected) =>
              handleChange("universityId", selected.value)
            }
            placeholder="Chọn trường học"
          />

          <label>Số điện thoại</label>
          <input
            type="text"
            placeholder="Nhập số điện thoại"
            value={formData.phone}
            onChange={(e) => handleChange("phone", e.target.value)}
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

export default AddInternModal;
