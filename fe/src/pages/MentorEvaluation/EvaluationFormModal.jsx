import React, { useMemo } from "react";
// Thay đổi đường dẫn này nếu Modal.jsx nằm ở vị trí khác
import Modal from "./Modal"; 

const criteria = [
  { key: "knowledge", label: "Hiểu biết chuyên môn" },
  { key: "quality", label: "Chất lượng công việc / độ chính xác" },
  { key: "problemSolving", label: "Tư duy giải quyết vấn đề" },
  { key: "learning", label: "Khả năng học hỏi công nghệ mới" },
];

const EvaluationFormModal = ({ intern, form, setForm, onSubmit, onClose }) => {
  
  // Hàm xử lý thay đổi giá trị (chủ yếu dùng cho thanh trượt)
  const handleChange = (e) => {
    const { name, value, type } = e.target;
    const [parent, child] = name.split(".");
    
    if (child) {
        let numericValue = parseFloat(value);
        
        // Nếu là thanh trượt (range), áp dụng giới hạn và làm tròn ngay lập tức
        if (type === 'range') {
            let finalValue = numericValue;
            if (finalValue < 1 || isNaN(finalValue)) {
                finalValue = 1;
            } else if (finalValue > 10) {
                finalValue = 10;
            }
            // Làm tròn theo step (0.5)
            finalValue = Math.round(finalValue * 2) / 2;
            
            setForm((prev) => ({
                ...prev,
                [parent]: { ...prev[parent], [child]: finalValue },
            }));
        } else {
            // Nếu là input number, lưu giá trị nhập vào dạng chuỗi để cho phép xóa mượt mà
            setForm((prev) => ({
                ...prev,
                [parent]: { ...prev[parent], [child]: value },
            }));
        }
    } else {
        setForm((prev) => ({ ...prev, [name]: value }));
    }
  };
  
  // HÀM XỬ LÝ KHI NGƯỜI DÙNG RỜI KHỎI Ô INPUT (ON BLUR)
  const handleBlur = (e) => {
      const { name, value } = e.target;
      const [parent, child] = name.split(".");
      
      let numericValue = parseFloat(value);
      let finalValue;
      
      if (isNaN(numericValue) || numericValue < 1) {
          finalValue = 1;
          if (numericValue !== 0 && value !== "") {
              alert("Điểm tối thiểu là 1.");
          }
      } else if (numericValue > 10) {
          finalValue = 10;
          alert("Điểm tối đa là 10.");
      } else {
          finalValue = numericValue;
      }
      
      // Đảm bảo giá trị cuối cùng được làm tròn theo step (0.5)
      finalValue = Math.round(finalValue * 2) / 2;

      setForm((prev) => ({
          ...prev,
          [parent]: { ...prev[parent], [child]: finalValue },
      }));
  };
  
  // Tính điểm trung bình (sử dụng giá trị số)
  const avgScore = useMemo(() => {
    const scores = Object.values(form.technical || {}).map(score => parseFloat(score) || 0); // Ép kiểu an toàn
    const validScores = scores.filter(score => score >= 1 && score <= 10);
    
    if (validScores.length === 0) return 0.0;
    
    const sum = validScores.reduce((a, b) => a + b, 0);
    return (sum / validScores.length).toFixed(1);
    
  }, [form.technical]);


  return (
    <Modal title={`${intern.evaluated ? "Chỉnh sửa đánh giá" : "Đánh giá"}: ${intern.name}`} onClose={onClose}>
      <form onSubmit={onSubmit}>
        <div className="form-section">
          <h3>Kỹ năng chuyên môn</h3>
          {criteria.map((c) => (
            <div className="form-group" key={c.key}>
              <label>{c.label}</label>
              <div 
                className="score-input-group" 
                style={{ display: 'flex', alignItems: 'center', gap: '10px' }} 
              > 
                
                {/* 1. Thanh trượt */}
                <input
                  type="range"
                  name={`technical.${c.key}`}
                  min="1"
                  max="10"
                  step="0.5" 
                  // Dùng giá trị số từ form state
                  value={parseFloat(form.technical[c.key]) || 1} 
                  onChange={handleChange}
                  className="slider"
                  style={{ flexGrow: 1 }} 
                />

                {/* 2. Ô điền số (UX mượt mà hơn) */}
                <input
                  type="number"
                  name={`technical.${c.key}`}
                  step="0.5" 
                  // Sử dụng giá trị từ form state, KHÔNG ép giá trị về 1 ngay để cho phép xóa mượt mà
                  value={form.technical[c.key]}
                  onChange={handleChange}
                  onBlur={handleBlur} // Kiểm tra và ép giá trị khi rời khỏi input
                  className="score-number-input"
                  style={{ width: '70px', padding: '5px 8px' }} 
                />

                {/* 3. Hiển thị điểm */}
                <span className="slider-value" style={{ width: '40px', textAlign: 'right' }}>
                  {parseFloat(form.technical[c.key]) || 1}/10
                </span>
              </div>
            </div>
          ))}
          <div className="avg-score">
            <strong>Điểm trung bình: {avgScore}</strong>
          </div>
        </div>

        <div className="form-section">
          <h3>Thái độ & Kỹ năng mềm</h3>
          <div className="radio-group">
            {["Tốt", "Khá", "Trung bình", "Kém"].map((level) => (
              <label key={level} className="radio-label">
                <input
                  type="radio"
                  name="softSkills"
                  value={level}
                  checked={form.softSkills === level}
                  onChange={handleChange}
                />
                <span className="radio-text">{level}</span>
              </label>
            ))}
          </div>
        </div>

        <div className="form-section">
          <h3>Nhận xét</h3>
          <textarea
            name="assessment"
            value={form.assessment}
            onChange={handleChange}
            rows="5"
            placeholder="Ưu điểm: ... | Khuyết điểm: ..."
            className="textarea-input"
            required
          />
        </div>

        <div className="form-actions">
          <button type="button" className="btn-cancel" onClick={onClose}>
            Hủy
          </button>
          <button type="submit" className="btn-submit">
            {intern.evaluated ? "Cập nhật" : "Gửi Đánh Giá"}
          </button>
        </div>
      </form>
    </Modal>
  );
};

export default EvaluationFormModal;