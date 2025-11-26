// src/pages/allowance/AllowanceManagement.jsx
import React, { useState, useEffect } from "react";
import AllowanceFilters from "./AllowanceFilters.jsx";
import AllowanceTable from "./AllowanceTable.jsx";
import Pagination from "~/components/Pagination";
import { getInternshipProgram } from "~/services/InternshipProgramService";
import { getAllowances } from "~/services/AllowanceService.jsx";

import { useSearchParams } from "react-router-dom";

const AllowanceManagement = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
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

  return (
    <div className="main-content">
      <div className="page-title">Quản lý phụ cấp thực tập sinh</div>
      <AllowanceFilters
        filters={filters}
        setFilters={setFilters}
        internshipProgramOptions={internshipProgramOptions}
        onSearch={handleSearch}
      />

      <AllowanceTable
        data={data}
        loading={loading}
      />

      <Pagination
        pagination={pagination}
        currentPage={appliedFilters.page + 1}
        changePage={(page) => setAppliedFilters((prev) => ({ ...prev, page: page - 1 }))}
        name="phụ cấp"
      />
    </div>
  );
};

export default AllowanceManagement;