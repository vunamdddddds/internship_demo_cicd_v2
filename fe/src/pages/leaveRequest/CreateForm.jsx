import React, { useState } from "react";
import { X } from "lucide-react";
import Select from "react-select";
import { createLeaveRequest } from "~/services/LeaveRequestService";

const CreateForm = ({ onClose, typeOptions, onAddLeaveRequest }) => {
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    type: "",
    date: "",
    reason: "",
    attachedFile: null,
  });

  const handleChange = (key, value) => {
    setFormData({ ...formData, [key]: value });
  };

  const clearForm = () => {
    setFormData({
      type: "",
      date: "",
      reason: "",
      attachedFile: null,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const data = await createLeaveRequest(formData);
    if (data) {
      onAddLeaveRequest(data);
      clearForm();
      onClose();
    }
    setLoading(false);
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Tạo đơn xin phép</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <label>Loại đơn</label>
          <Select
            options={typeOptions}
            onChange={(selected) => handleChange("type", selected.value)}
            placeholder="Chọn loại đơn"
            isDisabled={loading}
          />

          <label>Ngày xin phép</label>
          <input
            type="date"
            value={formData.date}
            onChange={(e) => handleChange("date", e.target.value)}
            required
            isDisabled={loading}
          />

          <label>Lý do</label>
          <textarea
            rows="6"
            placeholder="Nhập lý do..."
            value={formData.reason}
            onChange={(e) => handleChange("reason", e.target.value)}
            required
            style={{ resize: "none", width: "100%" }}
            isDisabled={loading}
          ></textarea>

          <label>File đính kèm</label>
          <input
            type="file"
            accept=".pdf,.jpg,.png,.jpeg"
            onChange={(e) => handleChange("attachedFile", e.target.files[0])}
            isDisabled={loading}
          />

          <div className="modal-actions">
            <button type="submit" className="btn btn-save" disabled={loading}>
              {loading ? "Đang lưu..." : "Lưu"}
            </button>
            <button
              type="button"
              onClick={() => {
                if (!loading) {
                  clearForm();
                  onClose();
                }
              }}
              className="btn btn-cancel"
              disabled={loading}
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateForm;
