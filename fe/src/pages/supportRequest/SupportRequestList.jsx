import React, { useState, useEffect } from "react";
import SupportRequestService from "~/services/SupportRequestService";
import SupportRequestFilters from "./SupportRequestFilters";
import SupportRequestTable from "./SupportRequestTable";
import DetailModal from "./SupportRequestDetail"; 
import Pagination from "~/components/Pagination";

const SupportRequestList = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selected, setSelected] = useState(null);

  const [pagination, setPagination] = useState({
    pageNumber: 1,
    totalElements: 0,
    totalPages: 1,
    hasNext: false,
    hasPrevious: false,
  });

  const [filters, setFilters] = useState({
    search: "",
    status: "",
  });

  const [appliedFilters, setAppliedFilters] = useState({
    search: "",
    status: "",
    page: 1,
  });

  useEffect(() => {
    fetchRequests();
  }, [appliedFilters]);

  const fetchRequests = async () => {
    setLoading(true);
    const response = await SupportRequestService.getAll(
      { search: appliedFilters.search, status: appliedFilters.status },
      appliedFilters.page,
      10 
    );

    if (response) {
      const dataList = Array.isArray(response) ? response : (response.content || []);
      setRequests(dataList);
      
      setPagination({
        pageNumber: response.pageNumber || appliedFilters.page,
        totalElements: response.totalElements || 0,
        totalPages: response.totalPages || 1,
        hasNext: response.hasNext || false,
        hasPrevious: response.hasPrevious || false,
      });
    }
    setLoading(false);
  };

  const handleFilterChange = (key, value) => {
    setFilters({ ...filters, [key]: value });
  };

  const handleSearch = () => {
    setAppliedFilters({
      ...filters,
      page: 1,
    });
  };

  const handlePageChange = (newPage) => {
    setAppliedFilters({
      ...appliedFilters,
      page: newPage,
    });
  };

  const handleRefresh = () => {
    fetchRequests(); 
  };

  return (
    <div>
      {/* Component Lọc */}
      <SupportRequestFilters 
        filters={filters}
        handleFilterChange={handleFilterChange}
        handleSearch={handleSearch}
      />

      {/* Component Bảng */}
      <SupportRequestTable 
        requests={requests} 
        loading={loading}
        pagination={pagination}
        onViewDetail={(item) => setSelected(item)}
      />

      {/* Component Phân trang (Tái sử dụng từ InternManagement) */}
      {requests.length > 0 && (
        <Pagination
          pagination={pagination}
          filters={appliedFilters}
          changePage={handlePageChange}
          name="yêu cầu"
        />
      )}

      {/* Modal Chi tiết */}
      {selected && (
        <DetailModal
          data={selected}
          close={() => setSelected(null)}
          refresh={handleRefresh}
        />
      )}
    </div>
  );
};

export default SupportRequestList;