import React, { useState } from "react";
import { X } from "lucide-react";
import Select from "react-select";
import { createInternshipProgram } from "~/services/InternshipProgramService";

const AddInternshipProgramModal = ({
  onClose,
  departmentOptions,
  onAddInternshipProgram,
  convertToISO,
}) => {
  const [formData, setFormData] = useState({
    name: "",
    endPublishedTime: "",
    endReviewingTime: "",
    timeStart: "",
    departmentId: 0,
    allowance: "",           // ← mới
  });

  const handleChange = (key, value) => {
    setFormData({ ...formData, [key]: value });
  };

  const clearForm = () => {
    setFormData({
      name: "",
      endPublishedTime: "",
      endReviewingTime: "",
      timeStart: "",
      departmentId: 0,
      allowance: "",
    });
  };

  const handleSubmit = async (e, draft) => {
    e.preventDefault();
    const submitData = {
      ...formData,
      allowance: parseFloat(formData.allowance) || 0, // đảm bảo là số
      endPublishedTime: convertToISO(formData.endPublishedTime),
      endReviewingTime: convertToISO(formData.endReviewingTime),
      timeStart: convertToISO(formData.timeStart),
      draft: draft,
    };

    const data = await createInternshipProgram(submitData);
    if (data) {
      onAddInternshipProgram(data);
      clearForm();
      onClose();
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Thêm kỳ thực tập</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form className="modal-form">
          <div className="form-group">
            <label>Tên kỳ thực tập</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => handleChange("name", e.target.value)}
              placeholder="Nhập tên kỳ thực tập..."
              required
            />
          </div>

          <div className="form-group">
            <label>Phụ cấp (VND/tháng)</label>
            <input
              type="number"
              min="0"
              step="100000"
              value={formData.allowance}
              onChange={(e) => handleChange("allowance", e.target.value)}
              placeholder="Nhập phụ cấp..."
            />
          </div>

          <div className="form-group">
            <label>Hạn nộp hồ sơ</label>
            <input
              type="datetime-local"
              value={formData.endPublishedTime}
              onChange={(e) => handleChange("endPublishedTime", e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Hạn duyệt hồ sơ</label>
            <input
              type="datetime-local"
              value={formData.endReviewingTime}
              onChange={(e) => handleChange("endReviewingTime", e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Thời gian bắt đầu</label>
            <input
              type="datetime-local"
              value={formData.timeStart}
              onChange={(e) => handleChange("timeStart", e.target.value)}
              required
            />
          </div>

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
                handleChange("departmentId", selected?.value || 0)
              }
            />
          </div>

          <div className="modal-actions">
            <button
              type="button"
              className="btn btn-save"
              onClick={(e) => handleSubmit(e, false)}
            >
              Lưu
            </button>

            <button
              type="button"
              className="btn btn-secondary"
              onClick={(e) => handleSubmit(e, true)}
            >
              Lưu nháp
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

export default AddInternshipProgramModal;