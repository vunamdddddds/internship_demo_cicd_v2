// src/components/evaluation/EvaluationReport.jsx
import React, { useState, useEffect } from "react";
import Select from "react-select";
import {
  PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
  BarChart, Bar, CartesianGrid, XAxis, YAxis,
} from "recharts";
import { useNavigate } from "react-router-dom";
import evaluationService from "~/services/EvaluationService";
import "~/components/layout/Layout.css";

const COLORS = ["#10b981", "#f59e0b", "#ef4444"];

const selectStyles = {
  control: (base) => ({
    ...base,
    minHeight: 40,
    borderRadius: 8,
    border: "1px solid #e2e8f0",
    boxShadow: "none",
    "&:hover": { borderColor: "#94a3b8" },
  }),
  placeholder: (base) => ({ ...base, color: "#94a3b8", fontSize: "0.9rem" }),
  menu: (base) => ({
    ...base,
    borderRadius: 8,
    marginTop: 4,
    boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
  }),
  option: (base, { isFocused, isSelected }) => ({
    ...base,
    backgroundColor: isSelected ? "#2563eb" : isFocused ? "#ebf8ff" : "white",
    color: isSelected ? "white" : "#1e293b",
    fontSize: "0.9rem",
  }),
};

const EvaluationReport = () => {
  // --- STATE ---
  const [reports, setReports] = useState([]);
  const [filters, setFilters] = useState({ internshipProgramId: null, universityId: null });
  const [termOptions, setTermOptions] = useState([]);
  const [uniOptions, setUniOptions] = useState([]);
  const [loadingFilters, setLoadingFilters] = useState(true);
  const [ setCurrentInternshipProgramId] = useState(null);

  const navigate = useNavigate();

  // --- EFFECTS ---

  // 1. Tải bộ lọc (Kỳ thực tập & Trường)
  useEffect(() => {
    const loadFilters = async () => {
      setLoadingFilters(true);
      try {
        const [terms, unis] = await Promise.all([
          evaluationService.getInternshipTerms(),
          evaluationService.getUniversities(),
        ]);
        setTermOptions(terms);
        setUniOptions(unis);
      } catch (err) {
        console.error("Lỗi tải bộ lọc", err);
      } finally {
        setLoadingFilters(false);
      }
    };
    loadFilters();
  }, []);

  // 2. Tải báo cáo khi filters thay đổi
  useEffect(() => {
    fetchReports();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters]);

  const fetchReports = async () => {
    const { reports: data } = await evaluationService.getReports(filters);
    setReports(data);

    // Cập nhật ID kỳ thực tập hiện tại để dùng cho link chi tiết
    if (filters.internshipProgramId) {
      setCurrentInternshipProgramId(filters.internshipProgramId);
    }
  };

  // --- HELPER FUNCTIONS & DERIVED DATA ---
  // (Phải đặt ngoài fetchReports để dùng được trong return)

  const chartData = [
    { name: "A (≥8.0)", value: reports.filter((r) => r.avgScore >= 8).length },
    { name: "B (6.5–7.9)", value: reports.filter((r) => r.avgScore >= 6.5 && r.avgScore < 8).length },
    { name: "C (<6.5)", value: reports.filter((r) => r.avgScore < 6.5).length },
  ];

  const getGradeClass = (score) => {
    if (score >= 8.0) return "approved";
    if (score >= 6.5) return "under_review";
    return "rejected";
  };

  const getGradeLabel = (score) => {
    if (score >= 8.0) return "A";
    if (score >= 6.5) return "B";
    return "C";
  };

  // --- RENDER ---
  return (
    <div className="evaluation-report">
      <h2 className="page-title">Báo cáo đánh giá thực tập</h2>

      {/* BỘ LỌC */}
      <div className="filter-container">
        <div className="filter-grid">
          <Select
            placeholder={loadingFilters ? "Đang tải kỳ" : "Chọn kỳ thực tập"}
            isClearable
            isLoading={loadingFilters}
            options={termOptions}
            onChange={(opt) => setFilters({ ...filters, internshipProgramId: opt?.value || null })}
            styles={selectStyles}
          />

          <Select
            placeholder={loadingFilters ? "Đang tải trường..." : "Chọn trường đại học"}
            isClearable
            isLoading={loadingFilters}
            options={uniOptions}
            onChange={(opt) => setFilters({ ...filters, universityId: opt?.value || null })}
            styles={selectStyles}
          />

          <div className="export-buttons">
            <button
              className="btn btn-approve"
              onClick={() => evaluationService.exportEvaluationReport("excel")}
            >
              Xuất Excel
            </button>
          </div>
        </div>
      </div>

      {/* BIỂU ĐỒ */}
      <div className="chart-section">
        <div className="chart-box">
          <h4 className="chart-title">Tỉ lệ xếp loại (A/B/C)</h4>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie data={chartData} dataKey="value" outerRadius={100} label>
                {chartData.map((_, i) => (
                  <Cell key={i} fill={COLORS[i]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-box">
          <h4 className="chart-title">Biểu đồ cột xếp loại</h4>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="value">
                {chartData.map((_, i) => (
                  <Cell key={`bar-${i}`} fill={COLORS[i]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* BẢNG DỮ LIỆU */}
      <div
        className="table-container"
        style={{
          maxHeight: "480px",
          overflowY: "auto",
          boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
          borderRadius: "12px",
        }}
      >
        <div className="table-wrapper">
          <table className="intern-table">
            <thead>
              <tr>
                <th>STT</th>
                <th>TÊN SV</th>
                <th>EMAIL</th>
                <th>CHUYÊN MÔN</th>
                <th>CHẤT LƯỢNG</th>
                <th>TƯ DUY</th>
                <th>HỌC HỎI</th>
                <th>KỸ NĂNG MỀM</th>
                <th>ĐIỂM TB</th>
                <th>NHẬN XÉT</th>
                <th>DỮ LIỆU CHUYÊN CẦN</th>
              </tr>
            </thead>
            <tbody>
              {reports.length === 0 ? (
                <tr>
                  <td colSpan="11" className="text-center">
                    Không có dữ liệu
                  </td>
                </tr>
              ) : (
                reports.map((r, i) => (
                  <tr key={r.internId}>
                    <td>{i + 1}</td>
                    <td>{r.fullName}</td>
                    <td>{r.email}</td>
                    <td>{r.expertiseScore}</td>
                    <td>{r.qualityScore}</td>
                    <td>{r.problemSolvingScore}</td>
                    <td>{r.technologyLearningScore}</td>
                    <td>{r.softSkill}</td>
                    <td>
                      <span className={`status-badge ${getGradeClass(r.avgScore)}`}>
                        {r.avgScore.toFixed(1)} ({getGradeLabel(r.avgScore)})
                      </span>
                    </td>
                    <td>{r.assessment}</td>
                    <td
                      onClick={() => {
                        // Kiểm tra an toàn
                        if (!r.internshipProgramId) {
                          console.error("Thiếu ID kỳ thực tập!");
                          return;
                        }
                        // Sửa đường dẫn navigate để khớp với Route ở Bước 1
                        navigate(`/evaluationReport/diligence/${r.internId}/${r.internshipProgramId}`, {
                          state: { internName: r.fullName } // (Tùy chọn) Truyền tên để hiển thị
                        });
                      }}
                      style={{ cursor: "pointer", color: "#2563eb" }}
                    >
                      {r.attendanceRate}%
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

export default EvaluationReport;