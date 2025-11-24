import React, { useState } from "react";
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

function CreateSprintModal({ isOpen, onClose, teamId, onSprintCreated }) {
  const [name, setName] = useState("");
  const [goal, setGoal] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  if (!isOpen) {
    return null;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name || !startDate || !endDate) {
      setError("Không để trống Tên, Ngày bắt đầu và Ngày kết thúc.");
      return;
    }
    setIsSubmitting(true);
    setError("");

    const sprintData = { name, goal, startDate, endDate };

    try {
      await SprintApi.create(teamId, sprintData);
      onSprintCreated(); // Notify parent to refresh
      handleClose();
    } catch (err) {
      const errorMessage = err.response?.data?.message || "Không thể tạo sprint. Vui lòng kiểm tra ngày tạo.";
      setError(errorMessage);
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    // Reset form
    setName("");
    setGoal("");
    setStartDate("");
    setEndDate("");
    setError("");
    onClose();
  };

  return (
    <Modal onClose={handleClose} title="Tạo Sprint Mới">
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: "15px" }}>
          <label>Tên Sprint*</label>
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
            <label>Ngày bắt đầu*</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              style={{ width: "100%", padding: "8px" }}
            />
          </div>
          <div style={{ flex: 1 }}>
            <label>Ngày kết thúc*</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              style={{ width: "100%", padding: "8px" }}
            />
          </div>
        </div>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <div style={{ textAlign: "right" }}>
          <button type="button" onClick={handleClose} disabled={isSubmitting}>
            Đóng
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            style={{ marginLeft: "10px" }}
          >
            {isSubmitting ? "Đang tạo..." : "Tạo Sprint"}
          </button>
        </div>
      </form>
    </Modal>
  );
}

export default CreateSprintModal;
