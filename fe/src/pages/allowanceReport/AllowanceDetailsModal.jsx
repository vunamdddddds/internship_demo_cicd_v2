import React from "react";

const modalOverlayStyle = {
  position: "fixed",
  top: 0,
  left: 0,
  width: "100%",
  height: "100%",
  backgroundColor: "rgba(0, 0, 0, 0.5)",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  zIndex: 1000,
};

const modalContentStyle = {
  backgroundColor: "white",
  padding: "2rem",
  borderRadius: "8px",
  width: "80%",
  maxWidth: "900px",
  maxHeight: "80vh",
  overflowY: "auto",
};

const tableStyle = {
    width: "100%",
    borderCollapse: "collapse",
    marginTop: "1rem",
  };
  
  const thStyle = {
    border: "1px solid #ddd",
    padding: "12px",
    backgroundColor: "#f2f2f2",
    textAlign: "left",
  };
  
  const tdStyle = {
    border: "1px solid #ddd",
    padding: "12px",
  };

const AllowanceDetailsModal = ({
  isOpen,
  onClose,
  data,
  isLoading,
  month,
}) => {
  if (!isOpen) {
    return null;
  }

  return (
    <div style={modalOverlayStyle} onClick={onClose}>
      <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
        <h2 style={{marginTop: 0}}>Chi tiết Phụ cấp Tháng {month}</h2>
        {isLoading ? (
          <p>Đang tải dữ liệu chi tiết...</p>
        ) : !data || data.length === 0 ? (
          <p>Không có dữ liệu chi tiết cho tháng này.</p>
        ) : (
          <table style={tableStyle}>
            <thead>
              <tr>
                <th style={thStyle}>Tên Intern</th>
                <th style={thStyle}>Gói Phụ cấp</th>
                <th style={thStyle}>Số tiền</th>
                <th style={thStyle}>Ngày công</th>
                <th style={thStyle}>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {data.map((item) => (
                <tr key={item.id}>
                  <td style={tdStyle}>{item.intern?.user?.fullName || "N/A"}</td>
                  <td style={tdStyle}>{item.allowancePackage?.name || "N/A"}</td>
                  <td style={tdStyle}>{item.amount?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</td>
                  <td style={tdStyle}>{item.workDays}</td>
                  <td style={tdStyle}>{item.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <button
          onClick={onClose}
          style={{
            marginTop: "1.5rem",
            padding: "10px 15px",
            backgroundColor: "#6c757d",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Đóng
        </button>
      </div>
    </div>
  );
};

export default AllowanceDetailsModal;
