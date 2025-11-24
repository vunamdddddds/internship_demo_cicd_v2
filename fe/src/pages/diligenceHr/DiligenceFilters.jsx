import { useEffect, useState } from "react";
import Select from "react-select";
import InternshipProgramApi from "~/api/InternshipProgramApi";
import TeamApi from "~/api/TeamApi";

const DiligenceFilters = ({ filters = {}, onChange }) => {
  const [internshipPrograms, setInternshipPrograms] = useState([]);
  const [teams, setTeams] = useState([]);
  const [loadingTeams, setLoadingTeams] = useState(false);
  const [programSelected, setProgramSelected] = useState(null);

  const teamId = filters.teamId ?? null;

  // --- Lấy danh sách kỳ thực tập khi mount ---
  useEffect(() => {
    const fetchPrograms = async () => {
      try {
        const res = await InternshipProgramApi.getAll();
        const data = (res?.data || res).map((item) => ({
          value: item.id,
          label: item.name,
        }));

        // ✅ Thêm option "Tất cả"
        setInternshipPrograms([{ value: "", label: "Tất cả" }, ...data]);
      } catch (err) {
        console.error("Lỗi khi lấy kỳ thực tập:", err);
      }
    };
    fetchPrograms();
  }, []);

  // --- Khi chọn kỳ thực tập ---
  const handleProgramChange = async (selected) => {
    const programId = selected?.value || null;
    setProgramSelected(selected);
    onChange("teamId", null);
    setTeams([]);

    // Nếu chọn "Tất cả" hoặc bỏ chọn → không tải nhóm
    if (!programId) return;

    setLoadingTeams(true);
    try {
      const res = await TeamApi.getTeamsByIP(programId);
      const data = (res?.data || res).map((item) => ({
        value: item.id,
        label: item.name,
      }));

      // ✅ Nếu không có nhóm nào
      if (data.length === 0) {
        setTeams([]); // vẫn rỗng, nhưng sẽ xử lý placeholder bên dưới
      } else {
        // ✅ Thêm option "Tất cả nhóm"
        setTeams([{ value: "", label: "Tất cả" }, ...data]);
      }
    } catch (err) {
      console.error("Lỗi khi lấy nhóm:", err);
    } finally {
      setLoadingTeams(false);
    }
  };

  // --- Khi chọn nhóm ---
  const handleTeamChange = (selected) => {
    onChange("teamId", selected?.value || null);
  };

  const handleExport = async () => {
    console.log("Export Excel clicked");
  };

  // --- Tính placeholder nhóm ---
  let teamPlaceholder = "Chọn kỳ thực tập trước";
  if (loadingTeams) teamPlaceholder = "Đang tải nhóm...";
  else if (programSelected && !loadingTeams && teams.length === 0)
    teamPlaceholder = "Không có nhóm nào";
  else if (teams.length > 0) teamPlaceholder = "Chọn nhóm thực tập";

  return (
    <div className="filter-container">
      <div className="filter-grid">
        {/* --- Kỳ thực tập --- */}
        <div className="filter-item">
          <label className="filter-label">Kỳ thực tập</label>
          <Select
            options={internshipPrograms}
            onChange={handleProgramChange}
            value={programSelected}
            placeholder="Chọn kỳ thực tập"
            className="custom-select"
          />
        </div>

        {/* --- Nhóm thực tập --- */}
        <div className="filter-item">
          <label className="filter-label">Nhóm thực tập</label>
          <Select
            options={teams}
            isDisabled={
              loadingTeams ||
              (!loadingTeams &&
                (!programSelected || (programSelected && teams.length === 0)))
            }
            isLoading={loadingTeams}
            value={teams.find((t) => t.value === teamId) || null}
            onChange={handleTeamChange}
            placeholder={teamPlaceholder}
            className="custom-select"
          />
        </div>

        {/* --- Nút xuất báo cáo --- */}
        <button onClick={handleExport} className="btn btn-add">
          Xuất Excel
        </button>
      </div>
    </div>
  );
};

export default DiligenceFilters;