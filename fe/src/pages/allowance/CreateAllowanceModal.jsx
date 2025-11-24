// src/pages/allowance/CreateAllowanceModal.jsx
import React, { useState, useEffect } from "react";
import Select from "react-select";
import { getAllInternsForSelection } from "~/services/InternService";
import "./CreateAllowanceModal.css";

const CreateAllowanceModal = ({ isOpen, onClose, onSubmit }) => {
  const [selectedIntern, setSelectedIntern] = useState(null);
  const [amount, setAmount] = useState("");
  const [description, setDescription] = useState("");
  const [error, setError] = useState("");

  const [interns, setInterns] = useState([]);
  const [isLoadingInterns, setIsLoadingInterns] = useState(false);

  useEffect(() => {
    // Fetch interns only when the modal is opened
    if (isOpen) {
      const fetchInterns = async () => {
        setIsLoadingInterns(true);
        const internOptions = await getAllInternsForSelection();
        setInterns(internOptions);
        setIsLoadingInterns(false);
      };
      fetchInterns();
    }
  }, [isOpen]);


  const handleSubmit = (e) => {
    e.preventDefault();
    if (!selectedIntern || !amount || parseFloat(amount) < 0) {
      setError("Vui lòng chọn thực tập sinh và nhập số tiền hợp lệ.");
      return;
    }
    onSubmit({
      internId: selectedIntern.value, // Pass the intern's ID
      amount: parseFloat(amount),
      description,
    });
    // Reset form
    setSelectedIntern(null);
    setAmount("");
    setDescription("");
    setError("");
  };
  
  const handleClose = () => {
    // Reset form state on close
    setSelectedIntern(null);
    setAmount("");
    setDescription("");
    setError("");
    onClose();
  }

  if (!isOpen) {
    return null;
  }

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Tạo Khoản Phụ Cấp Mới</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="internId">Thực tập sinh</label>
            <Select
              id="internId"
              options={interns}
              value={selectedIntern}
              onChange={setSelectedIntern}
              placeholder="Chọn hoặc tìm kiếm thực tập sinh..."
              isLoading={isLoadingInterns}
              isClearable
              noOptionsMessage={() => "Không tìm thấy thực tập sinh"}
            />
          </div>
          <div className="form-group">
            <label htmlFor="amount">Số tiền (VND)</label>
            <input
              type="number"
              id="amount"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="Nhập số tiền..."
              required
              min="0"
            />
          </div>
          <div className="form-group">
            <label htmlFor="description">Mô tả</label>
            <textarea
              id="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Nhập mô tả (Thưởng, phụ cấp tháng X...)"
            />
          </div>
          {error && <p className="error-message">{error}</p>}
          <div className="modal-actions">
            <button type="button" className="btn-cancel" onClick={handleClose}>
              Hủy
            </button>
            <button type="submit" className="btn-submit">
              Lưu
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateAllowanceModal;
