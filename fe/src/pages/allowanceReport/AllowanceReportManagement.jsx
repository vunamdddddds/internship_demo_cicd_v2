import React, { useEffect, useState, useCallback } from "react";
import AllowanceReportTable from "./AllowanceReportTable";
import AllowanceReportApi from "../../api/AllowanceReportApi";
import AllowanceDetailsModal from "./AllowanceDetailsModal";
import Pagination from "../../components/Pagination";

const AllowanceReportManagement = () => {
  const [reports, setReports] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // State for details modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalData, setModalData] = useState([]);
  const [isDetailLoading, setIsDetailLoading] = useState(false);
  const [selectedMonth, setSelectedMonth] = useState(null);

  // State for pagination
  const [paginationInfo, setPaginationInfo] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const PAGE_SIZE = 10;

  const fetchReports = useCallback(async (page) => {
    setIsLoading(true);
    try {
      // API page is 0-based, UI page is 1-based
      const response = await AllowanceReportApi.getAllowanceReports(page - 1, PAGE_SIZE);
      const { content, totalPages, hasNext, hasPrevious, totalElements, number } = response;
      setReports(content);
      setPaginationInfo({ totalPages, hasNext, hasPrevious, totalElements });
      setCurrentPage(number + 1); // Update current page from 0-based to 1-based
    } catch (err) {
      setError("Failed to fetch report history.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchReports(currentPage);
  }, [fetchReports, currentPage]);

  const handleViewDetails = async (month) => {
    setSelectedMonth(month);
    setIsModalOpen(true);
    setIsDetailLoading(true);
    try {
      const response = await AllowanceReportApi.getAllowanceDetails(month);
      setModalData(response.data);
    } catch (err) {
      console.error("Failed to fetch allowance details:", err);
      setModalData([]); // Clear previous data on error
    } finally {
      setIsDetailLoading(false);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setModalData([]);
    setSelectedMonth(null);
  };
  
  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  return (
    <div style={{ padding: "2rem" }}>
      <h1 style={{ borderBottom: "2px solid #eee", paddingBottom: "1rem", marginBottom: "2rem" }}>
        Quản lý Báo cáo Phụ cấp
      </h1>
      

      {error && <p style={{ color: "red" }}>{error}</p>}
      
      <AllowanceReportTable 
        reports={reports} 
        isLoading={isLoading} 
        onViewDetails={handleViewDetails}
      />

      {paginationInfo && paginationInfo.totalElements > 0 && (
        <Pagination
          pagination={paginationInfo}
          currentPage={currentPage}
          changePage={handlePageChange}
          name="báo cáo"
        />
      )}

      <AllowanceDetailsModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        data={modalData}
        isLoading={isDetailLoading}
        month={selectedMonth}
      />
    </div>
  );
};

export default AllowanceReportManagement;
