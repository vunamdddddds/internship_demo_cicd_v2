import { X } from "lucide-react";

const Detail = ({ details, onClose }) => {
  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>
            Chi tiết ngày {new Date(details.date).toLocaleDateString("vi-VN")}
          </h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <div className="detail-container">
          <div className="detail-row">
            <span className="label">Giờ vào:</span>
            <span className="value">{details.timeStart || "--"}</span>
          </div>
          <div className="detail-row">
            <span className="label">Giờ ra:</span>
            <span className="value">{details.timeEnd || "--"}</span>
          </div>
          <div className="detail-row">
            <span className="label">Trạng thái:</span>
            <span className="value">{details.status || "--"}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Detail;