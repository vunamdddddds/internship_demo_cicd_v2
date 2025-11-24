import React, { useState, useEffect, useCallback } from "react";
import InternApi from "../../api/internApi"; // Corrected import path
import SprintApi from "../../api/SprintApi";
import TaskApi from "../../api/TaskApi";
import TeamSprintFilters from "../task_management/TeamSprintFilters";
import KanbanBoard from "../task_management/KanbanBoard";
import CreateTaskModal from "../task_management/CreateTaskModal";
import TaskDetailModal from "../task_management/TaskDetailModal";
import ConfirmationModal from "../task_management/ConfirmationModal";
import styles from "../mentor/TaskManagementPage.module.css"; // Re-use the same styles

function MyWorkPage() {
  // Data states
  const [sprints, setSprints] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [teamDetails, setTeamDetails] = useState(null);

  // Selection states
  const [selectedSprintId, setSelectedSprintId] = useState("");

  // Loading states
  const [isLoadingProfile, setIsLoadingProfile] = useState(true);
  const [isLoadingSprints, setIsLoadingSprints] = useState(false);
  const [isLoadingTasks, setIsLoadingTasks] = useState(false);

  // Modal states
  const [isCreateTaskModalOpen, setIsCreateTaskModalOpen] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isDeleteConfirmationOpen, setIsDeleteConfirmationOpen] = useState(false);
  
  // Data for modals
  const [itemToDelete, setItemToDelete] = useState({ id: null, type: null });
  const [selectedTask, setSelectedTask] = useState(null);
  const [initialStatusForCreate, setInitialStatusForCreate] = useState("TODO");

  // --- Modal Handlers (Task) ---
  const handleOpenCreateTaskModal = (status) => {
    setInitialStatusForCreate(status);
    setIsCreateTaskModalOpen(true);
  };
  const handleCloseCreateTaskModal = () => setIsCreateTaskModalOpen(false);

  const handleOpenDetailModal = (task) => {
    setSelectedTask(task);
    setIsDetailModalOpen(true);
  };
  const handleCloseDetailModal = () => setIsDetailModalOpen(false);

  const handleOpenDeleteTaskModal = (taskId) => {
    setItemToDelete({ id: taskId, type: 'task' });
    setIsDeleteConfirmationOpen(true);
  };

  // --- Generic Confirmation Modal Handlers ---
  const handleCloseDeleteConfirmation = () => {
    setItemToDelete({ id: null, type: null });
    setIsDeleteConfirmationOpen(false);
  };

  const handleConfirmDelete = async () => {
    if (!itemToDelete.id || itemToDelete.type !== 'task') return;

    try {
      await TaskApi.deleteTask(itemToDelete.id);
      setTasks(prevTasks => prevTasks.filter(task => task.id !== itemToDelete.id));
    } catch (error) {
      console.error(`Không thể xóa ${itemToDelete.type}:`, error);
      alert(`Không thể xóa ${itemToDelete.type}. Vui lòng thử lại.`);
    } finally {
      handleCloseDeleteConfirmation();
    }
  };

  // --- API Calls ---
  useEffect(() => {
    const fetchMyProfile = async () => {
      setIsLoadingProfile(true);
      try {
        const response = await InternApi.getMe();
        setTeamDetails(response.teamDetails || null);
      } catch (error) {
        console.error("Không thể lấy hồ sơ thực tập sinh:", error);
        setTeamDetails(null);
      } finally {
        setIsLoadingProfile(false);
      }
    };
    fetchMyProfile();
  }, []);

  const fetchSprints = useCallback(async () => {
    if (!teamDetails?.id) return;
    setIsLoadingSprints(true);
    try {
      const response = await SprintApi.getSprintsByTeam(teamDetails.id);
      setSprints(response || []);
    } catch (error) {
      console.error("Không thể lấy các sprint:", error);
      setSprints([]);
    } finally {
      setIsLoadingSprints(false);
    }
  }, [teamDetails]);

  const fetchTasks = useCallback(async () => {
    if (!selectedSprintId) return;
    setIsLoadingTasks(true);
    try {
      const response = await TaskApi.getTasksBySprint(selectedSprintId);
      setTasks(response.data || []);
    } catch (error) {
      console.error("Không thể lấy các task:", error);
      setTasks([]);
    } finally {
      setIsLoadingTasks(false);
    }
  }, [selectedSprintId]);

  useEffect(() => {
    fetchSprints();
  }, [fetchSprints]);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  const getSprintStatus = (sprint) => {
    const today = new Date();
    const startDate = new Date(sprint.startDate);
    const endDate = new Date(sprint.endDate);
    today.setHours(0, 0, 0, 0);
    
    if (today < startDate) return "TODO";
    if (today > endDate) return "DONE";
    return "IN_PROGRESS";
  };

  if (isLoadingProfile) {
    return <div className={styles.pageContainer}><p>Đang tải hồ sơ của bạn...</p></div>;
  }

  if (!teamDetails) {
    return <div className={styles.pageContainer}><p>Bạn chưa được phân công vào nhóm.</p></div>;
  }

  const sprintTasks = tasks.filter(task => task.status !== 'CANCELLED');

  const columns = {
    TODO: { name: "To Do", items: sprintTasks.filter(t => t.status === 'TODO') },
    IN_PROGRESS: { name: "In Progress", items: sprintTasks.filter(t => t.status === 'IN_PROGRESS') },
    DONE: { name: "Done", items: sprintTasks.filter(t => t.status === 'DONE') },
  };

  return (
    <div className={styles.pageContainer}>
      <header className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>Nhiệm vụ của tôi</h1>
        <p style={{ margin: 0, color: '#718096' }}>Nhóm: {teamDetails.teamName}</p>
      </header>
      
      <TeamSprintFilters
        sprints={sprints}
        selectedSprintId={selectedSprintId}
        onSprintChange={(selectedOption) => setSelectedSprintId(selectedOption ? selectedOption.value : "")}
        isLoadingSprints={isLoadingSprints}
        getSprintStatus={getSprintStatus}
        isInternView={true}
      />
      
      <hr style={{ margin: '24px 0', border: 'none', borderTop: '1px solid #e2e8f0' }} />

      {selectedSprintId ? (
        <KanbanBoard
          columns={columns}
          setTasks={fetchTasks}
          isLoading={isLoadingTasks}
          onOpenCreateModal={handleOpenCreateTaskModal}
          onOpenDetailModal={handleOpenDetailModal}
          onDeleteTask={handleOpenDeleteTaskModal}
          selectedSprintId={selectedSprintId}
        />
      ) : (
        <p style={{ textAlign: 'center', marginTop: '40px', color: '#718096' }}>
          Vui lòng chọn một sprint để xem các nhiệm vụ của bạn.
        </p>
      )}

      {/* Modals */}
      <CreateTaskModal
        isOpen={isCreateTaskModalOpen}
        onClose={handleCloseCreateTaskModal}
        sprintId={selectedSprintId}
        initialStatus={initialStatusForCreate}
        teamMembers={teamDetails?.members || []}
        onTaskCreated={fetchTasks}
      />
      <TaskDetailModal
        isOpen={isDetailModalOpen}
        onClose={handleCloseDetailModal}
        task={selectedTask}
        teamMembers={teamDetails?.members || []}
        onTaskUpdated={fetchTasks}
      />
      <ConfirmationModal
        isOpen={isDeleteConfirmationOpen}
        onClose={handleCloseDeleteConfirmation}
        onConfirm={handleConfirmDelete}
        title={`Xác nhận xóa ${itemToDelete.type}`}
        message={`Bạn có chắc chắn muốn xóa ${itemToDelete.type}? Hành động này không thể hoàn tác.`}
      />
    </div>
  );
}

export default MyWorkPage;
