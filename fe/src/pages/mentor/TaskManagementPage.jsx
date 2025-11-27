import React, { useState, useEffect, useCallback } from "react";
import TeamApi from "../../api/TeamApi";
import SprintApi from "../../api/SprintApi";
import TaskApi from "../../api/TaskApi";
import TeamSprintFilters from "../task_management/TeamSprintFilters";
import KanbanBoard from "../task_management/KanbanBoard";
import CreateTaskModal from "../task_management/CreateTaskModal";
import TaskDetailModal from "../task_management/TaskDetailModal";
import ConfirmationModal from "../task_management/ConfirmationModal";
import CreateSprintModal from "../task_management/CreateSprintModal";
import EditSprintModal from "../task_management/EditSprintModal";
import SprintReviewModal from "../task_management/SprintReviewModal";
import styles from "./TaskManagementPage.module.css";

function TaskManagementPage() {
  // Data states
  const [teams, setTeams] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [sprintTasks, setSprintTasks] = useState([]);
  const [backlogTasks, setBacklogTasks] = useState([]);
  const [assignableMembers, setAssignableMembers] = useState([]); // New state for assignable members

  // Selection states
  const [selectedTeamId, setSelectedTeamId] = useState("");
  const [selectedSprintId, setSelectedSprintId] = useState("");

  // Loading states
  const [ setIsLoadingTeams] = useState(true);
  const [isLoadingSprints, setIsLoadingSprints] = useState(false);
  const [isLoadingTasks, setIsLoadingTasks] = useState(false);

  // Modal states
  const [isCreateTaskModalOpen, setIsCreateTaskModalOpen] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isDeleteConfirmationOpen, setIsDeleteConfirmationOpen] = useState(false);
  const [isCreateSprintModalOpen, setIsCreateSprintModalOpen] = useState(false);
  const [isEditSprintModalOpen, setIsEditSprintModalOpen] = useState(false);
  const [isReviewModalOpen, setIsReviewModalOpen] = useState(false);
  
  // Data for modals
  const [itemToDelete, setItemToDelete] = useState({ id: null, type: null });
  const [selectedTask, setSelectedTask] = useState(null);
  const [initialStatusForCreate, setInitialStatusForCreate] = useState("TODO");
  const [tasksToReview, setTasksToReview] = useState([]);

  // --- Modal Handlers (Task) ---
  const handleOpenCreateTaskModal = async (status) => { // Made async
    if (!selectedSprintId && status !== 'BACKLOG') {
      alert("Vui lòng chọn một sprint để tạo nhiệm vụ.");
      return;
    }
    setInitialStatusForCreate(status);
    setAssignableMembers([]); // Clear previous members

    // Fetch members for the currently selected team/sprint context
    if (selectedSprintId) {
      const sprint = sprints.find(s => s.id.toString() === selectedSprintId);
      if (sprint && sprint.teamId) {
        try {
          const response = await TeamApi.getTeamById(sprint.teamId);
          setAssignableMembers(response.data.members || []);
        } catch (error) {
          console.error("Không thể lấy thành viên nhóm để tạo nhiệm vụ:", error);
          setAssignableMembers([]);
        }
      }
    } else if (selectedTeamId) { // If no sprint selected, but team is, try to get members for the team
        try {
            const response = await TeamApi.getTeamById(selectedTeamId);
            setAssignableMembers(response.data.members || []);
        } catch (error) {
            console.error("Không thể lấy thành viên nhóm để tạo tác vụ (không có sprint):", error);
            setAssignableMembers([]);
        }
    }
    setIsCreateTaskModalOpen(true);
  };
  const handleCloseCreateTaskModal = () => setIsCreateTaskModalOpen(false);

  const handleOpenDetailModal = async (task) => { // Made async
    setSelectedTask(task);
    setAssignableMembers([]); // Clear previous members

    if (task.sprint_Id) { // If task is in a sprint
      const sprint = sprints.find(s => s.id === task.sprint_Id);
      if (sprint && sprint.teamId) {
        try {
          const response = await TeamApi.getTeamById(sprint.teamId);
          setAssignableMembers(response.members || []); // Assuming response.data.members
        } catch (error) {
          console.error("Failed to fetch team members for task:", error);
          setAssignableMembers([]);
        }
      }
    } else if (selectedTeamId) { // If task is in backlog, use the currently selected team
        try {
            const response = await TeamApi.getTeamById(selectedTeamId);
            setAssignableMembers(response.data.members || []);
        } catch (error) {
            console.error("Không thể lấy thành viên nhóm cho nhiệm vụ tồn đọng:", error);
            setAssignableMembers([]);
        }
    }
    setIsDetailModalOpen(true);
  };
  const handleCloseDetailModal = () => setIsDetailModalOpen(false);

  const handleOpenDeleteTaskModal = (taskId) => {
    setItemToDelete({ id: taskId, type: 'task' });
    setIsDeleteConfirmationOpen(true);
  };

  // --- Modal Handlers (Sprint) ---
  const handleOpenCreateSprintModal = () => setIsCreateSprintModalOpen(true);
  const handleCloseCreateSprintModal = () => setIsCreateSprintModalOpen(false);

  const handleOpenEditSprintModal = () => {
    if (selectedSprintId) {
      setIsEditSprintModalOpen(true);
    }
  };
  const handleCloseEditSprintModal = () => setIsEditSprintModalOpen(false);

  const handleOpenDeleteSprintModal = (sprintId) => {
    setItemToDelete({ id: sprintId, type: 'sprint' });
    setIsDeleteConfirmationOpen(true);
  };

  // --- Modal Handlers (Review) ---
  const handleOpenReviewModal = () => {
    const unfinished = sprintTasks.filter(t => t.status !== 'DONE' && t.status !== 'CANCELLED');
    setTasksToReview(unfinished);
    setIsReviewModalOpen(true);
  };
  const handleCloseReviewModal = () => setIsReviewModalOpen(false);

  // --- Generic Confirmation Modal Handlers ---
  const handleCloseDeleteConfirmation = () => {
    setItemToDelete({ id: null, type: null });
    setIsDeleteConfirmationOpen(false);
  };

  // --- API Calls ---
  const fetchSprintsAndBacklog = useCallback(async () => {
    if (!selectedTeamId) {
      setSprints([]);
      setBacklogTasks([]);
      return;
    }
    setIsLoadingSprints(true);
    setIsLoadingTasks(true);
    try {
      const [sprintsRes, tasksRes] = await Promise.all([
        SprintApi.getSprintsByTeam(selectedTeamId),
        TaskApi.getTasksByTeam(selectedTeamId)
      ]);
      setSprints(sprintsRes || []);
      setBacklogTasks((tasksRes|| []).filter(task => !task.sprint_Id));
    } catch (error) {
      console.error("Không thể lấy các sprint và backlog", error);
      setSprints([]);
      setBacklogTasks([]);
    } finally {
      setIsLoadingSprints(false);
      setIsLoadingTasks(false);
    }
  }, [selectedTeamId]);

  const fetchTasksForSprint = useCallback(async () => {
    if (!selectedSprintId) {
      setSprintTasks([]);
      return;
    }
    setIsLoadingTasks(true);
    try {
      const response = await TaskApi.getTasksBySprint(selectedSprintId);
      setSprintTasks(response || []);
    } catch (error) {
      console.error("Không thể lấy các task cho sprint", error);
      setSprintTasks([]);
    } finally {
      setIsLoadingTasks(false);
    }
  }, [selectedSprintId]);

  const refreshKanbanData = () => {
    if (selectedTeamId) fetchSprintsAndBacklog();
    if (selectedSprintId) fetchTasksForSprint();
  };
  
  const handleConfirmDelete = async () => {
    if (!itemToDelete.id || !itemToDelete.type) return;

    try {
      if (itemToDelete.type === 'task') {
        await TaskApi.deleteTask(itemToDelete.id);
      } else if (itemToDelete.type === 'sprint') {
        await SprintApi.delete(itemToDelete.id);
        setSelectedSprintId(""); // Deselect sprint after deleting it
      }
      refreshKanbanData(); // Refresh data after deletion
    } catch (error) {
      console.error(`Không thể xóa ${itemToDelete.type}:`, error);
      alert(`Không thể xóa ${itemToDelete.type}. Vui lòng thử lại.`);
    } finally {
      handleCloseDeleteConfirmation();
    }
  };

  useEffect(() => {
    const fetchTeams = async () => {
      setIsLoadingTeams(true);
      try {
        const response = await TeamApi.getMyTeams();
        setTeams(response || []); // User confirmed this is correct
      } catch (error) {
        console.error("Không thể lấy nhóm:", error);
        setTeams([]);
      } finally {
        setIsLoadingTeams(false);
      }
    };
    fetchTeams();
  }, []);

  useEffect(() => {
    setSelectedSprintId("");
    setSprintTasks([]);
    fetchSprintsAndBacklog();
  }, [fetchSprintsAndBacklog]);

  useEffect(() => {
    fetchTasksForSprint();
  }, [fetchTasksForSprint]);

  const sprintToEdit = sprints.find(sprint => sprint.id === selectedSprintId);

  const handleTeamChange = (selectedOption) => {
    const newTeamId = selectedOption ? selectedOption.value : "";
    setSelectedTeamId(newTeamId);
  };

  const getSprintStatus = (sprint) => {
    const today = new Date();
    const startDate = new Date(sprint.startDate);
    const endDate = new Date(sprint.endDate);
    today.setHours(0, 0, 0, 0);
    
    if (today < startDate) return "TODO";
    if (today > endDate) return "DONE";
    return "IN_PROGRESS";
  };

  // --- Column Generation for Kanban Board ---
  const columnsForBoard = {
    BACKLOG: { name: "Backlog", items: backlogTasks.filter(t => t.status !== 'CANCELLED') },
    TODO: { name: "To Do", items: sprintTasks.filter(t => t.status === 'TODO') },
    IN_PROGRESS: { name: "In Progress", items: sprintTasks.filter(t => t.status === 'IN_PROGRESS') },
    DONE: { name: "Done", items: sprintTasks.filter(t => t.status === 'DONE') },
  };

  return (
    <div className={styles.pageContainer}>
      <header className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>Quản Lý Nhiệm Vụ</h1>
      </header>
      
      <TeamSprintFilters
        teams={teams}
        sprints={sprints}
        selectedTeamId={selectedTeamId}
        selectedSprintId={selectedSprintId}
        onTeamChange={handleTeamChange}
        onSprintChange={(selectedOption) => setSelectedSprintId(selectedOption ? selectedOption.value : "")}
        isLoadingSprints={isLoadingSprints}
        onOpenCreateSprintModal={handleOpenCreateSprintModal}
        onOpenEditSprintModal={handleOpenEditSprintModal}
        getSprintStatus={getSprintStatus}
        onOpenReviewModal={handleOpenReviewModal}
      />
      
      {selectedTeamId ? (
        <KanbanBoard
          columns={columnsForBoard}
          // The `setTasks` prop for optimistic updates is removed for now.
          // A full refresh is used instead. This can be optimized later.
          setTasks={refreshKanbanData}
          isLoading={isLoadingTasks}
          onOpenCreateModal={handleOpenCreateTaskModal}
          onOpenDetailModal={handleOpenDetailModal}
          onDeleteTask={handleOpenDeleteTaskModal}
          selectedSprintId={selectedSprintId}
        />
      ) : (
        <p style={{ textAlign: 'center', marginTop: '40px', color: '#718096' }}>
          Vui lòng chọn nhóm để xem danh sách tồn đọng và các sprint.
        </p>
      )}

      {/* Task Modals */}
      <CreateTaskModal
        isOpen={isCreateTaskModalOpen}
        onClose={handleCloseCreateTaskModal}
        sprintId={selectedSprintId}
        initialStatus={initialStatusForCreate}
        teamMembers={assignableMembers} // Use assignableMembers
        onTaskCreated={refreshKanbanData}
      />
      <TaskDetailModal
        isOpen={isDetailModalOpen}
        onClose={handleCloseDetailModal}
        task={selectedTask}
        teamMembers={assignableMembers} // Use assignableMembers
        onTaskUpdated={refreshKanbanData}
      />

      {/* Sprint Modals */}
      <CreateSprintModal
        isOpen={isCreateSprintModalOpen}
        onClose={handleCloseCreateSprintModal}
        teamId={selectedTeamId}
        onSprintCreated={fetchSprintsAndBacklog}
      />
      <EditSprintModal
        isOpen={isEditSprintModalOpen}
        onClose={handleCloseEditSprintModal}
        sprint={sprintToEdit}
        onSprintUpdated={fetchSprintsAndBacklog}
        onDeleteRequest={handleOpenDeleteSprintModal}
      />

      {/* Review Modal */}
      <SprintReviewModal
        isOpen={isReviewModalOpen}
        onClose={handleCloseReviewModal}
        unfinishedTasks={tasksToReview}
        activeSprints={sprints.filter(s => getSprintStatus(s) !== 'DONE')}
        onConfirm={() => {
          handleCloseReviewModal();
          refreshKanbanData();
        }}
      />

      {/* Generic Confirmation Modal */}
      <ConfirmationModal
        isOpen={isDeleteConfirmationOpen}
        onClose={handleCloseDeleteConfirmation}
        onConfirm={handleConfirmDelete}
        title={`Xác nhận xóa ${itemToDelete.type} `}
        message={`Bạn có chắc chắn muốn xóa ${itemToDelete.type}? Hành động này không thể hoàn tác.`}
      />
    </div>
  );
}

export default TaskManagementPage;


