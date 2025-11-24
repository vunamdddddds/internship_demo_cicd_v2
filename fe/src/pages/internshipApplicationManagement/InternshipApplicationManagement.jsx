import InternshipApplicationFilter from "./InternshipApplicationFilters";
import { getAllUniversity } from "~/services/UniversityService";
import { getAllMajor } from "~/services/MajorService";
import { getAllInternshipProgram } from "~/services/InternshipProgramService";
import {
  getAllApplication,
  updateStatus,
} from "~/services/InternshipApplicationService";
import React, { useState, useEffect } from "react";
import InternshipApplicationTable from "./InternshipApplicationTable";
import Pagination from "~/components/Pagination";
import { useSearchParams } from "react-router-dom";

const InternshipApplicationManagement = () => {
  const [loading, setLoading] = useState(false);
  const [internships, setInternships] = useState([]);
  const [universities, setUniversities] = useState([]);
  const [majors, setMajors] = useState([]);
  const [idApplications, setIdApplications] = useState(new Set());
  const [internshipPrograms, setInternshipPrograms] = useState([]);
  const [searchParams, setSearchParams] = useSearchParams();

  const [filters, setFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    status: searchParams.get("status") || "",
    internshipTerm: parseInt(searchParams.get("internshipTerm")) || 0,
    university: parseInt(searchParams.get("university")) || 0,
    major: parseInt(searchParams.get("major")) || 0,
    page: parseInt(searchParams.get("page")) || 1,
  });

  const [appliedFilters, setAppliedFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    status: searchParams.get("status") || "",
    internshipTerm: parseInt(searchParams.get("internshipTerm")) || 0,
    university: parseInt(searchParams.get("university")) || 0,
    major: parseInt(searchParams.get("major")) || 0,
    page: parseInt(searchParams.get("page")) || 1,
  });

  useEffect(() => {
    const params = {};
    if (appliedFilters.keyWord) params.keyWord = appliedFilters.keyWord;
    if (appliedFilters.status) params.status = appliedFilters.status;
    if (appliedFilters.internshipTerm)
      params.internshipTerm = appliedFilters.internshipTerm;
    if (appliedFilters.university)
      params.university = appliedFilters.university;
    if (appliedFilters.major) params.major = appliedFilters.major;
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

  const handleSearch = () => {
    setAppliedFilters({ ...filters, page: 1 });
  };

  useEffect(() => {
    const fetchData = async () => {
      const [uniData, majorData, InternshipProgramData] = await Promise.all([
        getAllUniversity(),
        getAllMajor(),
        getAllInternshipProgram(),
      ]);
      setUniversities([{ id: 0, name: "Tất cả" }, ...uniData]);
      setMajors([{ id: 0, name: "Tất cả" }, ...majorData]);
      setInternshipPrograms([
        { id: 0, name: "Tất cả" },
        ...InternshipProgramData,
      ]);
    };
    fetchData();
  }, []);

  const approveApplications = async (action) => {
    const success = await updateStatus({
      idList: idApplications,
      action,
    });
    if (success) {
      setIdApplications(new Set());
      fetchInternships();
    }
  };

  const universityOptions = universities.map((u) => ({
    value: u.id,
    label: u.name,
  }));

  const majorOptions = majors.map((m) => ({
    value: m.id,
    label: m.name,
  }));

  const internshipProgramOptions = internshipPrograms.map((m) => ({
    value: m.id,
    label: m.name,
  }));

  useEffect(() => {
    fetchInternships();
  }, [appliedFilters]);

  const fetchInternships = async () => {
    setLoading(true);
    const data = await getAllApplication({
      internshipTerm: appliedFilters.internshipTerm,
      university: appliedFilters.university,
      major: appliedFilters.major,
      keyword: appliedFilters.keyWord,
      status: appliedFilters.status,
      page: appliedFilters.page,
    });
    setInternships(data.content);
    setPagination({
      pageNumber: data.pageNumber,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      hasNext: data.hasNext,
      hasPrevious: data.hasPrevious,
    });
    setLoading(false);
  };

  const statusOptions = [
    { value: "", label: "Tất cả" },
    { value: "SUBMITTED", label: "SUBMITTED" },
    { value: "UNDER_REVIEW", label: "UNDER_REVIEW" },
    { value: "APPROVED", label: "APPROVED" },
    { value: "CONFIRM", label: "CONFIRM" },
    { value: "REJECTED", label: "REJECTED" },
    { value: "WITHDRAWN", label: "WITHDRAWN" },
  ];

  return (
    <>
      <InternshipApplicationFilter
        filters={filters}
        handleFilterChange={(key, value) => {
          setFilters({ ...filters, [key]: value });
        }}
        handleSearch={handleSearch}
        universityOptions={universityOptions}
        majorOptions={majorOptions}
        internshipTermOptions={internshipProgramOptions}
        statusOptions={statusOptions}
        approveApplications={() => approveApplications("approve")}
        rejectApplications={() => approveApplications("reject")}
      />

      <InternshipApplicationTable
        internships={internships}
        loading={loading}
        setIdApplications={setIdApplications}
        idApplications={idApplications}
      />

      <Pagination
        pagination={pagination}
        filters={appliedFilters}
        changePage={(newPage) =>
          setAppliedFilters({ ...appliedFilters, page: newPage })
        }
        name={"hồ sơ thực tập"}
      />
    </>
  );
};

export default InternshipApplicationManagement;
