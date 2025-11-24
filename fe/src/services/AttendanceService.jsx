import { toast } from "react-toastify";
import AttendanceApi from "~/api/AttendanceApi";

export const getMyCalendar = async () => {
  try {
    const res = await AttendanceApi.getMyCalendar();
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const getTeamCalendar = async (teamId) => {
  try {
    if (teamId == null) return;
    const res = await AttendanceApi.getTeamCalendar(teamId);
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const checkIn = async () => {
  try {
    const res = await AttendanceApi.checkIn();
    toast.success("Check-in thành công");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const checkOut = async () => {
  try {
    const res = await AttendanceApi.checkOut();
    toast.success("Check-out thành công");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};
