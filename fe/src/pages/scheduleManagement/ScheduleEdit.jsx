import { X } from "lucide-react";
import React from "react";
import { useState } from "react";
import { updateSchedule, deleteSchedule } from "~/services/WorkScheduleService";

const ScheduleEdit = ({
  onClose,
  selectedEvent,
  handleUpdate,
  setIsChanged,
  handleDelete,
}) => {
  const [formData, setFormData] = useState({
    id: selectedEvent.id,
    timeStart: selectedEvent.timeStart,
    timeEnd: selectedEvent.timeEnd,
  });

  const handleChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const removeSchedule = async () => {
    const data = await deleteSchedule(selectedEvent.id);
    if (data) {
      handleDelete(data);
      setIsChanged(true);
      onClose();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await updateSchedule(formData);
    if (data) {
      handleUpdate(data);
      setIsChanged(true);
      onClose();
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Sửa lịch nhóm</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <label>Thời gian bắt đầu</label>
          <input
            type="time"
            value={formData.timeStart}
            onChange={(e) => handleChange("timeStart", e.target.value)}
            required
          />

          <label>Thời gian kết thúc</label>
          <input
            type="time"
            value={formData.timeEnd}
            onChange={(e) => handleChange("timeEnd", e.target.value)}
            required
          />

          <div className="modal-actions">
            <button type="submit" className="btn btn-save">
              Lưu
            </button>
            <button
              type="button"
              onClick={() => removeSchedule()}
              className="btn btn-delete"
            >
              Xóa
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

export default ScheduleEdit;
