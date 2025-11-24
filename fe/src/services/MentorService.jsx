import { toast } from "react-toastify";
import MentorApi from "~/api/MentorApi";

export const getMentors = async ({ keyword, department, page }) => {
  try {
    const res = await MentorApi.getAll({ keyword, department, page });
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const getAllMentor = async () => {
  try {
    const res = await MentorApi.getAllMentor();
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const createMentor = async ({
  fullName,
  email,
  phone,
  address,
  departmentId,
}) => {
  try {
    const res = await MentorApi.create({
      fullName,
      email,
      phone,
      address,
      departmentId,
    });
    toast.success("Thêm mentor thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const editMentor = async ({ id, departmentId }) => {
  try {
    const res = await MentorApi.edit({ id, departmentId });
    toast.success("Cập nhật mentor thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};
