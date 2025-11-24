import React, { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import MentorFilter from "./TeamFilters";
import { getAllInternshipProgram } from "~/services/InternshipProgramService";
import { getAllMentor } from "~/services/MentorService";
import { getTeams } from "~/services/TeamService";
import TeamTable from "./TeamTable";
import Pagination from "~/components/Pagination";

const TeamManagement = () => {
  const [loading, setLoading] = useState(false);
  const [teams, setTeams] = useState([]);
  const [mentors, setMentors] = useState([]);
  const [internshipPrograms, setInternshipPrograms] = useState([]);
  const [searchParams, setSearchParams] = useSearchParams();

  const [filters, setFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    internshipProgram: parseInt(searchParams.get("internshipProgram")) || 0,
    mentor: parseInt(searchParams.get("mentor")) || 0,
    page: parseInt(searchParams.get("page")) || 1,
  });

  const [appliedFilters, setAppliedFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    internshipProgram: searchParams.get("internshipProgram") || 0,
    mentor: searchParams.get("mentor") || 0,
    page: parseInt(searchParams.get("page")) || 1,
  });

  useEffect(() => {
    const params = {};
    if (appliedFilters.keyWord) params.keyWord = appliedFilters.keyWord;
    if (appliedFilters.internshipProgram)
      params.internshipProgram = appliedFilters.internshipProgram;
    if (appliedFilters.mentor) params.mentor = appliedFilters.mentor;
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
      const [InternshipProgramData, MentorData] = await Promise.all([
        getAllInternshipProgram(),
        getAllMentor(),
      ]);
      setInternshipPrograms(InternshipProgramData), setMentors(MentorData);
    };

    fetchData();
  }, []);

  useEffect(() => {
    fetchInterns();
  }, [appliedFilters]);

  const fetchInterns = async () => {
    setLoading(true);
    const data = await getTeams({
      keyWord: appliedFilters.keyWord,
      internshipProgram: appliedFilters.internshipProgram,
      mentor: appliedFilters.mentor,
      page: appliedFilters.page,
    });
    setTeams(data.content);
    setPagination({
      pageNumber: data.pageNumber,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      hasNext: data.hasNext,
      hasPrevious: data.hasPrevious,
    });
    setLoading(false);
  };

  return (
    <>
      <MentorFilter
        filters={filters}
        handleFilterChange={(key, value) => {
          setFilters({ ...filters, [key]: value });
        }}
        handleSearch={handleSearch}
        internshipPrograms={internshipPrograms}
        mentors={mentors}
        onAddTeam={(newTeam) => {
          if (appliedFilters.page === 1) {
            setTeams((prev) => [newTeam, ...prev]);
          }
        }}
      />

      <TeamTable
        teams={teams}
        mentor={mentors}
        loading={loading}
        setTeams={setTeams}
        onAddMember={(newTeam) => {
          setTeams((prev) =>
            prev.map((team) => (team.id === newTeam.id ? newTeam : team))
          );
        }}
      />

      <Pagination
        pagination={pagination}
        filters={appliedFilters}
        changePage={(newPage) =>
          setAppliedFilters({ ...appliedFilters, page: newPage })
        }
        name={"nhóm"}
      />
    </>
  );
};

export default TeamManagement;
