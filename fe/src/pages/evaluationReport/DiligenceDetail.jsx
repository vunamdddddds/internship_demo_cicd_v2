// src/components/evaluation/DiligenceDetail.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation} from "react-router-dom";
import { Calendar, CheckCircle2, AlertCircle, XCircle } from "lucide-react";
import evaluationService from "~/services/EvaluationService";
import "~/components/layout/Layout.css";

const DiligenceDetail = () => {
  const { internId, internshipProgramId } = useParams(); 
  const location = useLocation();
  const internNameFromState = location.state?.internName;
  const navigate = useNavigate();
  const [diligence, setDiligence] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      // 2. QUAN TRỌNG: Nếu URL bị lỗi "undefined" hoặc null, chặn ngay lập tức
      if (!internId || !internshipProgramId || internshipProgramId === "undefined" || internshipProgramId === "null") {
        console.error("Thiếu tham số internshipProgramId, không thể gọi API");
        return; 
      }

      setLoading(true);
      try {
        // 3. Gọi service với đủ 2 tham số
        const data = await evaluationService.getDiligenceDetail(internId, internshipProgramId);
        
        // Nếu có tên từ state thì ưu tiên dùng, nếu không thì dùng từ API
        if (internNameFromState) {
            data.fullName = internNameFromState;
        }
        setDiligence(data);
      } catch (err) {
        console.error("Lỗi tải dữ liệu:", err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [internId, internshipProgramId, internNameFromState]);

  if (loading) {
    return (
      <div className="evaluation-report">
        <h2 className="page-title">Chi tiết chuyên cần</h2>
        <p className="text-center">Đang tải dữ liệu...</p>
      </div>
    );
  }

  if (!diligence) return null;

  const percentWorked = ((diligence.workedDays / diligence.totalDays) * 100).toFixed(1);
  const percentLeave = ((diligence.leaveDays / diligence.totalDays) * 100).toFixed(1);
  const percentAbsent = ((diligence.absentDays / diligence.totalDays) * 100).toFixed(1);

  return (
    <div className="evaluation-report">
      <h2 className="page-title">Chi tiết chuyên cần của {diligence.fullName}</h2>

      <div className="detail-container">
        <div
        className="detail-container"
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: "16px",
          padding: "20px",
          marginTop: "16px",
        }}
      >
        {/* Ô 1: Tổng ngày */}
        <div style={{ flex: "1", minWidth: "200px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "4px" }}>
            <Calendar size={16} style={{ color: "#6b7280" }} />
            <span className="label">Tổng ngày làm việc</span>
          </div>
          <div className="value" style={{ fontSize: "1.25rem", fontWeight: "600" }}>
            {diligence.totalDays} ngày
          </div>
        </div>

        {/* Ô 2: Đi làm */}
        <div style={{ flex: "1", minWidth: "200px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "4px" }}>
            <CheckCircle2 size={16} style={{ color: "#16a34a" }} />
            <span className="label">Đi làm</span>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <span className="value" style={{ fontWeight: "600" }}>
              {diligence.totalWorkingDays} ngày
            </span>
            <span className="status-badge approved">
              {percentWorked}%
            </span>
          </div>
        </div>

        {/* Ô 3: Nghỉ phép */}
        <div style={{ flex: "1", minWidth: "200px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "4px" }}>
            <AlertCircle size={16} style={{ color: "#ca8a04" }} />
            <span className="label">Nghỉ phép</span>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <span className="value" style={{ fontWeight: "600" }}>
              {diligence.leaveDays} ngày
            </span>
            <span className="status-badge under_review">
              {percentLeave}%
            </span>
          </div>
        </div>

        {/* Ô 4: Nghỉ không phép */}
        <div style={{ flex: "1", minWidth: "200px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "4px" }}>
            <XCircle size={16} style={{ color: "#dc2626" }} />
            <span className="label">Nghỉ không phép</span>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <span className="value" style={{ fontWeight: "600" }}>
              {diligence.absentDays} ngày
            </span>
            <span className="status-badge rejected">
              {percentAbsent}%
            </span>
          </div>
        </div>
      </div>
      </div>

      <div className="table-container" style={{ marginTop: "20px" }}>
        <div className="table-wrapper">
          <table className="intern-table">
            <thead>
              <tr>
                <th>Ngày</th>
                <th>Trạng thái</th>
                <th>Lý do</th>
              </tr>
            </thead>
            <tbody>
              {diligence.details.map((d, i) => (
                <tr key={i}>
                  <td>{d.date}</td>
                  <td
                    style={{
                      color:
                        d.status === "Đi làm"
                          ? "green"
                          : d.status === "Nghỉ phép"
                          ? "#f59e0b"
                          : "red",
                      fontWeight: 600,
                    }}
                  >
                    {d.status}
                  </td>
                  <td>{d.reason || "—"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div style={{ textAlign: "right", marginTop: "20px" }}>
        <button
          className="btn btn-reject"
          onClick={() => navigate("/evaluationReport")}
        >
          ← Quay lại
        </button>
      </div>
    </div>
  );
};

export default DiligenceDetail;