import { toast } from "react-toastify";
import TeamApi from "~/api/TeamApi";

export const getTeams = async ({
  keyword,
  internshipProgram,
  mentor,
  page,
}) => {
  try {
    const iId = internshipProgram === 0 ? null : internshipProgram;
    const mId = mentor === 0 ? null : mentor;
    const res = await TeamApi.getTeams({
      keyword,
      internshipProgram: iId,
      mentor: mId,
      page,
    });
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const getAll = async () => {
  try {
    const res = await TeamApi.getAll();
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const createTeam = async ({ name, internshipProgramId, mentorId }) => {
  try {
    const mId = mentorId ? mentorId : null;
    const iId = internshipProgramId ? internshipProgramId : null;
    const res = await TeamApi.create({
      name,
      internshipProgramId: iId,
      mentorId: mId,
    });
    toast.success("Thêm nhóm thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const editTeam = async ({ name, mentorId, id }) => {
  try {
    const res = await TeamApi.edit({
      name,
      id,
      mentorId,
    });
    toast.success("Sửa nhóm thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const addMember = async ({ teamId, internIds }) => {
  try {
    const res = await TeamApi.addMember({
      teamId,
      internIds,
    });
    toast.success("Thêm thành viên thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const removeMember = async (id) => {
  try {
    const res = await TeamApi.removeMember(id);
    toast.success("Xóa thành viên thành công.");
    return res;
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};
