import { toast } from "react-toastify";
import WorkScheduleApi from "~/api/WorkScheduleApi";

export const getSchedule = async (teamId) => {
  try {
    const res = await WorkScheduleApi.getSchedule(teamId);
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const deleteSchedule = async (id) => {
  try {
    await WorkScheduleApi.deleteSchedule(id);
    toast.success("Xóa ngày làm việc thành công");
    return id;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const createSchedule = async ({
  idTeam,
  dayOfWeek,
  timeStart,
  timeEnd,
}) => {
  try {
    const res = await WorkScheduleApi.createSchedule({
      idTeam,
      dayOfWeek,
      timeStart,
      timeEnd,
    });
    toast.success("Thêm ngày làm việc thành công");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const updateSchedule = async ({ id, timeStart, timeEnd }) => {
  try {
    const res = await WorkScheduleApi.updateSchedule({
      id,
      timeStart,
      timeEnd,
    });
    toast.success("Sửa ngày làm việc thành công");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};
