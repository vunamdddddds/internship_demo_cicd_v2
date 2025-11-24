import UserFilter from "./UserFilters";
import UserTable from "./UserTable";
import { getAllUser, editUser } from "~/services/UserService";
import React, { useState, useEffect } from "react";
import Pagination from "~/components/Pagination";
import { useSearchParams } from "react-router-dom";

const UserManagement = () => {
  const [loading, setLoading] = useState(false);
  const [users, setUsers] = useState([]);
  const [searchParams, setSearchParams] = useSearchParams();
  const [filters, setFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    role: searchParams.get("role") || "",
    page: parseInt(searchParams.get("page")) || 1,
  });

  const [appliedFilters, setAppliedFilters] = useState({
    keyWord: searchParams.get("keyWord") || "",
    role: searchParams.get("role") || "",
    page: parseInt(searchParams.get("page")) || 1,
  });

  useEffect(() => {
    const params = {};
    if (appliedFilters.keyWord) params.keyWord = appliedFilters.keyWord;
    if (appliedFilters.role) params.role = appliedFilters.role;
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
    fetchUser();
  }, [appliedFilters]);

  const fetchUser = async () => {
    setLoading(true);
    const data = await getAllUser({
      keyword: appliedFilters.keyWord,
      role: appliedFilters.role,
      page: appliedFilters.page,
    });
    setUsers(data.content);
    setPagination({
      pageNumber: data.pageNumber,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      hasNext: data.hasNext,
      hasPrevious: data.hasPrevious,
    });
    setLoading(false);
  };

  const updateUser = async ({ id, role, active }) => {
    const data = await editUser({ id, role, active });
    if (data) {
      setUsers((prev) =>
        prev.map((user) => (user.id === data.id ? data : user))
      );
    }
  };

  const handleSearch = () => {
    setAppliedFilters({ ...filters, page: 1 });
  };

  const roleOptions = [
    { value: "", label: "Tất cả" },
    { value: "ADMIN", label: "ADMIN" },
    { value: "HR", label: "HR" },
    { value: "MENTOR", label: "MENTOR" },
    { value: "INTERN", label: "INTERN" },
    { value: "VISITOR", label: "VISITOR" },
  ];
  return (
    <>
      <UserFilter
        filters={filters}
        handleFilterChange={(key, value) => {
          setFilters({ ...filters, [key]: value });
        }}
        handleSearch={handleSearch}
        roleOptions={roleOptions}
        onAddUser={(newUser) => {
          if (appliedFilters.page === 1) {
            setUsers((prev) => [newUser, ...prev]);
          }
        }}
      />
      <UserTable
        users={users}
        loading={loading}
        editUser={updateUser}
        roleOptions={roleOptions.filter((opt) => opt.value !== "")}
      />

      <Pagination
        pagination={pagination}
        filters={appliedFilters}
        changePage={(newPage) =>
          setAppliedFilters({ ...appliedFilters, page: newPage })
        }
        name={"người dùng"}
      />
    </>
  );
};

export default UserManagement;
