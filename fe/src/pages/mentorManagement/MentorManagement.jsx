import React, { useState, useEffect } from "react";
import MentorFilters from "./MentorFilters";
import { useSearchParams } from "react-router-dom";
import MentorTable from "./MentorTable";
import Pagination from "~/components/Pagination";
import { getMentors, editMentor } from "~/services/MentorService";
import { getAllDepartment } from "~/services/DepartmentService";

const MentorManagement = () => {
  const [loading, setLoading] = useState(false);
  const [mentors, setMentors] = useState([]);
  const [searchParams, setSearchParams] = useSearchParams();
  const [departments, setDepartments] = useState([]);

  const [filters, setFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    department: searchParams.getAll("department"),
    page: parseInt(searchParams.get("page")) || 1,
  });

  const [appliedFilters, setAppliedFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    department: searchParams.getAll("department"),
    page: parseInt(searchParams.get("page")) || 1,
  });

  useEffect(() => {
    const params = {};
    if (appliedFilters.keyWord) params.keyWord = appliedFilters.keyWord;
    if (appliedFilters.department)
      params.department = appliedFilters.department;
    if (appliedFilters.page) params.page = appliedFilters.page;
    setSearchParams(params);
  }, [appliedFilters]);

  const [pagination, setPagination] = useState({
    pageNumber: 1,
    totalElements: 0,
    totalPages: 1,
    hasNext: false,
    hasPrevious: false,
  });

  useEffect(() => {
    fetchMentors();
  }, [appliedFilters]);

  const fetchMentors = async () => {
    setLoading(true);
    const data = await getMentors({
      keyword: appliedFilters.keyWord,
      department: appliedFilters.department,
      page: appliedFilters.page,
    });
    setMentors(data.content);
    setPagination({
      pageNumber: data.pageNumber,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      hasNext: data.hasNext,
      hasPrevious: data.hasPrevious,
    });
    setLoading(false);
  };

  const updateMentor = async ({ id, departmentId }) => {
    const data = await editMentor({ id, departmentId });
    if (data) {
      setMentors((prev) =>
        prev.map((mentor) => (mentor.id === data.id ? data : mentor))
      );
    }
  };

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

  return (
    <>
      <MentorFilters
        filters={filters}
        handleFilterChange={(key, value) => {
          setFilters({ ...filters, [key]: value });
        }}
        handleSearch={handleSearch}
        departmentOptions={departmentOptions}
        onAddMentor={(newMentor) => {
          if (appliedFilters.page === 1) {
            setMentors((prev) => [newMentor, ...prev]);
          }
        }}
      />
      <MentorTable
        mentors={mentors}
        loading={loading}
        updateMentor={updateMentor}
        departmentOptions={departmentOptions.filter((d) => d.value !== "")}
      />
      <Pagination
        pagination={pagination}
        filters={appliedFilters}
        changePage={(newPage) =>
          setAppliedFilters({ ...appliedFilters, page: newPage })
        }
        name={"mentor"}
      />
    </>
  );
};

export default MentorManagement;
