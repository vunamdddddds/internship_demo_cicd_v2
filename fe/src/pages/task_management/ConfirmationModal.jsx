import React from "react";

// A generic, reusable confirmation modal
function ConfirmationModal({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
}) {
  if (!isOpen) {
    return null;
  }

  return (
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
        zIndex: 1050, // Higher z-index to appear above other modals
      }}
    >
      <div
        style={{
          background: "white",
          padding: "25px",
          borderRadius: "8px",
          width: "90%",
          maxWidth: "400px",
          textAlign: "center",
          boxShadow: "0 5px 15px rgba(0,0,0,0.3)",
        }}
      >
        <h2 style={{ marginTop: 0, color: "#172B4D" }}>{title}</h2>
        <p style={{ color: "#5E6C84", marginBottom: "25px" }}>{message}</p>
        <div style={{ display: "flex", justifyContent: "center", gap: "15px" }}>
          <button
            onClick={onClose}
            style={{
              padding: "10px 20px",
              border: "none",
              borderRadius: "5px",
              cursor: "pointer",
              backgroundColor: "#f4f5f7",
              color: "#172B4D",
              fontWeight: "bold",
            }}
          >
            Đóng
          </button>
          <button
            onClick={onConfirm}
            style={{
              padding: "10px 20px",
              border: "none",
              borderRadius: "5px",
              cursor: "pointer",
              backgroundColor: "#de350b", // A destructive action color
              color: "white",
              fontWeight: "bold",
            }}
          >
            Xóa
          </button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmationModal;
