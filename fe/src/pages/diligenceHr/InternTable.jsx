// pages/diligenceHr/components/InternTable.jsx
import { useNavigate } from "react-router-dom";
import { Eye } from "lucide-react";

const InternTable = ({ data }) => {
  const navigate = useNavigate();

  // ✅ Đảm bảo data luôn là mảng
  const rows = Array.isArray(data?.content) ? data.content : Array.isArray(data) ? data : [];

  // ✅ Hiển thị thông báo khi rỗng
  if (rows.length === 0) {
    return (
      <div className="bg-white p-5 rounded-lg shadow text-center text-gray-500">
        <h3 className="font-semibold text-sm mb-3">Danh sách thực tập sinh</h3>
        <p>Không có dữ liệu để hiển thị</p>
      </div>
    );
  }

  return (
    <div className="table-container">
      <div className="table-wrapper">
        <table className="intern-table">
          <thead>
            <tr>
              <th>Tên Intern</th>
              <th>Nhóm</th>
              <th>Số ngày làm</th>
              <th>Số ngày nghỉ</th>
              <th>Tỉ lệ</th>
              <th>Muộn/Sớm</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => {
              // ✅ Tính rate tại FE
              const present = row.presentDay ?? 0;
              const absent = row.absentDay ?? 0;
              const total = present + absent;
              const rate = total > 0 ? Math.round((present / total) * 100) : 0;

              return (
                <tr key={row.internId}>
                  <td>{row.internName}</td>
                  <td>{row.teamName}</td>
                  <td>{present}</td>
                  <td>{absent}</td>
                  <td>
                    <span
                      className={`status-badge ${
                        rate >= 90
                          ? "done"
                          : rate >= 80
                          ? "in-progress"
                          : "in-review"
                      }`}
                    >
                      {rate}%
                    </span>
                  </td>
                  <td>{row.lateAndLeaveDay ?? 0}</td>
                  <td>
                    <button
                      onClick={() =>
                        navigate(`/diligenceHr/detail/${row.internId}?internshipProgramId=${row.internshipProgramId}`)
                      }
                      className="icon-btn"
                    >
                      <Eye size={18} />
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default InternTable;