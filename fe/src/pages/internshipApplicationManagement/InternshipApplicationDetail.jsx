import { X } from "lucide-react";

const InternshipApplicationDetail = ({ intern, onClose }) => {
  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Chi tiết đơn thực tập</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <div className="detail-container">
          <div className="detail-row">
            <span className="label">Họ và tên:</span>
            <span className="value">{intern.fullName}</span>
          </div>
          <div className="detail-row">
            <span className="label">Email:</span>
            <span className="value">{intern.email}</span>
          </div>
          <div className="detail-row">
            <span className="label">Số điện thoại:</span>
            <span className="value">{intern.phone || "--"}</span>
          </div>
          <div className="detail-row">
            <span className="label">Ngành học:</span>
            <span className="value">{intern.majorName}</span>
          </div>
          <div className="detail-row">
            <span className="label">Trường học:</span>
            <span className="value">{intern.universityName}</span>
          </div>
          <div className="detail-row">
            <span className="label">Kì thực tập:</span>
            <span className="value">{intern.internshipProgram}</span>
          </div>
          <div className="detail-row">
            <span className="label">Địa chỉ:</span>
            <span className="value">{intern.address || "--"}</span>
          </div>
          <div className="detail-row">
            <span className="label">CV:</span>
            {intern.cvUrl ? (
              <a
                href={intern.cvUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="link"
              >
                Xem CV
              </a>
            ) : (
              <span className="value">--</span>
            )}
          </div>

          <div className="detail-row">
            <span className="label">Đơn xin thực tập:</span>
            {intern.internshipApplicationtUrl ? (
              <a
                href={intern.internshipApplicationtUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="link"
              >
                Xem đơn xin thực tập
              </a>
            ) : (
              <span className="value">--</span>
            )}
          </div>

          <div className="detail-row">
            <span className="label">Hợp đồng thực tập:</span>
            {intern.internshipContractUrl ? (
              <a
                href={intern.internshipContractUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="link"
              >
                Xem hợp đồng
              </a>
            ) : (
              <span className="value">--</span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default InternshipApplicationDetail;
