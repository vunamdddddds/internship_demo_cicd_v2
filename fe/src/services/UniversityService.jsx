import { toast } from "react-toastify";
import UnversityApi from "~/api/UniversityApi";

export const getAllUniversity = async () => {
  try {
    const res = await UnversityApi.getAll();
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};
