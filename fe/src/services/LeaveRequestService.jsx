import { toast } from "react-toastify";
import LeaveRequestApi from "~/api/LeaveRequestApi";

export const getMyLeaveRequests = async ({ status }) => {
  try {
    const res = await LeaveRequestApi.getMyLeaveRequests({ status });
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const getAllLeaveRequests = async ({ keyword, type, status, page }) => {
  try {
    const res = await LeaveRequestApi.getAllLeaveRequests({
      keyword,
      type,
      status,
      page,
    });
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const createLeaveRequest = async ({
  type,
  date,
  reason,
  attachedFile,
}) => {
  try {
    const res = await LeaveRequestApi.createLeaveRequest({
      type,
      date,
      reason,
      attachedFile,
    });
    toast.success("Tạo đơn xin phép thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const cancelLeaveRequest = async (id) => {
  try {
    await LeaveRequestApi.cancelLeaveRequest(id);
    toast.success("Hủy đơn xin phép thành công.");
    return true;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const approveLeaveRequest = async (id) => {
  try {
    const res = await LeaveRequestApi.approveLeaveRequest(id);
    toast.success("Chấp nhận đơn xin phép thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const rejectLeaveRequest = async ({ id, reasonReject }) => {
  try {
    const res = await LeaveRequestApi.rejectLeaveRequest({ id, reasonReject });
    toast.success("Từ chối đơn xin phép thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};
