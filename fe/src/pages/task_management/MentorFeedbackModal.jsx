import React, { useState, useEffect } from "react";
import styles from './SprintReviewModal.module.css';
import { X, Send } from "lucide-react";
import { toast } from "react-toastify";
import SprintApi from "../../api/SprintApi";

const evaluationLevels = [
  { key: "feedbackGood", label: "Hài lòng", color: "#48bb78" },
  { key: "feedbackBad", label: "Chưa hài lòng", color: "#f6ad55" },
  { key: "feedbackImprove", label: "Cần cải thiện", color: "#f56565" }
];

function MentorFeedbackModal({
  isOpen,
  onClose,
  teamName,
  sprintName,
  sprintId,
  onFeedbackSuccess
}) {
  const [feedback, setFeedback] = useState({
    feedbackGood: "",
    feedbackBad: "",
    feedbackImprove: ""
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (!isOpen) return;
    // Nếu bạn có API lấy feedback trước đó, gọi ở đây để setFeedback
    // Ví dụ:
    // SprintApi.getMentorFeedback(sprintId).then(data => setFeedback(data));
    setFeedback({
      feedbackGood: "",
      feedbackBad: "",
      feedbackImprove: ""
    });
  }, [isOpen, sprintId]);

  if (!isOpen) return null;

  const handleChange = (key, value) => {
    setFeedback(prev => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async () => {
    const hasContent = Object.values(feedback).some(text => text.trim());
    if (!hasContent) {
      alert("Vui lòng nhập ít nhất một nhận xét!");
      return;
    }

    setIsSubmitting(true);
    try {
      await SprintApi.evaluateSprint(sprintId, feedback);
      toast.success("Gửi phản hồi thành công!");
      onFeedbackSuccess?.();
      onClose();
    } catch (error) {
      toast.error("Lỗi khi gửi phản hồi!");
      console.error("Error submitting feedback:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const canSubmit = Object.values(feedback).some(text => text.trim());

  return (
    <div className={styles.modalOverlay} onClick={onClose}>
      <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
        {/* HEADER */}
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>
            {teamName} - {sprintName}
          </h2>
          <button onClick={onClose} className={styles.closeButton}>
            <X size={20} />
          </button>
        </div>

        {/* FEEDBACK FORM */}
        <div className={styles.feedbackForm}>
          {evaluationLevels.map(({ key, label, color }) => (
            <div key={key} className={styles.feedbackItem}>
              <div className={styles.levelLabel} style={{ color }}>
                {label}
              </div>
              <textarea
                className={styles.textarea}
                placeholder="Nhập nhận xét..."
                value={feedback[key]}
                onChange={e => handleChange(key, e.target.value)}
                rows={4}
              />
            </div>
          ))}
        </div>

        {/* FOOTER */}
        <div className={styles.modalFooter}>
          <button
            className={`${styles.btn} ${styles.btnCancel}`}
            onClick={onClose}
          >
            Hủy
          </button>
          <button
            className={`${styles.btn} ${styles.btnConfirm}`}
            onClick={handleSubmit}
            disabled={!canSubmit || isSubmitting}
          >
            {isSubmitting ? (
              <>Đang gửi...</>
            ) : (
              <>
                <Send size={16} /> Gửi phản hồi
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}

export default MentorFeedbackModal;
