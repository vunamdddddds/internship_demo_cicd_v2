import React from "react";
import { format } from "date-fns";

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

const buttonStyle = {
  padding: "8px 12px",
  backgroundColor: "#28a745",
  color: "white",
  border: "none",
  borderRadius: "4px",
  cursor: "pointer",
  textDecoration: "none",
};

const viewButtonStyle = {
    ...buttonStyle,
    backgroundColor: "#17a2b8",
    marginLeft: "8px",
}

const AllowanceReportTable = ({ reports, isLoading, onViewDetails }) => {
  if (isLoading) {
    return <p>Loading report history...</p>;
  }

  if (!reports || reports.length === 0) {
    return <p>No auto-generated reports found.</p>;
  }

  return (
    <div>
        <h3 style={{ marginTop: 0, marginBottom: "1rem" }}>Lịch sử Báo cáo Tổng hợp (Tự động)</h3>
        <table style={tableStyle}>
        <thead>
            <tr>
            <th style={thStyle}>Tháng Báo cáo</th>
            <th style={thStyle}>Tên File</th>
            <th style={{ ...thStyle, width: "120px" }}>Hành động</th>
            </tr>
        </thead>
        <tbody>
            {reports.map((report) => (
            <tr key={report.id}>
                <td style={tdStyle}>
                {format(new Date(report.reportMonth), "MM-yyyy")}
                </td>
                <td style={tdStyle}>{report.fileName}</td>
                <td style={tdStyle}>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <a
                        href={report.fileUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        style={buttonStyle}
                    >
                        Tải về
                    </a>
                    <button
                        onClick={() => onViewDetails(format(new Date(report.reportMonth), "yyyy-MM"))}
                        style={viewButtonStyle}
                    >
                        Xem
                    </button>
                  </div>
                </td>
            </tr>
            ))}
        </tbody>
        </table>
    </div>
  );
};

export default AllowanceReportTable;
