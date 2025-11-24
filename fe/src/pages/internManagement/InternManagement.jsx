import React, { useState, useEffect } from "react";
import { getAllIntern, updateIntern } from "~/services/InternService";
import { getAllUniversity } from "~/services/UniversityService";
import { getAllMajor } from "~/services/MajorService";
import { getAllInternshipProgram } from "~/services/InternshipProgramService";
import InternFilters from "./InternFilters";
import InternTable from "./InternTable";
import Pagination from "~/components/Pagination";
import InternEditModal from "./EditInternForm";
import { useSearchParams } from "react-router-dom";

const InternManagement = () => {
  const [interns, setInterns] = useState([]);
  const [loading, setLoading] = useState(false);
  const [universities, setUniversities] = useState([]);
  const [majors, setMajors] = useState([]);
  const [internshipProgram, setInternshipProgram] = useState([]);
  const [showEditForm, setShowEditForm] = useState(false);
  const [searchParams, setSearchParams] = useSearchParams();

  // filter trên giao diện
  const [filters, setFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    universityId: parseInt(searchParams.get("universityId")) || 0,
    majorId: parseInt(searchParams.get("majorId")) || 0,
    page: parseInt(searchParams.get("page")) || 1,
  });

  // filter đang được áp dụng để gọi API
  const [appliedFilters, setAppliedFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    universityId: parseInt(searchParams.get("universityId")) || 0,
    majorId: parseInt(searchParams.get("majorId")) || 0,
    page: parseInt(searchParams.get("page")) || 1,
  });

  useEffect(() => {
    const params = {};
    if (appliedFilters.keyWord) params.keyWord = appliedFilters.keyWord;
    if (appliedFilters.universityId)
      params.universityId = appliedFilters.universityId;
    if (appliedFilters.majorId) params.majorId = appliedFilters.majorId;
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

  const [formData, setFormData] = useState({
    id: null,
    status: "",
    majorId: 0,
    universityId: 0,
  });

  const handleChange = (key, value) => {
    setFormData({ ...formData, [key]: value });
  };

  const statusOptions = [
    { value: "ACTIVE", label: "Active" },
    { value: "SUSPENDED", label: "Suspended" },
    { value: "COMPLETED", label: "Completed" },
    { value: "DROPPED", label: "Dropped" },
  ];

  // load danh sách trường và chuyên ngành
  useEffect(() => {
    const fetchData = async () => {
      const [uniData, majorData, internshipProgramData] = await Promise.all([
        getAllUniversity(),
        getAllMajor(),
        getAllInternshipProgram(),
      ]);
      setUniversities([{ id: 0, name: "Tất cả" }, ...uniData]);
      setMajors([{ id: 0, name: "Tất cả" }, ...majorData]);
      setInternshipProgram([
        { id: 0, name: "Tất cả" },
        ...internshipProgramData,
      ]);
    };
    fetchData();
  }, []);

  // gọi API khi appliedFilters thay đổi (ấn tìm kiếm hoặc chuyển trang)
  useEffect(() => {
    fetchInterns();
  }, [appliedFilters]);

  const fetchInterns = async () => {
    setLoading(true);
    const data = await getAllIntern({
      keyWord: appliedFilters.keyWord,
      universityId: appliedFilters.universityId,
      majorId: appliedFilters.majorId,
      page: appliedFilters.page,
    });
    setInterns(data.content);
    setPagination({
      pageNumber: data.pageNumber,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      hasNext: data.hasNext,
      hasPrevious: data.hasPrevious,
    });
    setLoading(false);
  };

  const handleEdit = (intern) => {
    const selectedMajor = majors.find((m) => m.name === intern.major);
    const selectedUni = universities.find((u) => u.name === intern.university);
    const selectedStatus = statusOptions.find((s) => s.value === intern.status);
    setFormData({
      id: intern.id,
      status: selectedStatus ? selectedStatus.value : "",
      majorId: selectedMajor ? selectedMajor.id : 0,
      universityId: selectedUni ? selectedUni.id : 0,
    });
    setShowEditForm(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const updated = await updateIntern(formData);
    if (updated) {
      setInterns((prev) =>
        prev.map((intern) => (intern.id === updated.id ? updated : intern))
      );
      setShowEditForm(false);
    }
  };

  const universityOptions = universities
    .filter((u) => u.id !== 0)
    .map((u) => ({ value: u.id, label: u.name }));

  const majorOptions = majors
    .filter((m) => m.id !== 0)
    .map((m) => ({ value: m.id, label: m.name }));

  const handleSearch = () => {
    setAppliedFilters({ ...filters, page: 1 });
  };

  return (
    <>
      <InternFilters
        filters={filters}
        universities={universities}
        majors={majors}
        internshipProgram={internshipProgram}
        handleFilterChange={(key, value) =>
          setFilters({ ...filters, [key]: value })
        }
        handleSearch={handleSearch}
        onAddIntern={(newIntern) => {
          if (appliedFilters.page === 1) {
            setInterns((prev) => [newIntern, ...prev]);
          }
        }}
      />

      <InternTable interns={interns} loading={loading} onEdit={handleEdit} />

      <Pagination
        pagination={pagination}
        filters={appliedFilters}
        changePage={(newPage) =>
          setAppliedFilters({ ...appliedFilters, page: newPage })
        }
        name={"thực tập sinh"}
      />

      {showEditForm && (
        <InternEditModal
          formData={formData}
          statusOptions={statusOptions}
          majorOptions={majorOptions}
          universityOptions={universityOptions}
          onChange={handleChange}
          onSubmit={handleSubmit}
          onClose={() => setShowEditForm(false)}
        />
      )}
    </>
  );
};

export default InternManagement;
