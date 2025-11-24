import React from "react";
import { useDroppable } from "@dnd-kit/core";
import TaskCard from "./TaskCard";
import styles from "../../pages/mentor/TaskManagementPage.module.css";
import { Plus } from "lucide-react";

function KanbanColumn({
  title,
  columnId,
  tasks,
  onOpenCreateModal,
  onOpenDetailModal,
  onDeleteTask,
}) {
  const { setNodeRef, isOver } = useDroppable({
    id: columnId,
  });

  return (
    <div className={styles.column}>
      <h3 className={styles.columnHeader}>{title}</h3>
      <div
        ref={setNodeRef}
        className={styles.columnContent}
        style={{ backgroundColor: isOver ? "#e9f2ff" : undefined }}
      >
        {tasks.map((task) => (
          <TaskCard
            key={task.id}
            task={task}
            onOpenDetailModal={onOpenDetailModal}
            onDelete={onDeleteTask}
          />
        ))}
      </div>
      <button
        onClick={() => onOpenCreateModal(columnId)}
        className={styles.actionButton}
        style={{ margin: '8px', backgroundColor: 'transparent', color: '#4A5568' }}
      >
        <Plus size={16} /> Thêm thẻ nhiệm vụ
      </button>
    </div>
  );
}

export default KanbanColumn;