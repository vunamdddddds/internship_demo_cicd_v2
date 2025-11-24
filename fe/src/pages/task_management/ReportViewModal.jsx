// src/components/task_management/ReportViewModal.jsx
import React from "react";
import { X, FileText, Download } from "lucide-react";
import styles from "../../pages/mentor/TaskManagementPage.module.css";

const ReportViewModal = ({ isOpen, onClose, report }) => {
  if (!isOpen || !report) return null;

  return (
    <div className={styles.modalOverlay} onClick={onClose}>
      <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
        {/* HEADER */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h2 className={styles.modalTitle}>Xem Báo Cáo Sprint</h2>
          <button onClick={onClose} style={{ background: "none", border: "none", cursor: "pointer" }}>
            <X size={20} />
          </button>
        </div>

        {/* DESCRIPTION */}
        <p className={styles.modalDescription}>
          Báo cáo do thực tập sinh nộp.
        </p>

        {/* FILE INFO */}
        <div style={{ marginBottom: "1.5rem", padding: "1rem", background: "#f7fafc", borderRadius: "6px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "8px" }}>
            <FileText size={18} style={{ color: "#4299e1" }} />
            <strong>{report.fileName}</strong>
          </div>
          <div style={{ fontSize: "14px", color: "#4a5568" }}>
            <p>Kích thước: {(report.size / 1024).toFixed(2)} KB</p>
            <p>Nộp lúc: {new Date(report.submittedAt).toLocaleString("vi-VN")}</p>
          </div>
        </div>

        {/* ACTION BUTTONS */}
        <div style={{ display: "flex", gap: "8px", flexWrap: "wrap" }}>
          <a
            href={report.url}
            target="_blank"
            rel="noopener noreferrer"
            className={`${styles.btn} ${styles.btnConfirm}`}
            style={{ display: "flex", alignItems: "center", gap: "5px" }}
          >
            <FileText size={16} /> Mở PDF
          </a>
          <a
            href={report.url}
            download={report.fileName}
            className={`${styles.btn} ${styles.btnConfirm}`}
            style={{ display: "flex", alignItems: "center", gap: "5px" }}
          >
            <Download size={16} /> Tải về
          </a>
        </div>

        {/* FOOTER */}
        <div className={styles.modalFooter}>
          <button onClick={onClose} className={`${styles.btn} ${styles.btnCancel}`}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default ReportViewModal;