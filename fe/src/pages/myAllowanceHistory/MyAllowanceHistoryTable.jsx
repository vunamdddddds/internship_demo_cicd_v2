// src/pages/myAllowanceHistory/MyAllowanceHistoryTable.jsx
import React from 'react';

const MyAllowanceHistoryTable = ({ data, loading }) => {
    const formatCurrency = (amount) =>
        new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount);
    
    const formatDateTime = (dt) => {
        if (!dt) return 'N/A';
        return new Date(dt).toLocaleDateString('vi-VN');
    }

    return (
        <div className="table-container">
            <table className="intern-table">
                <thead>
                    <tr>
                        <th>Chương trình thực tập</th>
                        <th>Số tiền</th>
                        <th>Trạng thái</th>
                        <th>Ngày trả</th>
                        <th>Người trả</th>
                    </tr>
                </thead>
                <tbody>
                    {loading ? (
                        <tr>
                            <td colSpan="5" className="text-center">Đang tải...</td>
                        </tr>
                    ) : data.length === 0 ? (
                        <tr>
                            <td colSpan="5" className="text-center">Không có dữ liệu</td>
                        </tr>
                    ) : (
                        data.map((item) => (
                            <tr key={item.id}>
                                <td>{item.internshipProgramName}</td>
                                <td>{formatCurrency(item.amount)}</td>
                                <td>
                                    <span className={`status-badge ${item.status === "PAID" ? "approved" : "under_review"}`}>
                                        
                                        {item.status === "PAID" ? "Đã chuyển" : item.status === "CANCELED" ? "Đã hủy" : "Chưa chuyển"}
                                    </span>
                                </td>
                                <td><strong>{formatDateTime(item.paidAt)}</strong></td>
                                <td>{item.remiter || 'N/A'}</td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default MyAllowanceHistoryTable;
