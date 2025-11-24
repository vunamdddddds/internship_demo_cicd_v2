import { useState, useEffect } from "react";
import { getDiligenceReport } from "~/services/DiligenceHrService";
import DiligenceFilters from "./DiligenceFilters";
import InternTable from "./InternTable";

const DiligenceHr = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState({ teamId: null });

  const fetchData = async () => {
    setLoading(true);
    const result = await getDiligenceReport({ teamId: filters.teamId });
    setData(result);
    setLoading(false);
  };
  // gọi API khi teamId thay đổi
  useEffect(() => {
    fetchData();
  }, [filters.teamId]);

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="page-title">Quản lý chuyên cần</h1>
      </div>

      <DiligenceFilters
        filters={filters}
        onChange={(key, value) =>
          setFilters((prev) => ({ ...prev, [key]: value }))
        }
      />

      {loading ? (
        <div className="text-center py-10">Đang tải...</div>
      ) : (
        <InternTable data={data} />
      )}
    </div>
  );
};

export default DiligenceHr;
