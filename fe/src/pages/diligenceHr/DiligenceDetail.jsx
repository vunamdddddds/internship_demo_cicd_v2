import { useEffect, useState } from "react";
import { useParams, useSearchParams, useNavigate } from "react-router-dom";
import ReportApi from "~/api/ReportApi";
import { ArrowLeft } from "lucide-react";

const DiligenceDetail = () => {
  const { internId } = useParams();
  const [searchParams] = useSearchParams();
  const internshipProgramId = searchParams.get("internshipProgramId");
  const navigate = useNavigate();

  const [data, setData] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        const response = await ReportApi.getInternAttendanceDetail(
          internId,
          internshipProgramId
        );
        console.log("DiligenceDetail response:", response);
        setData(response?.data || response);
      } catch (err) {
        console.error("Load attendance detail failed:", err);
      }
    };
    load();
  }, [internId, internshipProgramId]);

  if (!data) return <div>Đang tải...</div>;

  return (
    <div className="p-6">
      {/* Nút quay lại */}
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 mb-4 text-blue-600"
      >
        <ArrowLeft size={20} /> Quay lại
      </button>

      <div className="bg-white p-6 rounded-lg shadow">
        <h2 className="text-xl font-bold mb-2">Chi tiết chuyên cần</h2>
        <p className="text-gray-600 mb-6">
          Intern: <b>{data.fullName}</b> – Team: <b>{data.teamName}</b>
        </p>

        {/* Bảng lịch sử check-in/out */}
        <h3 className="font-semibold mb-3">Lịch sử check-in/out</h3>
        <div className="table-container mb-8 overflow-x-auto">
          <table className="intern-table min-w-full border">
            <thead>
              <tr>
                <th className="px-4 py-2 border">Ngày</th>
                <th className="px-4 py-2 border">Check-in</th>
                <th className="px-4 py-2 border">Check-out</th>
                <th className="px-4 py-2 border">Trạng thái</th>
                <th className="px-4 py-2 border">Giờ dự kiến</th>
              </tr>
            </thead>
            <tbody>
              {Array.isArray(data?.dailyLogs) && data.dailyLogs.length > 0 ? (
                data.dailyLogs.map((r, i) => (
                  <tr key={i} className="text-center border-t">
                    <td className="px-4 py-2">{r.date}</td>
                    <td className="px-4 py-2">{r.actualCheckIn || "-"}</td>
                    <td className="px-4 py-2">{r.actualCheckOut || "-"}</td>
                    <td className="px-4 py-2">
                      <span
                        className={`status-badge ${
                          r.status === "PRESENT"
                            ? "done"
                            : r.status === "ABSENT"
                            ? "rejected"
                            : "in-review"
                        }`}
                      >
                        {r.status === "PRESENT"
                          ? "Có mặt"
                          : r.status === "ABSENT"
                          ? "Vắng"
                          : "Khác"}
                      </span>
                    </td>
                    <td className="px-4 py-2">
                      {r.expectedTimeStart} - {r.expectedTimeEnd}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="text-center py-3 text-gray-500">
                    Không có dữ liệu check-in/out
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Bảng lịch sử duyệt nghỉ phép */}
        <h3 className="font-semibold mb-3">Lịch sử duyệt nghỉ phép</h3>
        <div className="table-container overflow-x-auto">
          <table className="intern-table min-w-full border">
            <thead>
              <tr>
                <th className="px-4 py-2 border">Ngày</th>
                <th className="px-4 py-2 border">Loại</th>
                <th className="px-4 py-2 border">Lý do</th>
                <th className="px-4 py-2 border">Trạng thái</th>
                <th className="px-4 py-2 border">Người duyệt</th>
              </tr>
            </thead>
            <tbody>
              {Array.isArray(data?.leaveLogs) && data.leaveLogs.length > 0 ? (
                data.leaveLogs.map((h, i) => (
                  <tr key={i} className="text-center border-t">
                    <td className="px-4 py-2">{h.date}</td>
                    <td className="px-4 py-2">{h.type}</td>
                    <td className="px-4 py-2">{h.reason}</td>
                    <td className="px-4 py-2">
                      <span
                        className={`status-badge ${
                          h.status === "APPROVED" ? "approved" : "rejected"
                        }`}
                      >
                        {h.status === "APPROVED" ? "Duyệt" : "Từ chối"}
                      </span>
                    </td>
                    <td className="px-4 py-2">{h.by}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="text-center py-3 text-gray-500">
                    Không có lịch sử nghỉ phép
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default DiligenceDetail;
