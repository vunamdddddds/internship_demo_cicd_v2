import React, { useState, useEffect, useCallback } from "react";
import Select from "react-select";
import { Pie, Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  ArcElement,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
} from "chart.js";
import * as XLSX from "xlsx";
import {
  fetchTeams,
  fetchEvaluation,
  submitEvaluation,
} from "~/services/MentorEvaluationService";
import EvaluationFormModal from "./EvaluationFormModal";
import ViewEvaluationModal from "./ViewEvaluationModal";
import { toast } from "react-toastify";

ChartJS.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend);

const MentorEvaluation = () => {
  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [internsWithStatus, setInternsWithStatus] = useState([]);
  const [selectedIntern, setSelectedIntern] = useState(null);
  const [viewEval, setViewEval] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [form, setForm] = useState({
    technical: { knowledge: 5, quality: 5, problemSolving: 5, learning: 5 },
    softSkills: "Khá",
    assessment: "",
  });

  // Cập nhật trạng thái đánh giá và tính điểm trung bình
  const updateInternsStatus = useCallback(async (internsList) => {
    if (!internsList || internsList.length === 0) {
      setInternsWithStatus([]);
      return;
    }

    const updatedInterns = await Promise.all(
      internsList.map(async (intern) => {
        let evaluationData = {};
        let evaluated = false;
        let averageScore = "N/A";

        try {
          evaluationData = await fetchEvaluation(intern.id);
          if (
            evaluationData &&
            evaluationData.expertiseScore !== undefined &&
            evaluationData.expertiseScore !== null
          ) {
            evaluated = true;

            const scores = [
              evaluationData.expertiseScore,
              evaluationData.qualityScore,
              evaluationData.problemSolvingScore,
              evaluationData.technologyLearningScore,
            ].filter(s => s != null).map(parseFloat);

            if (scores.length > 0) {
              const sum = scores.reduce((a, b) => a + b, 0);
              averageScore = (sum / scores.length).toFixed(1);
            }
          }
        } catch (e) {
          evaluated = false;
          console.error("Fetch evaluation error:", e);
        }

        return {
          ...intern,
          evaluated,
          expertiseScore: evaluationData.expertiseScore,
          qualityScore: evaluationData.qualityScore,
          problemSolvingScore: evaluationData.problemSolvingScore,
          technologyLearningScore: evaluationData.technologyLearningScore,
          softSkill: evaluationData.softSkill,
          assessment: evaluationData.assessment,
          averageScore: evaluated ? averageScore : "N/A",
        };
      })
    );

    setInternsWithStatus(updatedInterns);
  }, []);

  // Load teams
  const loadTeams = useCallback(async () => {
    try {
      const teamList = await fetchTeams();
      const formattedTeams = teamList.map((team) => {
        const rawInterns = team.members || team.interns || [];
        const interns = rawInterns.map((intern) => ({
          id: intern.id,
          name: intern.fullName || "Unknown",
          position: intern.major || "N/A",
          email: intern.email || "N/A",
          evaluated: false,
        }));

        return {
          id: team.id,
          name: team.teamName || team.name || "Unnamed Team",
          interns,
        };
      });

      setTeams(formattedTeams);

      const teamToSelect =
        formattedTeams.find((t) => t.id === selectedTeam?.id) ||
        formattedTeams[0] ||
        null;

      if (teamToSelect?.id !== selectedTeam?.id) {
        setSelectedTeam(teamToSelect);
      } else if (!selectedTeam && teamToSelect) {
        setSelectedTeam(teamToSelect);
      }
    } catch (error) {
      console.error("Load teams error:", error);
      setTeams([]);
      setSelectedTeam(null);
    }
  }, [selectedTeam]);

  useEffect(() => {
    loadTeams();
  }, [loadTeams]);

  useEffect(() => {
    if (selectedTeam) {
      updateInternsStatus(selectedTeam.interns);
    } else {
      setInternsWithStatus([]);
    }
  }, [selectedTeam, updateInternsStatus]);

  // Mở form đánh giá
  const openForm = async (intern, existingEval = null) => {
    setSelectedIntern(intern);
    let dataToUse = existingEval;

    if (!dataToUse && intern.evaluated) {
      try {
        dataToUse = await fetchEvaluation(intern.id);
      } catch {
        toast.info("Chưa có đánh giá cho thực tập sinh này.");
      }
    }

    if (dataToUse) {
      setForm({
        technical: {
          knowledge: parseFloat(dataToUse.expertiseScore) || 5,
          quality: parseFloat(dataToUse.qualityScore) || 5,
          problemSolving: parseFloat(dataToUse.problemSolvingScore) || 5,
          learning: parseFloat(dataToUse.technologyLearningScore) || 5,
        },
        softSkills:
          dataToUse.softSkill === "GOOD"
            ? "Tốt"
            : dataToUse.softSkill === "FAIR"
            ? "Khá"
            : dataToUse.softSkill === "AVERAGE"
            ? "Trung bình"
            : "Kém",
        assessment: dataToUse.assessment || "",
      });
    } else {
      setForm({
        technical: { knowledge: 5, quality: 5, problemSolving: 5, learning: 5 },
        softSkills: "Khá",
        assessment: "",
      });
    }
  };

  // Xem đánh giá
  const openView = async (intern) => {
    try {
      const dataToView = await fetchEvaluation(intern.id);

      if (
        dataToView &&
        dataToView.expertiseScore !== undefined &&
        dataToView.expertiseScore !== null
      ) {
        const scores = [
          parseFloat(dataToView.expertiseScore) || 0,
          parseFloat(dataToView.qualityScore) || 0,
          parseFloat(dataToView.problemSolvingScore) || 0,
          parseFloat(dataToView.technologyLearningScore) || 0,
        ];
        const avg = scores.reduce((a, b) => a + b, 0) / 4;

        setViewEval({ ...dataToView, intern, averageScore: avg.toFixed(1) });
      } else {
        toast.info("Chưa có đánh giá cho thực tập sinh này.");
      }
          } catch {
            toast.info("Chưa có đánh giá cho thực tập sinh này.");
          }  };

  const handleEdit = () => {
    openForm(viewEval.intern, viewEval);
    setViewEval(null);
  };

  // Gửi đánh giá
  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      expertiseScore: parseFloat(form.technical.knowledge),
      qualityScore: parseFloat(form.technical.quality),
      problemSolvingScore: parseFloat(form.technical.problemSolving),
      technologyLearningScore: parseFloat(form.technical.learning),
      softSkill:
        form.softSkills === "Tốt"
          ? "GOOD"
          : form.softSkills === "Khá"
          ? "FAIR"
          : form.softSkills === "Trung bình"
          ? "AVERAGE"
          : "POOR",
      assessment: form.assessment,
    };

    try {
      await submitEvaluation(selectedIntern.id, payload);
      setSelectedIntern(null);
      toast.success("Đánh giá đã được lưu!");
      await updateInternsStatus(selectedTeam.interns);
    } catch (err) {
      console.error("Submit evaluation error:", err);
    }
  };

  // Xuất Excel
  const exportToExcel = () => {
    const data = internsWithStatus.map((i) => ({
      "Tên": i.name,
      "Email": i.email ?? "N/A",
      "Điểm trung bình": i.averageScore ?? "N/A",
      "Trạng thái": i.evaluated ? "Đã đánh giá" : "Chưa đánh giá",
      "Hiểu biết chuyên môn": i.expertiseScore ?? "N/A",
      "Chất lượng công việc": i.qualityScore ?? "N/A",
      "Tư duy giải quyết vấn đề": i.problemSolvingScore ?? "N/A",
      "Khả năng học hỏi": i.technologyLearningScore ?? "N/A",
      "Thái độ & Kỹ năng mềm":
        i.softSkill === "GOOD"
          ? "Tốt"
          : i.softSkill === "FAIR"
          ? "Khá"
          : i.softSkill === "AVERAGE"
          ? "Trung bình"
          : i.softSkill === "POOR"
          ? "Kém"
          : "N/A",
      "Nhận xét": i.assessment || "N/A",
    }));

    const worksheet = XLSX.utils.json_to_sheet(data);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Đánh giá TTS");
    XLSX.writeFile(workbook, `Danh_gia_${selectedTeam?.name || "Nhom"}.xlsx`);
  };

  // Dữ liệu biểu đồ
  const interns = internsWithStatus;
  const filteredInterns = interns.filter((i) =>
    (i.name || "").toLowerCase().includes(searchTerm.toLowerCase())
  );
  const currentInterns = filteredInterns;
  const teamOptions = teams.map((t) => ({ value: t, label: t.name }));

  const evaluatedCount = interns.filter((i) => i.evaluated).length;
  const unevaluatedCount = interns.length - evaluatedCount;

  // Pie Chart: Thái độ & Kỹ năng mềm
  const softSkillsCount = { GOOD: 0, FAIR: 0, AVERAGE: 0, POOR: 0 };
  interns.forEach((i) => {
    if (i.evaluated && i.softSkill) softSkillsCount[i.softSkill]++;
  });

  const pieData = {
    labels: ["Tốt", "Khá", "Trung bình", "Kém"],
    datasets: [
      {
        data: [
          softSkillsCount.GOOD,
          softSkillsCount.FAIR,
          softSkillsCount.AVERAGE,
          softSkillsCount.POOR,
        ],
        backgroundColor: ["#10b981", "#3b82f6", "#f59e0b", "#ef4444"],
      },
    ],
  };

  // Bar Chart: Phân bố điểm trung bình
  const scoreBins = { "1-3": 0, "4-6": 0, "7-8": 0, "9-10": 0 };
  interns.forEach((i) => {
    if (i.evaluated) {
      const scores = [
        i.expertiseScore,
        i.qualityScore,
        i.problemSolvingScore,
        i.technologyLearningScore,
      ].filter((s) => s != null);
      const avg = scores.length > 0 ? scores.reduce((a, b) => a + b, 0) / scores.length : 0;

      if (avg >= 1 && avg <= 3) scoreBins["1-3"]++;
      else if (avg >= 4 && avg <= 6) scoreBins["4-6"]++;
      else if (avg >= 7 && avg <= 8) scoreBins["7-8"]++;
      else if (avg >= 9 && avg <= 10) scoreBins["9-10"]++;
    }
  });

  const barData = {
    labels: ["1-3", "4-6", "7-8", "9-10"],
    datasets: [
      {
        label: "Số lượng sinh viên",
        data: [
          scoreBins["1-3"],
          scoreBins["4-6"],
          scoreBins["7-8"],
          scoreBins["9-10"],
        ],
        backgroundColor: "#3b82f6",
      },
    ],
  };

  return (
    <div className="evaluation-report">
      <h1 className="page-title">Đánh Giá Thực Tập Sinh</h1>

      {selectedTeam && (
        <>
          {/* Thống kê nhanh */}
          <div className="filter-container" style={{ padding: "16px", marginBottom: "16px" }}>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "16px" }}>
              <div className="stat-card">
                <h3 style={{ margin: "0 0 8px 0", fontSize: "14px", color: "#6b7280" }}>Tổng TTS</h3>
                <p style={{ margin: 0, fontSize: "1.5rem", fontWeight: 600, color: "#1f2937" }}>{interns.length}</p>
              </div>
              <div className="stat-card">
                <h3 style={{ margin: "0 0 8px 0", fontSize: "14px", color: "#6b7280" }}>Đã đánh giá</h3>
                <p style={{ margin: 0, fontSize: "1.5rem", fontWeight: 600, color: "#16a34a" }}>{evaluatedCount}</p>
              </div>
              <div className="stat-card">
                <h3 style={{ margin: "0 0 8px 0", fontSize: "14px", color: "#6b7280" }}>Chưa đánh giá</h3>
                <p style={{ margin: 0, fontSize: "1.5rem", fontWeight: 600, color: "#dc2626" }}>{unevaluatedCount}</p>
              </div>
            </div>
          </div>

          {/* Biểu đồ */}
          <div className="chart-section">
            <div className="chart-box">
              <h3 className="chart-title">Trạng thái Thái độ & Kỹ năng mềm</h3>
              <div style={{ height: "280px" }}>
                <Pie
                  data={pieData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: { position: "bottom", labels: { padding: 20, usePointStyle: true, font: { size: 12 } } },
                    },
                  }}
                />
              </div>
            </div>

            <div className="chart-box">
              <h3 className="chart-title">Phân bố điểm trung bình</h3>
              <div style={{ height: "280px" }}>
                <Bar
                  data={barData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: { position: "bottom", labels: { padding: 20 } },
                    },
                    scales: {
                      y: { beginAtZero: true, ticks: { stepSize: 1 }, title: { display: true, text: "Số lượng sinh viên" } },
                      x: { title: { display: true, text: "Điểm trung bình" } },
                    },
                  }}
                />
              </div>
            </div>
          </div>

          {/* Bộ lọc + Tìm kiếm + Xuất Excel */}
          <div className="filter-container" style={{ marginTop: "20px" }}>
            <div className="filter-grid" style={{ alignItems: "center", gap: "0" }}>
              {/* Chọn nhóm */}
              <div className="filter-item" style={{ minWidth: "180px", borderRight: "1px solid #e5e7eb" }}>
                <label className="filter-label">Chọn nhóm</label>
                <Select
                  options={teamOptions}
                  value={teamOptions.find((o) => o.value.id === selectedTeam?.id) || null}
                  onChange={(opt) => setSelectedTeam(opt?.value || null)}
                  placeholder="Chọn nhóm"
                  isClearable={false}
                  className="custom-select"
                />
              </div>

              {/* Tìm kiếm */}
              <div className="filter-item" style={{ flex: 1, minWidth: "200px" }}>
                <label className="filter-label">Tìm kiếm</label>
                <div className="search-input-wrapper">
                  <input
                    type="text"
                    placeholder="Tìm kiếm thực tập sinh..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="search-input"
                    style={{ paddingLeft: "40px" }}
                  />
                  <svg className="search-icon" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
                  </svg>
                </div>
              </div>

              {/* Xuất Excel */}
              <div className="filter-item" style={{ display: "flex", justifyContent: "flex-end" }}>
                <button onClick={exportToExcel} className="btn btn-add" style={{ marginTop: "24px" }}>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M8.5 6.5a.5.5 0 0 0-1 0v3.793L6.354 9.146a.5.5 0 1 0-.708.708l2 2a.5.5 0 0 0 .708 0l2-2a.5.5 0 0 0-.708-.708L8.5 10.293V6.5z" />
                    <path d="M14 14V4.5L9.5 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2zM9.5 3A1.5 1.5 0 0 0 11 4.5h2V14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h5.5v2z" />
                  </svg>
                  Xuất Excel
                </button>
              </div>
            </div>
          </div>

          {/* Bảng */}
          <div className="table-container" style={{ marginTop: "20px" }}>
            <div className="table-wrapper">
              <table className="intern-table">
                <thead>
                  <tr>
                    <th>TÊN</th>
                    <th>EMAIL</th>
                    <th>ĐIỂM TRUNG BÌNH</th>
                    <th>TRẠNG THÁI</th>
                    <th>HÀNH ĐỘNG</th>
                  </tr>
                </thead>
                <tbody>
                  {currentInterns.map((i) => (
                    <tr key={i.id}>
                      <td>{i.name}</td>
                      <td>{i.email}</td>
                      <td>
                        <span style={{ fontWeight: 600, color: i.averageScore !== "N/A" ? '#3b82f6' : '#6b7280' }}>
                          {i.averageScore}
                        </span>
                      </td>
                      <td>
                        <span className={`status-badge ${i.evaluated ? "done" : "to-do"}`}>
                          {i.evaluated ? "Đã đánh giá" : "Chưa đánh giá"}
                        </span>
                      </td>
                      <td>
                        {i.evaluated ? (
                          <button
                            className="btn"
                            style={{
                              background: "#17a2b8",
                              color: "white",
                              padding: "6px 12px",
                              fontSize: "13px",
                              borderRadius: "6px",
                            }}
                            onClick={() => openView(i)}
                          >
                            Xem / Sửa
                          </button>
                        ) : (
                          <button className="btn btn-search" onClick={() => openForm(i)}>
                            Đánh Giá
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}

      {/* Modal */}
      {selectedIntern && (
        <EvaluationFormModal
          intern={selectedIntern}
          form={form}
          setForm={setForm}
          onSubmit={handleSubmit}
          onClose={() => setSelectedIntern(null)}
        />
      )}

      {viewEval && (
        <ViewEvaluationModal
          evaluation={viewEval}
          onClose={() => setViewEval(null)}
          onEdit={handleEdit}
        />
      )}
    </div>
  );
};

export default MentorEvaluation;