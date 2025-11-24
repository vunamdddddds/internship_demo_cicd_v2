import React, { useState } from "react";
import { Search, Plus } from "lucide-react";
import Select from "react-select";
import AddInternModal from "./AddInternForm";

const InternFilters = ({
  filters,
  universities,
  majors,
  handleFilterChange,
  handleSearch,
  onAddIntern,
  internshipProgram,
}) => {
  const [showForm, setShowForm] = useState(false);

  const universityOptions = universities.map((u) => ({
    value: u.id,
    label: u.name,
  }));

  const majorOptions = majors.map((m) => ({
    value: m.id,
    label: m.name,
  }));

  return (
    <>
      {/* FILTERS */}
      <div className="filter-container">
        <div className="filter-grid">
          <div className="filter-item">
            <label className="filter-label">Từ khóa</label>
            <div className="search-input-wrapper">
              <Search className="search-icon" size={20} />
              <input
                type="text"
                placeholder="Nhập tên, email hoặc SĐT"
                className="search-input"
                value={filters.keyWord}
                onChange={(e) => handleFilterChange("keyWord", e.target.value)}
                onKeyPress={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>
          </div>

          <div className="filter-item">
            <label className="filter-label">Trường học</label>
            <Select
              className="custom-select"
              options={universityOptions}
              value={universityOptions.find(
                (opt) => opt.value === filters.universityId
              )}
              onChange={(selected) =>
                handleFilterChange("universityId", selected.value)
              }
            />
          </div>

          <div className="filter-item">
            <label className="filter-label">Chuyên ngành</label>
            <Select
              className="custom-select"
              options={majorOptions}
              value={majorOptions.find((opt) => opt.value === filters.majorId)}
              onChange={(selected) =>
                handleFilterChange("majorId", selected.value)
              }
            />
          </div>

          <button onClick={handleSearch} className="btn btn-search">
            <Search size={18} /> Tìm kiếm
          </button>

          <button onClick={() => setShowForm(true)} className="btn btn-add">
            <Plus size={18} /> Thêm thực tập sinh
          </button>
        </div>
      </div>

      {/* MODAL FORM */}
      {showForm && (
        <AddInternModal
          universities={universities}
          majors={majors}
          internshipProgram={internshipProgram}
          onClose={() => setShowForm(false)}
          onAddIntern={(data) => {
            onAddIntern(data);
            setShowForm(false);
          }}
        />
      )}
    </>
  );
};

export default InternFilters;
