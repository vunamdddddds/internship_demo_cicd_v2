import React, { useState } from "react";
import TaskApi from "../../api/TaskApi";

// A simple modal component for demonstration
const Modal = ({ children, onClose }) => (
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
      {children}
    </div>
  </div>
);

function CreateTaskModal({
  isOpen,
  onClose,
  sprintId,
  initialStatus,
  teamMembers,
  onTaskCreated,
}) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [deadline, setDeadline] = useState("");
  const [assigneeId, setAssigneeId] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  if (!isOpen) {
    return null;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name) {
      setError("Tên nhiệm vụ là bắt buộc.");
      return;
    }
    setIsSubmitting(true);
    setError("");

    const taskData = {
      name,
      description,
      deadline: deadline || null,
      assigneeId: assigneeId ? parseInt(assigneeId, 10) : null,
      status: initialStatus, 
    };

    try {
      await TaskApi.createTask(sprintId, taskData);
      onTaskCreated();
      handleClose();
    } catch (err) {
      setError("Tạo Task thất bại. Vui lòng thử lại.");
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    // Reset form
    setName("");
    setDescription("");
    setDeadline("");
    setAssigneeId("");
    setError("");
    onClose();
  };

  return (
    <Modal onClose={handleClose}>
      <h2>Tạo Task Mới</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: "15px" }}>
          <label>Tên Task*</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            style={{ width: "100%", padding: "8px" }}
          />
        </div>
        <div style={{ marginBottom: "15px" }}>
          <label>Mô tả</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            style={{ width: "100%", padding: "8px", minHeight: "80px" }}
          />
        </div>
        <div style={{ marginBottom: "15px" }}>
          <label>Hạn hoàn thành</label>
          <input
            type="date"
            value={deadline}
            onChange={(e) => setDeadline(e.target.value)}
            style={{ width: "100%", padding: "8px" }}
          />
        </div>
        <div style={{ marginBottom: "15px" }}>
          <label>Phân công</label>
          <select
            value={assigneeId}
            onChange={(e) => setAssigneeId(e.target.value)}
            style={{ width: "100%", padding: "8px" }}
          >
            <option value="">Chưa được phân công</option>
            {teamMembers.map((member) => (
              <option key={member.id} value={member.id}>
                {member.fullName}
              </option>
            ))}
          </select>
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
            {isSubmitting ? "Đang tạo..." : "Tạo Task"}
          </button>
        </div>
      </form>
    </Modal>
  );
}

export default CreateTaskModal;
