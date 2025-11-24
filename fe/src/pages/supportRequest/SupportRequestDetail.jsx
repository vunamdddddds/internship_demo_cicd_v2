import React, { useState } from "react";
import { X, FileText } from "lucide-react";
import SupportRequestService from "~/services/SupportRequestService";

const DetailModal = ({ data, close, refresh }) => {
  const [reply, setReply] = useState("");

  const handleAction = async (status) => {
    await SupportRequestService.updateStatus(data.id, status, reply);
    refresh();
    close();
  };

  const isProcessed = data.status !== "PENDING";

  return (
    <div className="modal-overlay">
      <div className="modal">
        
        <div className="modal-header">
          <h3>Chi tiết yêu cầu hỗ trợ</h3>
          <button className="modal-close" onClick={close}>
            <X size={20} />
          </button>
        </div>

        <div className="detail-container">
          <div className="detail-row">
            <span className="label">Tên:</span>
            <span className="value">{data.internName}</span>
          </div>

          <div className="detail-row">
            <span className="label">Email:</span>
            <span className="value">{data.internEmail}</span>
          </div>

          <div className="detail-row">
            <span className="label">Tiêu đề:</span>
            <span className="value">{data.title}</span>
          </div>

          <div className="detail-row">
            <span className="label">Ngày gửi:</span>
            <span className="value">{data.createdAt ?? "-"}</span>
          </div>

          <div className="detail-row">
            <span className="label">Nội dung:</span>
            <span className="value">{data.description}</span>
          </div>

          <div className="detail-row">
            <span className="label">File đính kèm:</span>
            <span className="value">
              {data.evidenceFileUrl ? (
                <a
                  href={data.evidenceFileUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="link"
                >
                  <FileText size={18} /> Xem file
                </a>
              ) : (
                "-"
              )}
            </span>
          </div>

          {isProcessed && (
            <>
              <div className="detail-row">
                <span className="label">Xử lý bởi:</span>
                <span className="value">{data.handlerName ?? "-"}</span>
              </div>

              <div className="detail-row">
                <span className="label">Phản hồi HR:</span>
                <span className="value">{data.hrResponse ?? "-"}</span>
              </div>

              <div className="detail-row">
                <span className="label">Thời gian xử lý:</span>
                <span className="value">{data.resolvedAt ?? "-"}</span>
              </div>
            </>
          )}
        </div>

        {!isProcessed && (
          <>
            <label className="filter-label" style={{ marginTop: 16 }}>
              Phản hồi của HR
            </label>
            <textarea
              className="search-input"
              rows={4}
              placeholder="Nhập nội dung phản hồi..."
              value={reply}
              onChange={(e) => setReply(e.target.value)}
            />
          </>
        )}

        <div className="modal-actions">
          {!isProcessed ? (
            <>
              <button className="btn btn-approve" onClick={() => handleAction("APPROVED")}>
                Duyệt
              </button>

              <button className="btn btn-reject" onClick={() => handleAction("REJECTED")}>
                Từ chối
              </button>
            </>
          ) : null}

          <button className="btn btn-cancel" onClick={close}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default DetailModal;
