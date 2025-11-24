import React, { useState, useEffect } from "react";
import Pagination from "~/components/Pagination";
import { useSearchParams } from "react-router-dom";
import Filter from "./Filters";
import Table from "./Table";
import { getAllLeaveRequests } from "~/services/LeaveRequestService";

const LeaveRequestManagement = () => {
  const [loading, setLoading] = useState(false);
  const [leaveRequests, setLeaveRequests] = useState([]);
  const [searchParams, setSearchParams] = useSearchParams();

  const [filters, setFilters] = useState({
    keyword: searchParams.get("keyword") || "",
    type: searchParams.get("type") || "",
    status: searchParams.get("status") || "ALL",
    page: parseInt(searchParams.get("page")) || 1,
  });

  const [appliedFilters, setAppliedFilters] = useState({
    keyword: searchParams.get("keyword") || "",
    type: searchParams.get("type") || "",
    status: searchParams.get("status") || "ALL",
    page: parseInt(searchParams.get("page")) || 1,
  });

  useEffect(() => {
    const params = {};
    if (appliedFilters.keyword) params.keyword = appliedFilters.keyword;
    if (appliedFilters.type) params.type = appliedFilters.type;
    if (appliedFilters.status != null) params.status = appliedFilters.status;
    if (appliedFilters.page) params.page = appliedFilters.page;
    setSearchParams(params);
  }, [appliedFilters]);

  useEffect(() => {
    fetchleaveRequests();
  }, [appliedFilters]);

  const fetchleaveRequests = async () => {
    setLoading(true);
    const data = await getAllLeaveRequests({
      keyword: appliedFilters.keyword,
      type: appliedFilters.type,
      status: appliedFilters.status,
      page: appliedFilters.page,
    });
    setLeaveRequests(data.content);
    setPagination({
      pageNumber: data.pageNumber,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      hasNext: data.hasNext,
      hasPrevious: data.hasPrevious,
    });
    setLoading(false);
  };

  const [pagination, setPagination] = useState({
    pageNumber: 1,
    totalElements: 0,
    totalPages: 1,
    hasNext: false,
    hasPrevious: false,
  });

  const handleSearch = () => {
    setAppliedFilters({ ...filters, page: 1 });
  };

  const leaveRequestOptions = [
    { value: "", label: "Tất cả loại đơn" },
    { value: "LATE", label: "Xin đi muộn" },
    { value: "EARLY_LEAVE", label: "Xin về sớm" },
    { value: "ON_LEAVE", label: "Xin nghỉ phép" },
  ];

  return (
    <>
      <Filter
        filters={filters}
        handleFilterChange={(key, value) => {
          setFilters({ ...filters, [key]: value });
        }}
        handleSearch={handleSearch}
        leaveRequestOptions={leaveRequestOptions}
      />

      <Table
        leaveRequests={leaveRequests}
        loading={loading}
        leaveRequestOptions={leaveRequestOptions}
        setLeaveRequests={setLeaveRequests}
      />

      <Pagination
        pagination={pagination}
        filters={appliedFilters}
        changePage={(newPage) =>
          setAppliedFilters({ ...appliedFilters, page: newPage })
        }
        name={"đơn xin phép"}
      />
    </>
  );
};

export default LeaveRequestManagement;
