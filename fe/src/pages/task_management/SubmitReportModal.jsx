// src/components/task_management/SubmitReportModal.jsx
import React, { useState, useRef } from "react";
import { Download, Upload, X } from "lucide-react";
import styles from './SprintReviewModal.module.css';
import ReportApi from "../../api/ReportApi";
import { toast } from "react-toastify";

const SubmitReportModal = ({ isOpen, onClose, sprintId, onSubmitSuccess }) => {
  const SAMPLE_REPORT_URL = '../../../public/MẪU BÁO CÁO THỰC TẬP THEO TUẦN.pdf';
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);
  if (!isOpen) return null;

  const handleFileChange = (e) => {
    const selected = e.target.files[0];
    if (selected) {
      setFile(selected);
    } else {
      setFile(null);
    }
  };

  const handleSubmit = async () => {
    if (!file) {
      toast("Lỗi: Vui lòng chọn file báo cáo!");
      return;
    }

    setUploading(true);
    try {
      await ReportApi.sendReport(sprintId, file);
      toast("Nộp báo cáo thành công!");
      onSubmitSuccess?.();
      onClose();
    } catch (err) {
      toast(err.message || "Nộp thất bại!");
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className={styles.modalOverlay} onClick={onClose}>
      <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
        {/* HEADER */}
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>Nộp Báo Cáo Sprint</h2>
          <button onClick={onClose} className={styles.closeButton}>
            <X size={20} />
          </button>
        </div>

        {/* SAMPLE DOWNLOAD BUTTON */}
        <div style={{ marginBottom: "1.5rem" }}>
          <div style={{ marginBottom: "1rem" }}>
            <a
              href={SAMPLE_REPORT_URL}
              download="mau-bao-cao-sprint.pdf"
              className={`${styles.btn} ${styles.btnConfirm}`}
              style={{ display: "inline-flex", alignItems: "center", gap: "6px", backgroundColor: "#ef4444" }}
            >
              <Download size={16} /> Tải Mẫu Báo Cáo
            </a>
          </div>

          {/* UPLOAD */}
          <input
            ref={fileInputRef}
            type="file"
            onChange={handleFileChange}
            style={{ display: "none" }}
          />
          <button
            onClick={() => fileInputRef.current?.click()}
            className={`${styles.btn} ${styles.btnConfirm}`}
            style={{ display: "inline-flex", alignItems: "center", gap: "6px", backgroundColor: "#10b981" }}
          >
            <Upload size={16} /> {file ? "Thay đổi file" : "Tải lên báo cáo"}
          </button>
          {file && (
            <p className={styles.fileInfo} style={{ marginTop: "1rem" }}>
              Đã chọn: <strong>{file.name}</strong> ({(file.size / 1024).toFixed(2)} KB)
            </p>
          )}
        </div>

        {/* FOOTER */}
        <div className={styles.modalFooter}>
          <button onClick={onClose} className={`${styles.btn} ${styles.btnCancel}`}>
            Hủy
          </button>
          <button
            onClick={handleSubmit}
            disabled={!file || uploading}
            className={`${styles.btn} ${styles.btnConfirm}`}
          >
            {uploading ? "Đang gửi..." : "Gửi Báo Cáo"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default SubmitReportModal;