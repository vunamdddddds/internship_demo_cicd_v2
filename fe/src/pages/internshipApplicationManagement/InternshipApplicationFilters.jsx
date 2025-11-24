import { Search, Filter, CheckCircle, XCircle } from "lucide-react";
import Select from "react-select";
import React, { useState } from "react";

const InternshipApplicationFilter = ({
  filters,
  handleFilterChange,
  handleSearch,
  universityOptions,
  internshipTermOptions,
  majorOptions,
  statusOptions,
  approveApplications,
  rejectApplications,
}) => {
  const [showAdvancedFilter, setShowAdvancedFilter] = useState(false);

  return (
    <>
      <div className="filter-container">
        <div className="filter-grid main-bar">
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

          <button
            className={`filter-toggle-btn ${
              showAdvancedFilter ? "active" : ""
            }`}
            onClick={() => setShowAdvancedFilter(!showAdvancedFilter)}
          >
            <Filter size={18} />
          </button>

          <button onClick={handleSearch} className="btn btn-search">
            <Search size={18} /> Tìm kiếm
          </button>

          <button
            className="btn btn-approve"
            onClick={() => approveApplications()}
          >
            <CheckCircle size={18} /> Duyệt
          </button>

          <button
            className="btn btn-reject"
            onClick={() => rejectApplications()}
          >
            <XCircle size={18} /> Từ chối
          </button>
        </div>

        {showAdvancedFilter && (
          <div className="advanced-filters">
            <div className="filter-item">
              <label className="filter-label">Kì thực tập</label>
              <Select
                className="custom-select"
                options={internshipTermOptions}
                value={internshipTermOptions.find(
                  (opt) => opt.value === filters.internshipTerm
                )}
                onChange={(selected) =>
                  handleFilterChange("internshipTerm", selected.value)
                }
              />
            </div>

            <div className="filter-item">
              <label className="filter-label">Trường học</label>
              <Select
                className="custom-select"
                options={universityOptions}
                value={universityOptions.find(
                  (opt) => opt.value === filters.university
                )}
                onChange={(selected) =>
                  handleFilterChange("university", selected.value)
                }
              />
            </div>

            <div className="filter-item">
              <label className="filter-label">Chuyên ngành</label>
              <Select
                className="custom-select"
                options={majorOptions}
                value={majorOptions.find((opt) => opt.value === filters.major)}
                onChange={(selected) =>
                  handleFilterChange("major", selected.value)
                }
              />
            </div>

            <div className="filter-item">
              <label className="filter-label">Trạng thái</label>
              <Select
                className="custom-select"
                options={statusOptions}
                value={statusOptions.find(
                  (opt) => opt.value === filters.status
                )}
                onChange={(selected) =>
                  handleFilterChange("status", selected.value)
                }
              />
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default InternshipApplicationFilter;
