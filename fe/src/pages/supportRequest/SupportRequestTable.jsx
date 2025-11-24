import React from "react";
import { Eye, FileText } from "lucide-react";

const SupportRequestTable = ({ requests, loading, onViewDetail, pagination }) => {
  const getStatusLabel = (status) => {
    switch (status) {
      case "PENDING": return "Chờ xử lý";
      case "APPROVED": case "RESOLVED": return "Đã duyệt";
      case "REJECTED": return "Đã từ chối";
      case "IN_PROGRESS": return "Đang xử lý";
      default: return status;
    }
  };

  const getStatusClass = (status) => {
    if (status === "PENDING") return "under_review";
    if (status === "APPROVED" || status === "RESOLVED") return "approved";
    if (status === "REJECTED") return "rejected";
    if (status === "IN_PROGRESS") return "under_review";
    return "";
  };

  const getIndex = (index) => {
    return (pagination.pageNumber - 1) * 10 + index + 1;
  };

  return (
    <div className="table-container">
      <div className="table-wrapper">
        <table className="intern-table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Tên</th>
              <th>Email</th>
              <th>Tiêu đề</th>
              <th>Nội dung</th>
              <th>Ngày gửi</th>
              <th>File</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr><td colSpan={9} className="text-center">Đang tải dữ liệu...</td></tr>
            ) : requests.length === 0 ? (
              <tr><td colSpan={9} className="text-center">Không có yêu cầu hỗ trợ</td></tr>
            ) : (
              requests.map((item, i) => (
                <tr key={item.id}>
                  <td>{getIndex(i)}</td>
                  <td>{item.internName}</td>
                  <td>{item.internEmail}</td>
                  <td>{item.title}</td>
                  <td>
                    <div className="truncate-text" title={item.description}>
                      {item.description.length > 50 ? item.description.substring(0, 50) + "..." : item.description}
                    </div>
                  </td>
                  <td>{item.createdAt}</td>
                  <td>
                    {item.evidenceFileUrl ? (
                      <a href={item.evidenceFileUrl} target="_blank" className="link" rel="noreferrer">
                        <FileText size={18} />
                      </a>
                    ) : "-"}
                  </td>
                  <td>
                    <span className={`status-badge ${getStatusClass(item.status)}`}>
                      {getStatusLabel(item.status)}
                    </span>
                  </td>
                  <td>
                    <button className="icon-btn" onClick={() => onViewDetail(item)}>
                      <Eye size={18} />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default SupportRequestTable;