import { toast } from "react-toastify";
import DepartmentApi from "~/api/DepartmentApi";

export const getAllDepartment = async () => {
  try {
    const res = await DepartmentApi.getAll();
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};
