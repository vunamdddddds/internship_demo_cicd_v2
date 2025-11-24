import React, { useState, useEffect } from 'react';
import TaskApi from '../../api/TaskApi';
import styles from './SprintReviewModal.module.css'; // We'll create this CSS file

function SprintReviewModal({ isOpen, onClose, unfinishedTasks = [], activeSprints = [], onConfirm }) {
  const [taskActions, setTaskActions] = useState({});

  useEffect(() => {
    if (isOpen) {
      const initialState = unfinishedTasks.reduce((acc, task) => {
        acc[task.id] = { action: '', targetSprintId: '' };
        return acc;
      }, {});
      setTaskActions(initialState);
    }
  }, [isOpen, unfinishedTasks]);

  if (!isOpen) return null;

  const handleActionChange = (taskId, action) => {
    setTaskActions(prev => ({ ...prev, [taskId]: { ...prev[taskId], action } }));
  };

  const handleTargetSprintChange = (taskId, targetSprintId) => {
    setTaskActions(prev => ({ ...prev, [taskId]: { ...prev[taskId], targetSprintId } }));
  };

  const handleConfirm = async () => {
    const groupedByAction = Object.entries(taskActions).reduce((acc, [taskId, { action, targetSprintId }]) => {
      if (!action) return acc;
      if (!acc[action]) {
        acc[action] = {};
      }
      if (action === 'MOVE_TO_SPRINT') {
        const key = targetSprintId || 'default';
        if (!acc[action][key]) {
          acc[action][key] = { taskIds: [], targetSprintId: targetSprintId };
        }
        acc[action][key].taskIds.push(taskId);
      } else {
        if (!acc[action]['default']) {
          acc[action]['default'] = { taskIds: [] };
        }
        acc[action]['default'].taskIds.push(taskId);
      }
      return acc;
    }, {});

    try {
      const apiCalls = [];
      for (const action in groupedByAction) {
        for (const key in groupedByAction[action]) {
          const { taskIds, targetSprintId } = groupedByAction[action][key];
          if (taskIds.length > 0) {
            const payload = { action, taskIds, targetSprintId };
            apiCalls.push(TaskApi.batchUpdateTasks(payload));
          }
        }
      }
      await Promise.all(apiCalls);
      onConfirm();
    } catch (error) {
      console.error("Cập nhật nhiệm vụ thất bại", error);
      alert("Đã xảy ra lỗi khi cập nhật các task. Vui lòng thử lại.");
    }
  };

  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <h2 className={styles.modalTitle}>Rà soát cuối Sprint</h2>
        <p className={styles.modalDescription}>
          Chọn hành động cho các công việc chưa hoàn thành.
        </p>
        <div className={styles.taskList}>
          {unfinishedTasks.length > 0 ? (
            unfinishedTasks.map(task => (
              <div key={task.id} className={styles.taskItem}>
                <span className={styles.taskName}>{task.name}</span>
                <div className={styles.actions}>
                  <select
                    className={styles.actionSelect}
                    value={taskActions[task.id]?.action || ''}
                    onChange={(e) => handleActionChange(task.id, e.target.value)}
                  >
                    <option value="" disabled>Chọn hành động...</option>
                    <option value="MOVE_TO_SPRINT">Chuyển tiếp</option>
                    <option value="MOVE_TO_BACKLOG">Trả về Backlog</option>
                    <option value="CANCEL">Hủy bỏ</option>
                  </select>
                  {taskActions[task.id]?.action === 'MOVE_TO_SPRINT' && (
                    <select
                      className={styles.sprintSelect}
                      value={taskActions[task.id]?.targetSprintId || ''}
                      onChange={(e) => handleTargetSprintChange(task.id, e.target.value)}
                    >
                      <option value="" disabled>Chọn Sprint đích...</option>
                      {activeSprints.map(sprint => (
                        <option key={sprint.id} value={sprint.id}>{sprint.name}</option>
                      ))}
                    </select>
                  )}
                </div>
              </div>
            ))
          ) : (
            <p>Không có công việc nào chưa hoàn thành.</p>
          )}
        </div>
        <div className={styles.modalFooter}>
          <button className={`${styles.btn} ${styles.btnConfirm}`} onClick={handleConfirm} disabled={unfinishedTasks.length === 0}>
            Xác nhận
          </button>
          <button className={`${styles.btn} ${styles.btnCancel}`} onClick={onClose}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}

export default SprintReviewModal;
