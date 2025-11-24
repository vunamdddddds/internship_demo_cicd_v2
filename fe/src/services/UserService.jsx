import { toast } from "react-toastify";
import UserApi from "~/api/UserApi";

export const getAllUser = async ({ keyword, role, page }) => {
  try {
    const res = await UserApi.getAll({ keyword, role, page });
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const getInfo = async () => {
  try {
    const res = await UserApi.getInfo();
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const createUser = async ({ fullName, email, phone, address, role }) => {
  try {
    const res = await UserApi.create({ fullName, email, phone, address, role });
    toast.success("Thêm người dùng thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const editUser = async ({ id, role, active }) => {
  try {
    const res = await UserApi.edit({ id, role, active });
    toast.success("Cập nhật người dùng thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const updateInfo = async ({ fullName, phone, address, avatarFile }) => {
  try {
    const formData = new FormData();
    formData.append("fullName", fullName);
    formData.append("phone", phone);
    formData.append("address", address);
    if (avatarFile) formData.append("avatarFile", avatarFile);
    const res = await UserApi.editInfo(formData);
    toast.success("Cập nhật thông tin thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};
