import React, { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { getAllDepartment } from "~/services/DepartmentService";
import InternshipProgramFilters from "./InternshipProgramFilters";
import InternshipProgramTable from "./InternshipProgramTable";
import { getInternshipProgram } from "~/services/InternshipProgramService";
import Pagination from "~/components/Pagination";

const InternshipProgramManagament = () => {
  const [loading, setLoading] = useState(false);
  const [internshipPrograms, setInternshipPrograms] = useState([]);
  const [searchParams, setSearchParams] = useSearchParams();
  const [departments, setDepartments] = useState([]);

  const [filters, setFilters] = useState({
    keyword: searchParams.get("keyword") || "",
    department: searchParams.getAll("department"),
    page: parseInt(searchParams.get("page")) || 1,
  });

  const [appliedFilters, setAppliedFilters] = useState({
    keyword: searchParams.get("keyword") || "",
    department: searchParams.getAll("department"),
    page: parseInt(searchParams.get("page")) || 1,
  });

  const [pagination, setPagination] = useState({
    pageNumber: 1,
    totalElements: 0,
    totalPages: 1,
    hasNext: false,
    hasPrevious: false,
  });

  useEffect(() => {
    const params = {};
    if (appliedFilters.keyword) params.keyword = appliedFilters.keyword;
    if (appliedFilters.department)
      params.department = appliedFilters.department;
    if (appliedFilters.page) params.page = appliedFilters.page;
    setSearchParams(params);
  }, [appliedFilters]);

  const handleSearch = () => {
    setAppliedFilters({ ...filters, page: 1 });
  };

  useEffect(() => {
    const fetchData = async () => {
      const departments = await getAllDepartment();
      setDepartments(departments);
    };
    fetchData();
  }, []);

  const departmentOptions = departments.map((d) => ({
    value: d.id,
    label: d.name,
  }));

  useEffect(() => {
    fetchMentors();
  }, [appliedFilters]);

  const fetchMentors = async () => {
    setLoading(true);
    const data = await getInternshipProgram({
      keyword: appliedFilters.keyword,
      department: appliedFilters.department,
      page: appliedFilters.page,
    });
    setInternshipPrograms(data.content);
    setPagination({
      pageNumber: data.pageNumber,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      hasNext: data.hasNext,
      hasPrevious: data.hasPrevious,
    });
    setLoading(false);
  };

  const convertToISO = (datetimeLocal) => {
    if (!datetimeLocal) return null;

    const date = new Date(datetimeLocal);

    // Tạo lại chuỗi theo local, KHÔNG trừ đi 7 tiếng
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");

    // Xuất đúng định dạng ISO mà server mong đợi (không kèm Z)
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  };

  return (
    <>
      <InternshipProgramFilters
        filters={filters}
        handleFilterChange={(key, value) => {
          setFilters({ ...filters, [key]: value });
        }}
        handleSearch={handleSearch}
        departmentOptions={departmentOptions}
        onAddInternshipProgram={(newInternshipProgram) => {
          if (appliedFilters.page === 1) {
            setInternshipPrograms((prev) => [newInternshipProgram, ...prev]);
          }
        }}
        convertToISO={convertToISO}
      />

      <InternshipProgramTable
        internshipPrograms={internshipPrograms}
        loading={loading}
        convertToISO={convertToISO}
      />

      <Pagination
        pagination={pagination}
        filters={appliedFilters}
        changePage={(newPage) =>
          setAppliedFilters({ ...appliedFilters, page: newPage })
        }
        name={"kì thực tập"}
      />
    </>
  );
};

export default InternshipProgramManagament;
