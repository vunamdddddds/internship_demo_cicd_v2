import React, { useState, useEffect } from "react";
import SprintApi from "../../api/SprintApi";

// Re-using the simple modal structure
const Modal = ({ children, onClose, title }) => (
  <div
    style={{
      position: "fixed",
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: "rgba(0, 0, 0, 0.5)",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      zIndex: 1000,
    }}
  >
    <div
      style={{
        background: "white",
        padding: "25px",
        borderRadius: "5px",
        width: "90%",
        maxWidth: "500px",
        position: "relative",
      }}
    >
      <button
        onClick={onClose}
        style={{ position: "absolute", top: "10px", right: "10px" }}
      >
        &times;
      </button>
      <h2>{title}</h2>
      {children}
    </div>
  </div>
);

function EditSprintModal({ isOpen, onClose, sprint, onSprintUpdated, onDeleteRequest }) {
  const [name, setName] = useState("");
  const [goal, setGoal] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (sprint) {
      setName(sprint.name || "");
      setGoal(sprint.goal || "");
      setStartDate(sprint.startDate || "");
      setEndDate(sprint.endDate || "");
    }
  }, [sprint]);

  if (!isOpen) {
    return null;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError("");

    const sprintData = { name, goal, startDate, endDate };

    try {
      await SprintApi.update(sprint.id, sprintData);
      onSprintUpdated();
      onClose();
    } catch (err) {
      const errorMessage = err.response?.data?.message || "Cập nhật sprint không thành công. Vui lòng kiểm tra lại ngày tạo.";
      setError(errorMessage);
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = () => {
    onDeleteRequest(sprint.id);
  };

  return (
    <Modal onClose={onClose} title={`Chỉnh sửa Sprint: ${sprint?.name}`}>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: "15px" }}>
          <label>Tên Sprint</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            style={{ width: "100%", padding: "8px" }}
          />
        </div>
        <div style={{ marginBottom: "15px" }}>
          <label>Mục tiêu</label>
          <textarea
            value={goal}
            onChange={(e) => setGoal(e.target.value)}
            style={{ width: "100%", padding: "8px", minHeight: "80px" }}
          />
        </div>
        <div style={{ display: "flex", gap: "15px", marginBottom: "15px" }}>
          <div style={{ flex: 1 }}>
            <label>Ngày bắt đầu</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              style={{ width: "100%", padding: "8px" }}
            />
          </div>
          <div style={{ flex: 1 }}>
            <label>Ngày kết thúc</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              style={{ width: "100%", padding: "8px" }}
            />
          </div>
        </div>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <button
            type="button"
            onClick={handleDelete}
            disabled={isSubmitting}
            style={{
              padding: "10px 20px",
              border: "none",
              borderRadius: "5px",
              cursor: "pointer",
              backgroundColor: "#fef2f2",
              color: "#ef4444",
              fontWeight: "bold",
            }}
          >
            Xóa Sprint
          </button>
          <div>
            <button type="button" onClick={onClose} disabled={isSubmitting}>
              Đóng
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              style={{ marginLeft: "10px" }}
            >
              {isSubmitting ? "Đang lưu..." : "Lưu thay đổi"}
            </button>
          </div>
        </div>
      </form>
    </Modal>
  );
}

export default EditSprintModal;
