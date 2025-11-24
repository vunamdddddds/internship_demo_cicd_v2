import React, { useState, useEffect } from "react";
import TaskApi from "../../api/TaskApi";

// Re-using the simple modal from CreateTaskModal for consistency
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
      {children}
    </div>
  </div>
);

function TaskDetailModal({
  isOpen,
  onClose,
  task,
  teamMembers,
  onTaskUpdated,
}) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [deadline, setDeadline] = useState("");
  const [assigneeId, setAssigneeId] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (task) {
      setName(task.name || "");
      setDescription(task.description || "");
      setDeadline(task.deadline || "");
      setAssigneeId(task.assignee_Id || "");
    }
  }, [task]);

  if (!isOpen || !task) {
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

    const updatedData = {
      name,
      description,
      deadline: deadline || null,
      // Note: Backend doesn't support changing assignee via updateTask yet,
      // but we build the UI for it. This field might be ignored by the server.
      assigneeId: assigneeId ? parseInt(assigneeId, 10) : null,
    };

    try {
      await TaskApi.updateTask(task.id, updatedData);
      onTaskUpdated(); // Notify parent to refresh
      onClose();
    } catch (err) {
      setError("Cập nhật không thành công. Vui lòng thử lại.");
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal onClose={onClose}>
      <h2>Chỉnh sửa Task: {task.name}</h2>
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
      </form>
    </Modal>
  );
}

export default TaskDetailModal;
