import InternshipProgramApi from "~/api/InternshipProgramApi";
import { toast } from "react-toastify";

export const getAllInternshipProgram = async () => {
  try {
    const res = await InternshipProgramApi.getAll();
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const getInternshipProgram = async ({ keyword, department, page }) => {
  try {
    const res = await InternshipProgramApi.getInternshipProgram({
      keyword,
      department,
      page,
    });
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const createInternshipProgram = async ({
  name,
  endPublishedTime,
  endReviewingTime,
  timeStart,
  departmentId,
  draft,
}) => {
  try {
    const res = await InternshipProgramApi.create({
      name,
      endPublishedTime,
      endReviewingTime,
      timeStart,
      departmentId,
      draft,
    });
    toast.success("Thêm kì thực tập thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const editInternshipProgram = async ({
  id,
  name,
  endPublishedTime,
  endReviewingTime,
  timeStart,
}) => {
  try {
    const res = await InternshipProgramApi.update({
      id,
      name,
      endPublishedTime,
      endReviewingTime,
      timeStart,
    });
    toast.success("Sửa kì thực tập thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};
