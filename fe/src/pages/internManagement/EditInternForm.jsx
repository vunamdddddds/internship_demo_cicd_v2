import React from "react";
import Select from "react-select";
import { X } from "lucide-react";

const InternEditModal = ({
  formData,
  statusOptions,
  majorOptions,
  universityOptions,
  onChange,
  onSubmit,
  onClose,
}) => {
  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Sửa thông tin thực tập</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={onSubmit} className="modal-form">
          <label>Trạng thái</label>
          <Select
            options={statusOptions}
            value={statusOptions.find((opt) => opt.value === formData.status)}
            onChange={(option) => onChange("status", option.value)}
            placeholder="Chọn trạng thái"
          />

          <label>Chuyên ngành</label>
          <Select
            options={majorOptions}
            value={majorOptions.find((opt) => opt.value === formData.majorId)}
            onChange={(selected) => onChange("majorId", selected.value)}
            placeholder="Chọn chuyên ngành"
          />

          <label>Trường học</label>
          <Select
            options={universityOptions}
            value={universityOptions.find(
              (opt) => opt.value === formData.universityId
            )}
            onChange={(selected) => onChange("universityId", selected.value)}
            placeholder="Chọn trường học"
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

export default InternEditModal;
