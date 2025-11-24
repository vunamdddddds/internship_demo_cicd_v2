import { X } from "lucide-react";
import React from "react";
import { useState } from "react";
import { createSchedule } from "~/services/WorkScheduleService";

const ScheduleCreate = ({ onClose, newEventInfo, handleAdd, setIsChanged }) => {
  const [formData, setFormData] = useState({
    idTeam: newEventInfo.idTeam,
    dayOfWeek: newEventInfo.dayOfWeek,
    timeStart: newEventInfo.timeStart,
    timeEnd: newEventInfo.timeEnd,
  });

  const handleChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await createSchedule(formData);
    if (data) {
      handleAdd(data);
      setIsChanged(true);
      onClose();
    }
  };

  return (
    <div onSubmit={handleSubmit} className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Thêm lịch nhóm</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form className="modal-form">
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
              Thêm
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

export default ScheduleCreate;
