// src/components/task_management/TeamSprintFilters.jsx
import React, { useState, useEffect } from "react";
import Select, { components } from "react-select";
import {
  Plus,
  Pencil,
  Upload,
  MessageSquare,
  FileText,
  CheckCircle,
} from "lucide-react";
import styles from "../../pages/mentor/TaskManagementPage.module.css";
import SubmitReportModal from "./SubmitReportModal";
import MentorFeedbackModal from "./MentorFeedbackModal";
import ReportViewModal from "./ReportViewModal";
import SprintReviewModal from "./SprintReviewModal";
import InternFeedbackViewModal from "./InternFeedbackViewModal";
import ReportApi from "../../api/ReportApi";

const sprintStatusColors = {
  TODO: "#4299e1",
  IN_PROGRESS: "#48bb78",
  DONE: "#a0aec0",
};

const { Option } = components;
const SprintOption = (props) => (
  <Option {...props}>
    <div style={{ display: "flex", alignItems: "center" }}>
      <span
        style={{
          backgroundColor: sprintStatusColors[props.data.status],
          borderRadius: "50%",
          width: "10px",
          height: "10px",
          marginRight: "10px",
        }}
      />
      {props.data.label}
    </div>
  </Option>
);

function TeamSprintFilters({
  teams,
  sprints,
  selectedTeamId,
  selectedSprintId,
  onTeamChange,
  onSprintChange,
  isLoadingSprints,
  onOpenCreateSprintModal,
  onOpenEditSprintModal,
  getSprintStatus,
  isInternView = false,
}) {
  const [showSubmitModal, setShowSubmitModal] = useState(false);
  const [showFeedbackModal, setShowFeedbackModal] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [showInternFeedbackModal, setShowInternFeedbackModal] = useState(false);
  const [internReport] = useState(null);
  const [mentorFeedback, setMentorFeedback] = useState(null);

  // Hàm fetch feedback mentor theo sprintId
  const fetchMentorFeedBack = (sprintId) => {
    if (!sprintId) {
      setMentorFeedback(null);
      return;
    }
    ReportApi.getMentorFeedback(sprintId)
      .then((response) => {
        setMentorFeedback(response);
      })
      .catch(() => {
        setMentorFeedback(null);
      });
  };

  // Gọi API mỗi khi sprintId thay đổi
  useEffect(() => {
    fetchMentorFeedBack(selectedSprintId);
  }, [selectedSprintId]);

  const teamOptions = teams?.map((team) => ({
    value: team.id,
    label: team.teamName,
  }));

  const sprintOptions = sprints.map((sprint) => ({
    value: sprint.id,
    label: `${sprint.name} (${sprint.startDate} to ${sprint.endDate})`,
    reportUrl: sprint.reportUrl,
    status: getSprintStatus(sprint),
  }));

  const selectedTeam = teamOptions?.find(
    (option) => option.value === selectedTeamId
  );
  const selectedSprint = sprintOptions.find(
    (option) => option.value === selectedSprintId
  );

  const isSprintFinished = selectedSprint?.status === "DONE";

  const customSelectStyles = {
    control: (provided) => ({
      ...provided,
      backgroundColor: "#edf2f7",
      border: "1px solid #e2e8f0",
      borderRadius: "6px",
      minWidth: "250px",
      boxShadow: "none",
      "&:hover": { borderColor: "#cbd5e0" },
    }),
    option: (provided, state) => ({
      ...provided,
      backgroundColor: state.isSelected
        ? "#4299e1"
        : state.isFocused
        ? "#ebf8ff"
        : "white",
      color: state.isSelected ? "white" : "#2d3748",
    }),
  };

  const Legend = () => (
    <div
      style={{
        display: "flex",
        gap: "10px",
        alignItems: "center",
        marginBottom: "8px",
      }}
    >
      {Object.entries(sprintStatusColors).map(([status, color]) => (
        <div
          key={status}
          style={{
            display: "flex",
            alignItems: "center",
            fontSize: "12px",
            color: "#4a5568",
          }}
        >
          <span
            style={{
              backgroundColor: color,
              borderRadius: "50%",
              width: "10px",
              height: "10px",
              marginRight: "5px",
            }}
          />
          {status === "TODO"
            ? "Upcoming"
            : status === "IN_PROGRESS"
            ? "Ongoing"
            : "Finished"}
        </div>
      ))}
    </div>
  );

  return (
    <div className={styles.filtersContainer}>
      {/* TEAM SELECT - Chỉ Mentor */}
      {!isInternView && (
        <div className={styles.filterGroup}>
          <label htmlFor="team-select" className={styles.filterLabel}>
            Nhóm
          </label>
          <Select
            id="team-select"
            value={selectedTeam}
            onChange={onTeamChange}
            options={teamOptions}
            styles={customSelectStyles}
            isClearable
            placeholder="-- Chọn nhóm --"
          />
        </div>
      )}

      {/* SPRINT SELECT */}
      <div
        className={styles.filterGroup}
        style={{
          flexDirection: "column",
          gap: "8px",
          alignItems: "flex-start",
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <label htmlFor="sprint-select" className={styles.filterLabel}>
            Sprint
          </label>
          <Legend />
        </div>
        <Select
          id="sprint-select"
          value={selectedSprint}
          onChange={(option) => {
            onSprintChange(option);
            fetchMentorFeedBack(option?.value);
          }}
          options={sprintOptions}
          components={{ Option: SprintOption }}
          styles={customSelectStyles}
          isClearable
          isLoading={isLoadingSprints}
          isDisabled={!isInternView && !selectedTeamId}
          placeholder="-- Chọn Sprint --"
        />
      </div>

      {/* NÚT MENTOR */}
      <div style={{ alignSelf: "flex-end" }}>
        <button
          onClick={onOpenCreateSprintModal}
          disabled={!selectedTeamId}
          className={styles.actionButton}
        >
          <Plus size={16} /> Tạo Sprint
        </button>
      </div>
      {!isInternView && selectedSprintId && (
        <>
          <div style={{ alignSelf: "flex-end" }}>
            <button
              onClick={onOpenEditSprintModal}
              disabled={!selectedSprintId}
              className={styles.actionButton}
            >
              <Pencil size={16} /> Chỉnh sửa Sprint
            </button>
          </div>

          <div style={{ alignSelf: "flex-end" }}>
            <button
              onClick={() => setShowReviewModal(true)}
              disabled={!isSprintFinished}
              className={styles.actionButton}
              title="Review sprint đã kết thúc"
            >
              <CheckCircle size={16} /> Đánh giá Sprint
            </button>
          </div>

          <div style={{ alignSelf: "flex-end" }}>
            <button
              onClick={() => window.open(selectedSprint?.reportUrl, "_blank")}
              disabled={!internReport}
              className={`${styles.actionButton} ${
                !internReport ? "opacity-50 cursor-not-allowed" : ""
              }`}
              title={!internReport ? "Chưa có báo cáo" : "Xem báo cáo đã nộp"}
            >
              <FileText size={16} /> Báo Cáo
            </button>
          </div>

          <div style={{ alignSelf: "flex-end" }}>
            <button
              onClick={() => setShowFeedbackModal(true)}
              //disabled={!internReport}
              className={styles.actionButton}
              title={
                !internReport ? "Cần có báo cáo để phản hồi" : "Gửi phản hồi"
              }
            >
              <MessageSquare size={16} /> Phản Hồi
            </button>
          </div>
        </>
      )}

      {/* NÚT INTERN */}
      {isInternView && selectedSprintId && (
        <div style={{ alignSelf: "flex-end" }}>
          <button
            onClick={() => setShowSubmitModal(true)}
            //disabled={!isSprintFinished}
            className={styles.actionButton}
            title={
              isSprintFinished
                ? "Nộp báo cáo sprint"
                : "Chỉ nộp được khi sprint kết thúc"
            }
          >
            <Upload size={16} /> Nộp Báo Cáo
          </button>
        </div>
      )}

      {isInternView && selectedSprintId && (
        <div style={{ alignSelf: "flex-end", marginLeft: "8px" }}>
          <button
            onClick={() => setShowInternFeedbackModal(true)}
            //disabled={!mentorFeedback}
            className={styles.actionButton}
            title="Xem phản hồi từ Mentor"
          >
            <MessageSquare size={16} /> Xem Phản Hồi
          </button>
        </div>
      )}

      {/* MODALS */}
      <SubmitReportModal
        isOpen={showSubmitModal}
        onClose={() => setShowSubmitModal(false)}
        sprintId={selectedSprintId}
        onSubmitSuccess={() => {
          setShowSubmitModal(false);
          // Có thể refresh báo cáo ở đây nếu cần
        }}
      />

      <ReportViewModal
        isOpen={showReportModal}
        onClose={() => setShowReportModal(false)}
        report={internReport}
      />

      <MentorFeedbackModal
        isOpen={showFeedbackModal}
        onClose={() => setShowFeedbackModal(false)}
        sprintId={selectedSprintId}
        teamName={selectedTeam?.label || "Team"}
        sprintName={selectedSprint?.label?.split(" (")[0] || "Sprint"}
        onFeedbackSuccess={() => setShowFeedbackModal(false)}
      />

      <SprintReviewModal
        isOpen={showReviewModal}
        onClose={() => setShowReviewModal(false)}
        sprint={selectedSprint}
        report={internReport}
        feedback={mentorFeedback}
      />

      <InternFeedbackViewModal
        isOpen={showInternFeedbackModal}
        onClose={() => setShowInternFeedbackModal(false)}
        teamName={selectedTeam?.label || "Team"}
        sprintName={selectedSprint?.label?.split(" (")[0] || "Sprint"}
        feedback={mentorFeedback}
      />
    </div>
  );
}

export default TeamSprintFilters;
