import React, { useState } from "react";
import { Pencil, XCircle, Send, CheckCircle2 } from "lucide-react";
import EditInternshipProgramModal from "./EditInternshipProgramForm";

const InternshipProgramTable = ({
  internshipPrograms,
  loading,
  convertToISO,
}) => {
  const [internshipProgram, setInternshipProgram] = useState(null);
  const [showEditForm, setShowEditForm] = useState(false);

  const formatCurrency = (amount) => {
    if (amount === null || amount === undefined) return "-";
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  return (
    <>
      <div className="table-container">
        <table className="intern-table">
          <thead>
            <tr>
              <th>Kỳ thực tập</th>
              <th>Phòng ban</th>
              <th>Hạn nộp hồ sơ</th>
              <th>Hạn duyệt hồ sơ</th>
              <th>Bắt đầu</th>
              <th>Kết thúc</th>
              <th>Trạng thái</th>
              <th>Phụ cấp</th>  
              <th className="action-col"></th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="9" className="text-center">Đang tải...</td>
              </tr>
            ) : internshipPrograms.length === 0 ? (
              <tr>
                <td colSpan="9" className="text-center">Không có dữ liệu</td>
              </tr>
            ) : (
              internshipPrograms.map((ip) => (
                <tr key={ip.id}>
                  <td>{ip.name}</td>
                  <td>{ip.department}</td>
                  <td>{ip.endPublishedTime}</td>
                  <td>{ip.endReviewingTime}</td>
                  <td>{ip.timeStart}</td>
                  <td>{ip.timeEnd || "-"}</td>
                  <td>{ip.status}</td>
                  <td>{formatCurrency(ip.allowance)}</td>   {/* ← hiển thị */}

                  <td className="action-col">
                    <button
                      className="icon-btn"
                      onClick={() => {
                        setInternshipProgram(ip);
                        setShowEditForm(true);
                      }}
                    >
                      <Pencil size={15} />
                    </button>

                    {ip.status === "DRAFT" && (
                      <button className="icon-btn">
                        <Send size={15} />
                      </button>
                    )}

                    {ip.status === "ONGOING" ? (
                      <button className="icon-btn">
                        <CheckCircle2 size={15} />
                      </button>
                    ) : (
                      <button className="icon-btn">
                        <XCircle size={15} />
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showEditForm && internshipProgram && (
        <EditInternshipProgramModal
          onClose={() => setShowEditForm(false)}
          internshipProgram={internshipProgram}
          convertToISO={convertToISO}
        />
      )}
    </>
  );
};

export default InternshipProgramTable;