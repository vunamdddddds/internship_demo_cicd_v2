import { Search, Plus } from "lucide-react";
import Select from "react-select";
import React, { useState } from "react";
import AddMentorModal from "./AddMentorForm";

const MentorFilters = ({
  filters,
  handleFilterChange,
  handleSearch,
  departmentOptions,
  onAddMentor,
}) => {
  const [showForm, setShowForm] = useState(false);
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
            <Plus size={18} /> Thêm Mentor
          </button>
        </div>
      </div>
      {showForm && (
        <AddMentorModal
          departmentOptions={departmentOptions}
          onClose={() => setShowForm(false)}
          onAddMentor={(data) => {
            onAddMentor(data);
            setShowForm(false);
          }}
        />
      )}
    </>
  );
};
export default MentorFilters;
