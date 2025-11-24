import { Search, Plus } from "lucide-react";
import Select from "react-select";
import React from "react";

const Filter = ({
  filters,
  handleFilterChange,
  handleSearch,
  leaveRequestOptions,
}) => {
  const statusOptions = [
    { value: "ALL", label: "Tất cả" },
    { value: "APPROVED", label: "Đã duyệt" },
    { value: "REJECTED", label: "Từ chối" },
    { value: "PENDING", label: "Chờ duyệt" },
  ];

  return (
    <>
      {/* FILTERS */}
      <div className="filter-container">
        <div className="filter-grid">
          <div className="filter-item">
            <label className="filter-label">Họ tên</label>
            <div className="search-input-wrapper">
              <Search className="search-icon" size={20} />
              <input
                type="text"
                placeholder="Nhập họ tên"
                className="search-input"
                value={filters.keyword}
                onChange={(e) => handleFilterChange("keyword", e.target.value)}
                onKeyPress={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>
          </div>

          <div className="filter-item">
            <label className="filter-label">Loại đơn</label>
            <Select
              className="custom-select"
              options={leaveRequestOptions}
              value={leaveRequestOptions.find(
                (opt) => opt.value === filters.type
              )}
              onChange={(selected) =>
                handleFilterChange("type", selected.value)
              }
            />
          </div>

          <div className="filter-item">
            <label className="filter-label">Trạng thái</label>
            <Select
              className="custom-select"
              options={statusOptions}
              value={statusOptions.find((opt) => opt.value === filters.status)}
              onChange={(selected) =>
                handleFilterChange("status", selected.value)
              }
            />
          </div>

          <button onClick={handleSearch} className="btn btn-search">
            <Search size={18} /> Tìm kiếm
          </button>
        </div>
      </div>
    </>
  );
};

export default Filter;
