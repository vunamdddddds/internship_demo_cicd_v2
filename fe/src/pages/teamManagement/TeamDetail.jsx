import { X, Plus } from "lucide-react";
import Select from "react-select";
import { getInternNoTeam } from "~/services/InternService";
import { addMember, removeMember } from "~/services/TeamService";
import { useEffect, useState } from "react";
import Swal from "sweetalert2";

const TeamDetail = ({ team, onClose, onAddMember, setSelectedTeam }) => {
  const [internNoTeam, setInternNoTeam] = useState([]);
  const [internAddTeam, setInternAddTeam] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const interns = await getInternNoTeam(team.id);
      setInternNoTeam(interns);
    };
    fetchData();
  }, []);

  const handleAddMember = async () => {
    const newTeam = await addMember({
      teamId: team.id,
      internIds: internAddTeam,
    });
    if (newTeam) {
      onAddMember(newTeam);
      setInternAddTeam([]);
      setSelectedTeam(newTeam);
      setInternNoTeam((prev) =>
        prev.filter((intern) => !internAddTeam.includes(intern.id))
      );
    }
  };

  const handleRemoveMember = async (id, fullName, email) => {
    // Hiển thị popup xác nhận với styling tốt hơn
    const result = await Swal.fire({
      title: "Xác nhận xóa thành viên",
      html: `
        <div style="text-align: center; padding: 10px 0;">
          <p>Bạn có chắc muốn xóa thành viên này khỏi nhóm?</p>
          <div style="margin-top: 15px; padding: 12px; background: #f8f9fa; border-radius: 6px;">
            <strong>${fullName}</strong>
            <div style="color: #6c757d; font-size: 14px; margin-top: 4px;">${email}</div>
          </div>
        </div>
      `,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#dc3545",
      cancelButtonColor: "#6c757d",
      confirmButtonText: "Xóa khỏi nhóm",
      cancelButtonText: "Hủy bỏ",
      reverseButtons: true,
      customClass: {
        popup: "swal-wide",
      },
    });

    // Kiểm tra xác nhận
    if (!result.isConfirmed) return;

    // Thực hiện xóa thành viên
    const newTeam = await removeMember(id);
    if (newTeam) {
      onAddMember(newTeam);
      setSelectedTeam(newTeam);
      setInternNoTeam((prev) => [{ id, fullName, email }, ...prev]);
    }
  };

  const internNoTeamOptions = internNoTeam.map((d) => ({
    value: d.id,
    label: `${d.fullName} - ${d.email}`,
  }));

  return (
    <div className="modal-overlay">
      <div className="modal-detail">
        <div className="modal-header">
          <h3>
            Chi tiết nhóm {team.teamName} - kì thực tập:{" "}
            {team.internshipProgramName}
          </h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <div className="filter-container">
          <div className="filter-grid">
            <div className="filter-item">
              <Select
                className="custom-select"
                placeholder="Chọn thực tập sinh..."
                isMulti
                options={internNoTeamOptions}
                value={internNoTeamOptions.filter((opt) =>
                  internAddTeam.includes(opt.value)
                )}
                onChange={(selected) =>
                  setInternAddTeam(
                    selected ? selected.map((opt) => opt.value) : []
                  )
                }
              />
            </div>
            <button className="btn btn-add" onClick={handleAddMember}>
              <Plus size={18} /> Thêm thành viên
            </button>
          </div>
        </div>

        <div className="table-container">
          <table className="intern-table">
            <thead>
              <tr>
                <th>Họ và tên</th>
                <th>SĐT</th>
                <th>Email</th>
                <th>Ngành học</th>
                <th>Trường học</th>
                <th>Trạng thái</th>
                <th className="action-col"></th>
              </tr>
            </thead>
            <tbody>
              {team.members.length === 0 ? (
                <tr>
                  <td colSpan="7" className="text-center">
                    Không có dữ liệu
                  </td>
                </tr>
              ) : (
                team.members.map((intern) => (
                  <tr key={intern.id}>
                    <td>{intern.fullName}</td>
                    <td>{intern.phone || "-"}</td>
                    <td>{intern.email}</td>
                    <td>{intern.major}</td>
                    <td>{intern.university}</td>
                    <td>
                      <span
                        className={`status-badge ${intern.status.toLowerCase()}`}
                      >
                        {intern.status}
                      </span>
                    </td>
                    <td className="action-col">
                      <button
                        className="icon-btn"
                        onClick={() => {
                          handleRemoveMember(
                            intern.id,
                            intern.fullName,
                            intern.email
                          );
                        }}
                      >
                        <X size={20} style={{ color: "red" }} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default TeamDetail;
