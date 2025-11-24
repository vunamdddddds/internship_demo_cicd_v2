import { Search, Plus } from "lucide-react";
import Select from "react-select";
import React, { useState } from "react";
import AddInternshipProgramModal from "./AddInternshipProgramForm";

const InternshipProgramFilters = ({
  filters,
  handleFilterChange,
  handleSearch,
  departmentOptions,
  onAddInternshipProgram,
  convertToISO,
}) => {
  const [showForm, setShowForm] = useState(false);
  return (
    <>
      {/* FILTERS */}
      <div className="filter-container">
        <div className="filter-grid">
          <div className="filter-item">
            <label className="filter-label">Tên kỳ thực tập</label>
            <div className="search-input-wrapper">
              <Search className="search-icon" size={20} />
              <input
                type="text"
                placeholder="Nhập tên kì thực tập"
                className="search-input"
                value={filters.keyword}
                onChange={(e) => handleFilterChange("keyword", e.target.value)}
                onKeyPress={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>
          </div>

          <div className="filter-item">
            <label className="filter-label">Phòng ban</label>
            <Select
              className="custom-select"
              options={departmentOptions}
              isMulti
              placeholder="Chọn phòng ban..."
              value={departmentOptions.filter((opt) =>
                filters.department.map(String).includes(String(opt.value))
              )}
              onChange={(selected) =>
                handleFilterChange(
                  "department",
                  selected ? selected.map((opt) => opt.value) : []
                )
              }
            />
          </div>

          <button onClick={handleSearch} className="btn btn-search">
            <Search size={18} /> Tìm kiếm
          </button>

          <button onClick={() => setShowForm(true)} className="btn btn-add">
            <Plus size={18} /> Thêm kì thực tập
          </button>
        </div>
      </div>
      {showForm && (
        <AddInternshipProgramModal
          departmentOptions={departmentOptions}
          onClose={() => setShowForm(false)}
          onAddInternshipProgram={(data) => {
            onAddInternshipProgram(data);
            setShowForm(false);
          }}
          convertToISO={convertToISO}
        />
      )}
    </>
  );
};

export default InternshipProgramFilters;
