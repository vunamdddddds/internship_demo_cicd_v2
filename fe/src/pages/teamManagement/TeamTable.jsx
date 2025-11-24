import React from "react";
import { Pencil, FileText } from "lucide-react";
import { useState } from "react";
import TeamDetail from "./TeamDetail";
import TeamEditModal from "./EditTeamForm";
import { editTeam } from "~/services/TeamService";

const TeamTable = ({ teams, loading, onAddMember, mentor, setTeams }) => {
  const [showForm, setShowForm] = useState(false);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [showEditForm, setShowEditForm] = useState(false);
  const [formData, setFormData] = useState({
    id: null,
    name: "",
    mentorId: 0,
  });

  const handleChange = (key, value) => {
    setFormData({ ...formData, [key]: value });
  };

  const handleEdit = (team) => {
    const selectedMentor = mentor.find((m) => m.fullName === team.mentorName);
    setFormData({
      id: team.id,
      name: team.teamName,
      mentorId: selectedMentor ? selectedMentor.id : 0,
    });
    setShowEditForm(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const updated = await editTeam(formData);
    if (updated) {
      setTeams((prev) =>
        prev.map((team) => (team.id === updated.id ? updated : team))
      );
      setShowEditForm(false);
    }
  };

  return (
    <>
      <div className="table-container">
        <table className="intern-table">
          <thead>
            <tr>
              <th>Tên nhóm</th>
              <th>Tên kì thực tập</th>
              <th>tên mentor</th>
              <th>Số thành viên</th>
              <th className="action-col"></th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="6" className="text-center">
                  Đang tải...
                </td>
              </tr>
            ) : teams.length === 0 ? (
              <tr>
                <td colSpan="6" className="text-center">
                  Không có dữ liệu
                </td>
              </tr>
            ) : (
              teams.map((team) => (
                <tr key={team.id}>
                  <td>{team.teamName}</td>
                  <td>{team.internshipProgramName}</td>
                  <td>{team.mentorName}</td>
                  <td>{team.size}</td>
                  <td className="action-col">
                    <button
                      className="icon-btn"
                      onClick={() => handleEdit(team)}
                    >
                      <Pencil size={15} />
                    </button>

                    <button
                      className="icon-btn"
                      onClick={() => {
                        setSelectedTeam(team);
                        setShowForm(true);
                      }}
                    >
                      <FileText size={15} />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showForm && (
        <TeamDetail
          team={selectedTeam}
          onClose={() => setShowForm(false)}
          onAddMember={onAddMember}
          setSelectedTeam={setSelectedTeam}
        />
      )}

      {showEditForm && (
        <TeamEditModal
          formData={formData}
          onChange={handleChange}
          onSubmit={handleSubmit}
          onClose={() => setShowEditForm(false)}
          mentor={mentor}
        />
      )}
    </>
  );
};

export default TeamTable;
