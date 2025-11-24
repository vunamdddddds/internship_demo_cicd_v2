// src/pages/allowance/AllowanceFilters.jsx
import React from "react";
import { Search } from "lucide-react";
import Select from "react-select";

const AllowanceFilters = ({ filters, setFilters, onSearch, internshipProgramOptions }) => {
  const handleChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  return (
    <div className="filter-container">
      <div className="filter-grid">
        <div className="filter-item">
          <label className="filter-label">Tên hoặc Email</label>
          <div className="search-input-wrapper">
            <Search className="search-icon" size={20} />
            <input
              type="text"
              className="search-input"
              placeholder="Nhập tên hoặc email..."
              value={filters.keyword || ""}
              onChange={(e) => handleChange("keyword", e.target.value)}
              onKeyPress={(e) => e.key === "Enter" && onSearch()}
            />
          </div>
        </div>

        <div className="filter-item">
          <label className="filter-label">Kỳ thực tập</label>
          <Select
            className="custom-select"
            options={[{ value: 0, label: "Tất cả kỳ" }, ...internshipProgramOptions]}
            value={internshipProgramOptions.find(o => o.value === filters.internshipProgramId) || { value: 0, label: "Tất cả kỳ" }}
            onChange={(opt) => handleChange("internshipProgramId", opt.value)}
            placeholder="Chọn kỳ..."
          />
        </div>

        <div className="filter-item">
          <label className="filter-label">Trạng thái</label>
          <Select
            className="custom-select"
            options={[
              { value: "", label: "Tất cả trạng thái" },
              { value: "PAID", label: "Đã chuyển" },
              { value: "PENDING", label: "Chưa chuyển" },
            ]}
            value={[{ value: "", label: "Tất cả trạng thái" }, { value: "PAID", label: "Đã chuyển" }, { value: "PENDING", label: "Chưa chuyển" }].find(o => o.value === filters.status)}
            onChange={(opt) => handleChange("status", opt.value)}
          />
        </div>

        <button onClick={onSearch} className="btn btn-search">
          <Search size={18} /> Tìm kiếm
        </button>
      </div>
    </div>
  );
};

export default AllowanceFilters;