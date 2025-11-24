import { toast } from "react-toastify";
import MajorApi from "~/api/MajorApi";

export const getAllMajor = async () => {
  try {
    const res = await MajorApi.getAll();
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};
