// src/pages/allowance/AllowanceManagement.jsx
import React, { useState, useEffect } from "react";
import Swal from "sweetalert2";
import AllowanceFilters from "./AllowanceFilters.jsx";
import AllowanceTable from "./AllowanceTable.jsx";
import Pagination from "~/components/Pagination";
import CreateAllowanceModal from "./CreateAllowanceModal.jsx";
import { getInternshipProgram } from "~/services/InternshipProgramService";
import { getAllowances, transferAllowance, createAllowance, cancelAllowance } from "~/services/AllowanceService.jsx";

import { useSearchParams } from "react-router-dom";

const AllowanceManagement = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [internshipProgramOptions, setInternshipProgramOptions] = useState([]);
  const [searchParams, setSearchParams] = useSearchParams();

  const [filters, setFilters] = useState({
    internshipProgramId:
      parseInt(searchParams.get("internshipProgramId")) || 0,
    keyword: searchParams.get("keyword") || "",
    status: searchParams.get("status") || "",
    page: parseInt(searchParams.get("page")) || 1,
  });

  const [appliedFilters, setAppliedFilters] = useState({
    internshipProgramId:
      parseInt(searchParams.get("internshipProgramId")) || 0,
    keyword: searchParams.get("keyword") || "",
    status: searchParams.get("status") || "",
    page: parseInt(searchParams.get("page")) || 0,
  });

  const [pagination, setPagination] = useState({
    pageNumber: 1,
    totalPages: 1,
    hasNext: false,
    hasPrevious: false,
    totalElements: 0,
  });

  useEffect(() => {
    const params = {};
    if (appliedFilters.keyword) params.keyword = appliedFilters.keyword;
    if (appliedFilters.internshipProgramId)
      params.internshipProgramId = appliedFilters.internshipProgramId;
    if (appliedFilters.status) params.status = appliedFilters.status;
    if (appliedFilters.page) params.page = appliedFilters.page;
    setSearchParams(params);
  }, [appliedFilters]);

  useEffect(() => {
    const fetchPrograms = async () => {
      try {
        const res = await getInternshipProgram({ page: 0 });
        const opts = (res.content || []).map(p => ({
          value: p.id,
          label: p.name,
        }));
        setInternshipProgramOptions(opts);
      } catch (err) {
        console.error(err);
      }
    };
    fetchPrograms();
  }, []);

  useEffect(() => {
    fetchData();
  }, [appliedFilters]);

  const fetchData = async () => {
    setLoading(true);
    const res = await getAllowances({
      internshipProgramId: appliedFilters.internshipProgramId,
      keyword: appliedFilters.keyword,
      status: appliedFilters.status,
      page: appliedFilters.page,
    });
    setData(res.content);
    setPagination({
      totalPages: res.totalPages,
      totalElements: res.totalElements,
      hasNext: res.hasNext,
      hasPrevious: res.hasPrevious,
    });
    setLoading(false);
  };

  const handleSearch = () => {
    setAppliedFilters({ ...filters, page: 0 });
  };

  const handleTransfer = async (item) => {
    const result = await Swal.fire({
      title: "Xác nhận chuyển phụ cấp",
      html: `<div style="text-align:center;padding:16px 0">
        <p>Chuyển cho <strong>${item.internName}</strong></p>
        <p><strong>Kỳ:</strong> ${item.internshipProgramName}</p>
        <p style="font-size:18px;color:#dc2626"><strong>${item.amount.toLocaleString("vi-VN")} VND</strong></p>
      </div>`,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#16a34a",
      cancelButtonColor: "#dc2626",
      confirmButtonText: "Chuyển tiền",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      const updated = await transferAllowance(item.id);
      if (updated) {
        setData(prev => prev.map(x => (x.id === updated.id ? updated : x)));
      }
    }
  };

  const handleCancel = async (item) => {
    const result = await Swal.fire({
      title: "Xác nhận hủy",
      text: `Bạn có chắc chắn muốn hủy khoản phụ cấp cho ${item.internName}? Hành động này không thể hoàn tác.`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#dc2626",
      cancelButtonColor: "#6b7280",
      confirmButtonText: "Đồng ý hủy",
      cancelButtonText: "Không",
    });

    if (result.isConfirmed) {
      await cancelAllowance(item.id);
      // Cập nhật lại trạng thái trên UI
      setData(prev => prev.map(x => x.id === item.id ? { ...x, status: "CANCELED" } : x));
    }
  };
  
  const handleCreateSubmit = async (formData) => {
    try {
      const newData = await createAllowance(formData);
      if (newData) {
        setData(prev => [newData, ...prev]);
        setIsModalOpen(false);
      }
    } catch(err) {
      // Lỗi đã được xử lý bằng toast trong service
      console.error(err);
    }
  };

  return (
    <div className="main-content">
      <div className="page-title">Quản lý phụ cấp thực tập sinh</div>
      <AllowanceFilters
        filters={filters}
        setFilters={setFilters}
        internshipProgramOptions={internshipProgramOptions}
        onSearch={handleSearch}
      />

      <div className="mb-4">
        <button className="btn btn-add" onClick={() => setIsModalOpen(true)}>
          Tạo mới
        </button>
      </div>

      <AllowanceTable 
        data={data} 
        loading={loading} 
        onTransfer={handleTransfer}
        onCancel={handleCancel}
      />

      <Pagination
        pagination={pagination}
        filters={appliedFilters}
        changePage={(page) => setAppliedFilters((prev) => ({ ...prev, page }))}
        name="phụ cấp"
      />

      <CreateAllowanceModal 
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleCreateSubmit}
      />
    </div>
  );
};

export default AllowanceManagement;