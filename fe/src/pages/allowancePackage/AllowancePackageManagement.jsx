import React, { useState, useEffect } from "react";
import AllowancePackageService from "~/services/AllowancePackageService";
import AllowancePackageTable from "./AllowancePackageTable"; // Will create this
import AllowancePackageFormModal from "./AllowancePackageFormModal"; // Will create this
import Pagination from "~/components/Pagination";
import { getInternshipProgram } from "~/services/InternshipProgramService";
import Swal from "sweetalert2"; // For confirmation dialogs

const AllowancePackageManagement = () => {
  const [allowancePackages, setAllowancePackages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPackage, setEditingPackage] = useState(null); // For editing
  const [internshipPrograms, setInternshipPrograms] = useState([]);
  const [pagination, setPagination] = useState({
    pageNumber: 1,
    totalPages: 1,
    totalElements: 0,
    hasNext: false,
    hasPrevious: false,
  });
  const [filters, setFilters] = useState({ page: 0, size: 10, keyword: "" }); // Basic filters

  useEffect(() => {


    const fetchPrograms = async () => {
      const res = await getInternshipProgram({ size: 1000, activeOnly: true }); // Fetch only active programs
      if (res && res.content) {
        setInternshipPrograms(res.content);
      }
    };
    fetchPrograms();
  }, []);


   useEffect(() => {
    fetchAllowancePackages();
  }, [filters])


  const fetchAllowancePackages = async () => {
    setLoading(true);
    const res = await AllowancePackageService.getAllAllowancePackages({
      page: filters.page,
      size: filters.size,
      keyword: filters.keyword,
    });
    if (res) {
      setAllowancePackages(res.content);
      setPagination({
        pageNumber: res.pageable.pageNumber + 1, // Adjust for 1-based page number
        totalPages: res.totalPages,
        totalElements: res.totalElements,
        hasNext: !res.last,
        hasPrevious: !res.first,
      });
    } else {
      setAllowancePackages([]);
      setPagination({ pageNumber: 1, totalPages: 1, totalElements: 0, hasNext: false, hasPrevious: false });
    }
    setLoading(false);
  };

  const handleCreateNew = () => {
    setEditingPackage(null); // Clear any previous editing state
    setIsModalOpen(true);
  };

  const handleEdit = (packageData) => {
    setEditingPackage(packageData);
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, delete it!",
    });

    if (result.isConfirmed) {
      const success = await AllowancePackageService.deleteAllowancePackage(id);
      if (success) {
        fetchAllowancePackages(); // Re-fetch to update the list
      }
    }
  };

  const handleFormSubmit = async (formData) => {
    let success = false;
    if (editingPackage) {
      // Update existing
      success = await AllowancePackageService.updateAllowancePackage(editingPackage.id, formData);
    } else {
      // Create new
      success = await AllowancePackageService.createAllowancePackage(formData);
    }

    if (success) {
      setIsModalOpen(false);
      fetchAllowancePackages(); // Re-fetch to update the list
    }
  };

  const changePage = (newPage) => {
    setFilters((prev) => ({ ...prev, page: newPage - 1 })); // Adjust for 0-based page in API
  };

  const handleSearch = (keyword) => {
    setFilters((prev) => ({ ...prev, keyword: keyword, page: 0 })); // Reset page on new search
  };

  return (
    <div className="main-content">
      <div className="page-title">Quản lý Gói Phụ Cấp</div>

      {/* Basic Search/Filter input */}
      <div className="flex items-center gap-2 mb-4">
        <input
          type="text"
          placeholder="Search by package name..."
          className="form-control"
          value={filters.keyword}
          onChange={(e) => setFilters(prev => ({ ...prev, keyword: e.target.value }))}
        />
        <button className="btn btn-primary" onClick={() => handleSearch(filters.keyword)}>
          Search
        </button>
      </div>

      <div className="mb-4">
        <button className="btn btn-add" onClick={handleCreateNew}>
          Tạo gói phụ cấp mới
        </button>
      </div>

      <AllowancePackageTable
        data={allowancePackages}
        loading={loading}
        onEdit={handleEdit}
        onDelete={handleDelete}

      />

      <Pagination
        pagination={pagination}
        currentPage={filters.page + 1}
        changePage={changePage}
        name="gói phụ cấp"
      />


      {isModalOpen && (
        <AllowancePackageFormModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          onSubmit={handleFormSubmit}
          initialData={editingPackage}
          internshipPrograms={internshipPrograms}
        />
      )}
    </div>
  );
};

export default AllowancePackageManagement;
