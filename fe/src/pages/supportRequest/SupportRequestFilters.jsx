import React from "react";
import { Search } from "lucide-react";
import Select from "react-select";

const statusOptions = [
  { value: "", label: "Tất cả" },
  { value: "PENDING", label: "Chờ xử lý" },
  { value: "RESOLVED", label: "Đã duyệt" },
  { value: "REJECTED", label: "Đã từ chối" },
];

const selectStyles = {
  control: (base) => ({
    ...base,
    height: 42,
    minHeight: 42,
    borderRadius: "8px",
    borderColor: "#e2e8f0",
    boxShadow: "none",
    "&:hover": { borderColor: "#94a3b8" },
  }),
  menu: (base) => ({ ...base, zIndex: 100 }),
  placeholder: (base) => ({ ...base, color: "#9ca3af", fontSize: "14px" }),
  option: (base, state) => ({
    ...base,
    fontSize: "14px",
    cursor: "pointer",
    backgroundColor: state.isSelected ? "#2563eb" : state.isFocused ? "#eff6ff" : "white",
    color: state.isSelected ? "white" : "#1e293b",
  }),
};

const SupportRequestFilters = ({ filters, handleFilterChange, handleSearch }) => {
  return (
    <div className="filter-container">
      <div className="filter-grid">
        {/* SEARCH */}
        <div className="filter-item">
          <label className="filter-label">Tìm theo tên hoặc email</label>
          <div className="search-input-wrapper">
            <Search size={16} className="search-icon" />
            <input
              className="search-input"
              placeholder="Nhập từ khóa..."
              value={filters.search}
              onChange={(e) => handleFilterChange("search", e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSearch()}
            />
          </div>
        </div>

        {/* STATUS FILTER */}
        <div className="filter-item">
          <label className="filter-label">Trạng thái</label>
          <Select
            value={statusOptions.find((opt) => opt.value === filters.status)}
            onChange={(opt) => handleFilterChange("status", opt ? opt.value : "")}
            options={statusOptions}
            styles={selectStyles}
            placeholder="Chọn trạng thái"
            isClearable={false}
          />
        </div>

        <button className="btn btn-search" onClick={handleSearch}>
          Lọc dữ liệu
        </button>
      </div>
    </div>
  );
};

export default SupportRequestFilters;