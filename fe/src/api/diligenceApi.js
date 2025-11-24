import AxiosClient from "./AxiosClient";

const leaveRequestApi = {
  // For Intern: Create a new leave request
  createLeaveRequest: (formData) => {
    const url = "/leaveRequests";
    return AxiosClient.post(url, formData, {
      withAuth: true,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  // For Intern: Get all of their own leave requests
  getMyLeaveRequests: () => {
    const url = "/leaveRequests/me";
    return AxiosClient.get(url, { withAuth: true });
  },

  // For HR: Get all leave requests with filters and pagination
  getAllLeaveRequests: (params) => {
    const url = "/leaveRequests";
    return AxiosClient.get(url, { params, withAuth: true });
  },

  // For HR & Intern: View a specific leave application
  getLeaveRequestById: (id) => {
    const url = `/leaveRequests/${id}`;
    return AxiosClient.get(url, { withAuth: true });
  },

  // For Intern: Cancel a leave application
  cancelLeaveRequest: (id) => {
    const url = `/leaveRequests/${id}`;
    return AxiosClient.delete(url, { withAuth: true });
  },

  // For HR: Approve a leave application
  approveLeaveRequest: (id) => {
    const url = `/leaveRequests/approve/${id}`;
    return AxiosClient.patch(url, {}, { withAuth: true });
  },

  // For HR: Reject a leave application
  rejectLeaveRequest: (data) => {
    const url = "/leaveRequests/reject";
    return AxiosClient.patch(url, data, { withAuth: true });
  },
};

export default leaveRequestApi;