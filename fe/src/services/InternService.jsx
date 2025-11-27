import { toast } from "react-toastify";
import InternApi from "~/api/internApi";

export const getAllIntern = async ({
  keyWord,
  universityId,
  majorId,
  page,
}) => {
  const uId = universityId === 0 ? null : universityId;
  const mId = majorId === 0 ? null : majorId;
  try {
    const res = await InternApi.getAll({
      keyWord,
      universityId: uId,
      majorId: mId,
      page,
    });
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const getAllInternsForSelection = async () => {
  try {
    const res = await InternApi.getAll({ size: 1000 }); // Fetch all interns
    if (res && res.content) {
      return res.content.map(intern => ({
        value: intern.id,
        label: `${intern.fullName} (${intern.email})`
      }));
    }
    return [];
  } catch (err) {
    toast.error("Không thể tải danh sách thực tập sinh.");
    console.error("Error fetching interns for selection:", err);
    return [];
  }
};


export const createIntern = async ({
  fullName,
  email,
  phone,
  address,
  majorId,
  universityId,
  internshipProgramId,
}) => {
  try {
    const res = await InternApi.create({
      fullName,
      email,
      phone,
      address,
      universityId,
      majorId,
      internshipProgramId,
    });
    toast.success("Thêm thực tập sinh thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const updateIntern = async ({ status, universityId, majorId, id }) => {
  try {
    const res = await InternApi.edit({ id, status, universityId, majorId });
    toast.success("Sửa thông tin thực tập sinh thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const getInternNoTeam = async (teamId) => {
  try {
    const res = await InternApi.getInternNoTeam(teamId);
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};
