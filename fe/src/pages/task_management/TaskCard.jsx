import React from "react";
import { useDraggable } from "@dnd-kit/core";
import { Trash2 } from "lucide-react";
import styles from "../../pages/mentor/TaskManagementPage.module.css";

function TaskCard({ task, isOverlay, onOpenDetailModal, onDelete }) {
  const { attributes, listeners, setNodeRef, transform, isDragging } = useDraggable({
    id: task.id,
    data: {
      task,
    },
  });

  const dynamicStyle = {
    opacity: isDragging && !isOverlay ? 0.5 : 1,
    boxShadow: isOverlay ? '0px 5px 15px rgba(0, 0, 0, 0.3)' : undefined,
    transform: isOverlay ? `rotate(2deg)` : (transform ? `translate3d(${transform.x}px, ${transform.y}px, 0)` : undefined),
  };

  const handleDeleteClick = (e) => {
    e.stopPropagation();
    onDelete(task.id);
  };

  return (
    <div 
      ref={setNodeRef} 
      style={dynamicStyle} 
      {...listeners} 
      {...attributes}
      onClick={() => onOpenDetailModal(task)}
      className={styles.taskCard}
    >
      <button className={styles.deleteButton} onClick={handleDeleteClick}>
        <Trash2 size={16} />
      </button>
      <p className={styles.taskName}>{task.name}</p>
      <small className={styles.assigneeName}>
        {task.assigneeName || "Chưa được phân công"}
      </small>
    </div>
  );
}

export default TaskCard;