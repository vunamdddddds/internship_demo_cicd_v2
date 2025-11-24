import React from 'react';

const Modal = ({ title, onClose, children }) => {
  return (
    // Modal Overlay (lớp mờ phía sau)
    <div 
      className="modal-overlay" 
      onClick={onClose}
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000, // Đảm bảo modal nằm trên cùng
      }}
    >
      {/* Modal Content (khung chứa nội dung) */}
      <div 
        className="modal-content" 
        onClick={(e) => e.stopPropagation()} // Ngăn chặn sự kiện click lan ra overlay
        style={{
          backgroundColor: 'white',
          padding: '25px',
          borderRadius: '8px',
          maxWidth: '550px',
          width: '90%',
          boxShadow: '0 5px 15px rgba(0, 0, 0, 0.3)',
          position: 'relative',
        }}
      >
        {/* Modal Header */}
        <div 
          className="modal-header"
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '15px',
          }}
        >
          <h2 style={{ margin: 0, fontSize: '1.25rem', color: '#1f2937' }}>{title}</h2>
          <button 
            onClick={onClose} 
            style={{ 
              background: 'none', 
              border: 'none', 
              fontSize: '1.5rem', 
              cursor: 'pointer',
              color: '#9ca3af'
            }}
          >
            &times;
          </button>
        </div>
        
        {/* Modal Body */}
        <div className="modal-body">
          {children}
        </div>
      </div>
    </div>
  );
};

export default Modal;