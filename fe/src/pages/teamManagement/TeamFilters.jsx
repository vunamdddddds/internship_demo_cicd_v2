import { Search, Plus } from "lucide-react";
import Select from "react-select";
import React, { useState } from "react";
import AddTeamModal from "./AddTeamForm";

const MentorFilter = ({
  filters,
  handleFilterChange,
  handleSearch,
  internshipPrograms,
  mentors,
  onAddTeam,
}) => {
  const [showForm, setShowForm] = useState(false);

  const internshipProgramOptions = [
    { value: 0, label: "Tất cả" },
    ...internshipPrograms.map((d) => ({
      value: d.id,
      label: d.name,
    })),
  ];

  const mentorOptions = [
    { value: 0, fullName: "Tất cả", email: "", departmentName: "" },
    ...mentors.map((m) => ({
      value: m.id,
      fullName: m.fullName,
      email: m.email,
      departmentName: m.departmentName,
    })),
  ];

  return (
    <>
      {/* FILTERS */}
      <div className="filter-container">
        <div className="filter-grid">
          <div className="filter-item">
            <label className="filter-label">Tên nhóm</label>
            <div className="search-input-wrapper">
              <Search className="search-icon" size={20} />
              <input
                type="text"
                placeholder="Nhập tên nhóm"
                className="search-input"
                value={filters.keyWord}
                onChange={(e) => handleFilterChange("keyWord", e.target.value)}
                onKeyPress={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>
          </div>

          <div className="filter-item">
            <label className="filter-label">Kì thực tập</label>
            <Select
              className="custom-select"
              options={internshipProgramOptions}
              value={internshipProgramOptions.find(
                (opt) => opt.value === filters.internshipProgram
              )}
              onChange={(selected) =>
                handleFilterChange("internshipProgram", selected.value)
              }
            />
          </div>

          <div className="filter-item">
            <label className="filter-label">Mentor</label>
            <Select
              className="custom-select"
              options={mentorOptions}
              value={mentorOptions.find((opt) => opt.value === filters.mentor)}
              onChange={(selected) =>
                handleFilterChange("mentor", selected.value)
              }
              // 👇 Chỉ hiển thị email trong dropdown menu
              formatOptionLabel={(m, { context }) =>
                context === "menu" ? (
                  <div>
                    <div>
                      {m.fullName}{" "}
                      {m.departmentName ? `- ${m.departmentName}` : ""}
                    </div>
                    <div
                      style={{
                        fontSize: "10px",
                        marginTop: "1px",
                      }}
                    >
                      {m.email}
                    </div>
                  </div>
                ) : (
                  <div>
                    {m.fullName}{" "}
                    {m.departmentName ? ` - ${m.departmentName}` : ""}
                  </div>
                )
              }
            />
          </div>

          <button onClick={handleSearch} className="btn btn-search">
            <Search size={18} /> Tìm kiếm
          </button>

          <button onClick={() => setShowForm(true)} className="btn btn-add">
            <Plus size={18} /> Thêm nhóm
          </button>
        </div>
      </div>

      {showForm && (
        <AddTeamModal
          onClose={() => setShowForm(false)}
          internshipPrograms={internshipPrograms}
          mentors={mentors}
          onAddTeam={(data) => {
            onAddTeam(data);
            setShowForm(false);
          }}
        />
      )}
    </>
  );
};

export default MentorFilter;
