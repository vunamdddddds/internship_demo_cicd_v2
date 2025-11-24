import { toast } from "react-toastify";
import AxiosClient from "~/api/AxiosClient";
import AllowanceApi from "~/api/AllowanceApi";

export const getAllowances = async (filters) => {
  try {
    const res = await AllowanceApi.getAllowances(filters);
    return res;
  } catch (err) {
    toast.error(
      err.response ? err.response.data.message : "Không thể kết nối đến server"
    );
  }
};

export const transferAllowance = async (id) => {
  try {
    const res = await AllowanceApi.transferAllowance(id);
    toast.success("Đã xác nhận chuyển tiền thành công");
    return res;
  } catch (err) {
    toast.error(
      err.response ? err.response.data.message : "Không thể kết nối đến server"
    );
  }
};

export const createAllowance = async (data) => {
  try {
    const res = await AllowanceApi.createAllowance(data);
    toast.success("Tạo khoản phụ cấp thành công");
    return res;
  } catch (err) {
    toast.error(
      err.response ? err.response.data.message : "Không thể kết nối đến server"
    );
    throw err;
  }
};

export const cancelAllowance = async (id) => {
  try {
    const res = await AllowanceApi.cancelAllowance(id);
    toast.success("Đã hủy khoản phụ cấp thành công");
    return res;
  } catch (err) {
    toast.error(
      err.response ? err.response.data.message : "Không thể kết nối đến server"
    );
    throw err;
  }
};
