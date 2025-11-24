import Card from "./components/Card";
import "./LeaveRequest.css";
import { PlusCircle } from "lucide-react";
import LeaveRequestTable from "./LeaveRequestTable";
import { useState, useEffect } from "react";
import { getMyLeaveRequests } from "~/services/LeaveRequestService";
import CreateForm from "./CreateForm";

const LeaveRequest = () => {
  const [leaveRequests, setLeaveRequests] = useState({});
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState("All");
  const [showForm, setShowForm] = useState(false);

  const changeStatus = (newStatus) => {
    setStatus(newStatus);
  };

  const typeOptions = [
    { value: "ON_LEAVE", label: "Xin nghỉ phép" },
    { value: "LATE", label: "Xin đi muộn" },
    { value: "EARLY_LEAVE", label: "Xin về sớm" },
  ];

  const onAddLeaveRequest = (data) => {
    setLeaveRequests((prev) => ({
      ...prev,
      leaveApplications: [data, ...(prev.leaveApplications || [])],
      countLeaveApplication: (prev.countLeaveApplication || 0) + 1,
      countPendingApprove: (prev.countPendingApprove || 0) + 1,
    }));
  };

  const onDeleteLeaveRequest = (id) => {
    setLeaveRequests((prev) => ({
      ...prev,
      leaveApplications: prev.leaveApplications.filter(
        (item) => item.id !== id
      ),
      countLeaveApplication: (prev.countLeaveApplication || 0) - 1,
      countPendingApprove: (prev.countPendingApprove || 0) - 1,
    }));
  };

  useEffect(() => {
    const fetchLeaveRequests = async () => {
      setLoading(true);
      const response = await getMyLeaveRequests({ status });
      setLeaveRequests(response);
      setLoading(false);
    };
    fetchLeaveRequests();
  }, [status]);

  return (
    <div className="leave-container">
      <div className="leave-stats">
        <Card
          name={"Tổng đơn"}
          quantity={leaveRequests.countLeaveApplication}
          changeStatus={() => changeStatus("ALL")}
        />
        <Card
          name={"Chờ duyệt"}
          quantity={leaveRequests.countPendingApprove}
          changeStatus={() => changeStatus("PENDING")}
        />
        <Card
          name={"Đã duyệt"}
          quantity={leaveRequests.countApprove}
          color="green"
          changeStatus={() => changeStatus("APPROVED")}
        />
        <Card
          name={"Từ chối"}
          quantity={leaveRequests.countReject}
          color="red"
          changeStatus={() => changeStatus("REJECTED")}
        />
      </div>

      <div className="action-bar">
        <button className="btn btn-add" onClick={() => setShowForm(true)}>
          <PlusCircle size={18} />
          Tạo đơn mới
        </button>
      </div>

      <LeaveRequestTable
        leaveRequests={leaveRequests.leaveApplications}
        loading={loading}
        typeOptions={typeOptions}
        onDeleteLeaveRequest={onDeleteLeaveRequest}
      />

      {showForm && (
        <CreateForm
          onClose={() => setShowForm(false)}
          typeOptions={typeOptions}
          onAddLeaveRequest={onAddLeaveRequest}
        />
      )}
    </div>
  );
};

export default LeaveRequest;
