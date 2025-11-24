import React, { useState } from "react";
import {
  DndContext,
  PointerSensor,
  KeyboardSensor,
  useSensor,
  useSensors,
  DragOverlay,
} from "@dnd-kit/core";
import KanbanColumn from "./KanbanColumn";
import TaskCard from "./TaskCard";
import TaskApi from "../../api/TaskApi";

import styles from "../../pages/mentor/TaskManagementPage.module.css";

function KanbanBoard({
  columns,
  setTasks,
  isLoading,
  onOpenCreateModal,
  onOpenDetailModal,
  onDeleteTask,
  selectedSprintId,
}) {
  const [activeTask, setActiveTask] = useState(null);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 5,
      },
    }),
    useSensor(KeyboardSensor)
  );

  const handleDragStart = (event) => {
    const { active } = event;
    // Find the active task from all items in all columns
    const task = Object.values(columns)
      .flatMap((col) => col.items)
      .find((t) => t.id === active.id);
    setActiveTask(task);
  };

  const handleDragEnd = async (event) => {
    const { active, over } = event;
    setActiveTask(null);

    if (!over) return;

    const activeId = active.id;
    const overId = over.id;

    const sourceTask = Object.values(columns)
      .flatMap((col) => col.items)
      .find((t) => t.id === active.id);

    const destinationColumnId = Object.keys(columns).find(
      (key) =>
        columns[key].items.some((item) => item.id === overId) || key === overId
    );

    if (!destinationColumnId || !sourceTask) return;

    const sourceColumnId = sourceTask.sprint_Id ? sourceTask.status : 'BACKLOG';

    if (sourceColumnId === destinationColumnId) return; // No change

    // --- API Call Logic ---
    try {
      // Case 1: Moving TO the backlog
      if (destinationColumnId === 'BACKLOG') {
        await TaskApi.batchUpdateTasks({
          action: 'MOVE_TO_BACKLOG',
          taskIds: [activeId],
        });
      } 
      // Case 2: Moving FROM the backlog to a sprint status column
      else if (sourceColumnId === 'BACKLOG') {
        if (!selectedSprintId) {
          alert("Vui lòng chọn một sprint trước khi chuyển các công việc từ danh sách tồn đọng.");
          return;
        }
        await TaskApi.batchUpdateTasks({
          action: 'MOVE_TO_SPRINT',
          taskIds: [activeId],
          targetSprintId: selectedSprintId,
        });
        // Also update the status if it's not TODO
        if (destinationColumnId !== 'TODO') {
            await TaskApi.updateTask(activeId, { status: destinationColumnId });
        }
      }
      // Case 3: Moving between status columns within a sprint
      else {
        await TaskApi.updateTask(activeId, { status: destinationColumnId });
      }

      // --- Refresh data ---
      // Instead of an optimistic update, call the refresh function passed from the parent.
      setTasks();

    } catch (error) {
      console.error("Không thể cập nhật nhiệm vụ:", error);
      alert("Không thể chuyển task. Vui lòng thử lại");
      // On error, you might still want to refresh to revert any failed optimistic state,
      // but since we're not doing that, this is fine.
    }
  };

  if (isLoading) {
    return <p>Đang tải các nhiệm vụ...</p>;
  }

  return (
    <DndContext
      sensors={sensors}
      onDragStart={handleDragStart}
      onDragEnd={handleDragEnd}
    >
      <div className={styles.boardContainer}>
        {Object.entries(columns).map(([columnId, column]) => (
          <KanbanColumn
            key={columnId}
            columnId={columnId}
            title={column.name}
            tasks={column.items}
            onOpenCreateModal={() => onOpenCreateModal(columnId)}
            onOpenDetailModal={onOpenDetailModal}
            onDeleteTask={onDeleteTask}
          />
        ))}
      </div>
      <DragOverlay>
        {activeTask ? <TaskCard task={activeTask} isOverlay /> : null}
      </DragOverlay>
    </DndContext>
  );
}

export default KanbanBoard;