import React from "react";
// Đảm bảo đường dẫn import Modal của bạn là đúng
import Modal from "./Modal"; 

const ViewEvaluationModal = ({ evaluation, onClose, onEdit }) => {
  if (!evaluation) return null;

  const { intern, averageScore, assessment } = evaluation;

  // Convert soft skill from enum to Vietnamese for display
  const formatSoftSkill = (skill) => {
    switch (skill) {
      case "GOOD":
        return "Tốt";
      case "FAIR":
        return "Khá";
      case "AVERAGE":
        return "Trung bình";
      case "POOR":
        return "Kém";
      default:
        return "N/A";
    }
  };

  return (
    <Modal title={`Xem Đánh Giá: ${intern.name}`} onClose={onClose}>
      <div className="evaluation-modal-content">
        <div className="section">
          <h3>Kỹ năng chuyên môn</h3>
          <p>
            Hiểu biết chuyên môn:{" "}
            <span className="score">{evaluation.expertiseScore}/10</span>
          </p>
          <p>
            Chất lượng công việc / độ chính xác:{" "}
            <span className="score">{evaluation.qualityScore}/10</span>
          </p>
          <p>
            Tư duy giải quyết vấn đề:{" "}
            <span className="score">{evaluation.problemSolvingScore}/10</span>
          </p>
          <p>
            Khả năng học hỏi công nghệ mới:{" "}
            <span className="score">
              {evaluation.technologyLearningScore}/10
            </span>
          </p>
          <div className="average-score-display">
            Trung bình: <span>{averageScore}/10</span>
          </div>
        </div>

        <div className="section">
          <h3>Thái độ & Kỹ năng mềm</h3>
          <p className="soft-skill-value">
            {formatSoftSkill(evaluation.softSkill)}
          </p>
        </div>

        <div className="section">
          <h3>Nhận xét</h3>
          <textarea
            value={assessment}
            readOnly
            rows="4"
            className="evaluation-comment-view"
          />
        </div>

        {/* CHỈNH SỬA PHẦN NÀY: Dùng Flexbox với justify-content: space-between */}
        <div 
          className="modal-actions"
          style={{ display: 'flex', justifyContent: 'space-between', marginTop: '20px' }}
        >
          {/* NÚT ĐÓNG (Ở BÊN TRÁI) */}
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onClose}
            style={{ 
              width: "fit-content", 
              padding: "10px 20px",
              // Thêm màu nền và viền nếu nút Đóng là màu trắng như ảnh
              background: 'white',
              color: '#374151',
              border: '1px solid #d1d5db'
            }} 
          >
            Đóng
          </button>
          
          {/* NÚT CHỈNH SỬA (Ở BÊN PHẢI) */}
          <button
            type="button"
            className="btn btn-primary"
            onClick={onEdit}
            style={{ 
              background: "#f59e0b", 
              color: "white", 
              width: "fit-content", 
              padding: "10px 20px" 
            }}
          >
            Chỉnh sửa
          </button>
        </div>
      </div>
    </Modal>
  );
};

export default ViewEvaluationModal;