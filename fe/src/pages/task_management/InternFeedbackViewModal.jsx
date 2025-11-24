// src/components/task_management/InternFeedbackViewModal.jsx
import React from "react";
import { X } from "lucide-react";
import styles from './SprintReviewModal.module.css';

const evaluationLevels = [
  { key: "feedbackGood", label: "Hài lòng", color: "#48bb78" },
  { key: "feedbackBad", label: "Chưa hài lòng", color: "#f6ad55" },
  { key: "feedbackImprove", label: "Cần cải thiện", color: "#f56565" }
];

function InternFeedbackViewModal({
  isOpen,
  onClose,
  teamName,
  sprintName,
  feedback
}) {
  if (!isOpen || !feedback) return null;

  return (
    <div className={styles.modalOverlay} onClick={onClose}>
      <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
        {/* HEADER */}
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>
            Phản hồi từ Mentor: {teamName} - {sprintName}
          </h2>
          <button onClick={onClose} className={styles.closeButton}>
            <X size={20} />
          </button>
        </div>

        {/* FEEDBACK DISPLAY */}
        <div className={styles.feedbackForm}>
          {evaluationLevels.map(({ key, label, color }) => {
            const text = feedback[key];
            if (!text?.trim()) return null;

            return (
              <div key={key} className={styles.feedbackItem}>
                <div
                  className={styles.levelLabel}
                  style={{ color }}
                >
                  {label}
                </div>
                <div
                  style={{
                    padding: "0.75rem",
                    backgroundColor: "#f7fafc",
                    borderRadius: "6px",
                    border: "1px solid #e2e8f0",
                    minHeight: "80px",
                    whiteSpace: "pre-wrap",
                    fontSize: "0.9rem",
                    color: "#2d3748"
                  }}
                >
                  {text}
                </div>
              </div>
            );
          })}
        </div>

        {/* FOOTER */}
        <div className={styles.modalFooter}>
          <button
            onClick={onClose}
            className={`${styles.btn} ${styles.btnCancel}`}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}

export default InternFeedbackViewModal;
