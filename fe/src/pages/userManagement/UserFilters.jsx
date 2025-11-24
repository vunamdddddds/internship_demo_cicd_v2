import { Search, Plus } from "lucide-react";
import Select from "react-select";
import React, { useState } from "react";
import AddUserForm from "./AddUserForm";

const UserFilter = ({
  filters,
  handleFilterChange,
  handleSearch,
  roleOptions,
  onAddUser,
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
            <label className="filter-label">Vai trò</label>
            <Select
              className="custom-select"
              options={roleOptions}
              value={roleOptions.find((opt) => opt.value === filters.role)}
              onChange={(selected) =>
                handleFilterChange("role", selected.value)
              }
            />
          </div>

          <button onClick={handleSearch} className="btn btn-search">
            <Search size={18} /> Tìm kiếm
          </button>

          <button onClick={() => setShowForm(true)} className="btn btn-add">
            <Plus size={18} /> Thêm người dùng
          </button>
        </div>
      </div>

      {showForm && (
        <AddUserForm
          onClose={() => setShowForm(false)}
          roleOptions={roleOptions}
          onAddUser={(data) => {
            onAddUser(data);
            setShowForm(false);
          }}
        />
      )}
    </>
  );
};

export default UserFilter;
