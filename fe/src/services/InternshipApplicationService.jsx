import InternshipApplicationApi from "~/api/InternshipApplicationApi";
import { toast } from "react-toastify";

export const getAllApplication = async ({
  internshipTerm,
  university,
  major,
  keyword,
  status,
  page,
}) => {
  const uId = university === 0 ? null : university;
  const mId = major === 0 ? null : major;
  const iId = internshipTerm === 0 ? null : internshipTerm;
  try {
    const res = await InternshipApplicationApi.getAll({
      internshipTerm: iId,
      university: uId,
      major: mId,
      keyword,
      status,
      page,
    });
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const updateStatus = async ({ idList, action }) => {
  try {
    const payload = {
      applicationIds: Array.from(idList),
      action: action,
    };
    await InternshipApplicationApi.updateStatus(payload);
    toast.success(
      action === "approve" ? "Duyệt thành công!" : "Từ chối thành công!"
    );
    return true;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
    return false;
  }
};
