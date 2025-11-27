import React, { useState } from "react";
import AllowanceReportApi from "../../api/AllowanceReportApi";
import fileDownload from "js-file-download";

const DetailedReportDownloader = () => {
  const [selectedMonth, setSelectedMonth] = useState(
    new Date().toISOString().slice(0, 7)
  );
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleDownload = async () => {
    if (!selectedMonth) {
      setError("Please select a month.");
      return;
    }
    setIsLoading(true);
    setError(null);
    try {
      const response = await AllowanceReportApi.downloadDetailedReport(
        selectedMonth
      );
      const fileName = `bao_cao_chi_tiet_phu_cap_thang_${selectedMonth}.xlsx`;
      fileDownload(response.data, fileName);
    } catch (err) {
      setError("Failed to download the report. Please try again.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ padding: "1rem", border: "1px solid #ccc", borderRadius: "8px", marginBottom: "1.5rem" }}>
     
    </div>
  );
};

export default DetailedReportDownloader;
