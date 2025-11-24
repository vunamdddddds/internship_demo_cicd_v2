import { X } from "lucide-react";

const Detail = ({ onClose, details }) => {
  return (
    <div className="modal-overlay">
      <div className="modal-detail">
        <div className="modal-header">
          <h3>
            Chi tiết ngày {new Date(details.date).toLocaleDateString("vi-VN")}
          </h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <div className="table-container">
          <table className="intern-table">
            <thead>
              <tr>
                <th>Họ và tên</th>
                <th>Email</th>
                <th>Check-in</th>
                <th>Check-out</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {!details.details || details.details.length === 0 ? (
                <tr>
                  <td colSpan="5" className="text-center">
                    Không có dữ liệu
                  </td>
                </tr>
              ) : (
                details.details.map((attendance) => (
                  <tr key={attendance.id}>
                    <td>{attendance.fullName}</td>
                    <td>{attendance.email}</td>
                    <td>{attendance.checkIn}</td>
                    <td>{attendance.checkOut}</td>
                    <td>
                      <span
                        className={`status-badge ${attendance.status.toLowerCase()}`}
                      >
                        {attendance.status}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Detail;
