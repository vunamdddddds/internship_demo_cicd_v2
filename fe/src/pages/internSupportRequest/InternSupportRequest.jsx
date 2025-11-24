import React, { useState, useEffect } from "react";
import { toast } from "react-toastify";
import Swal from "sweetalert2";
import { InternSupportRequestService } from "~/services/InternSupportRequestService";
import {
    FaHistory, FaPaperPlane, FaFileUpload, FaFileAlt, FaEdit,
    FaCheckCircle, FaTimesCircle, FaClock, FaInbox, FaTrashAlt, FaUndo
} from "react-icons/fa";

import "./InternSupportRequest.css";

const InternSupportRequest = () => {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [file, setFile] = useState(null);
    const [fileName, setFileName] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [editingId, setEditingId] = useState(null);

    const loadRequests = async () => {
        setLoading(true);
        try {
            const data = await InternSupportRequestService.fetchMyRequests();
            setRequests(data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadRequests(); }, []);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            if (selectedFile.size > 5 * 1024 * 1024) {
                toast.error("File không được quá 5MB");
                e.target.value = null;
                setFile(null);
                setFileName("");
            } else {
                setFile(selectedFile);
                setFileName(selectedFile.name);
            }
        }
    };

    const handleEditClick = (req) => {
        setEditingId(req.id);
        setTitle(req.title);
        setDescription(req.description);
        setFile(null);
        setFileName("");
        const formElement = document.querySelector('.right-column');
        if (formElement) formElement.scrollIntoView({ behavior: 'smooth' });
    };

    const handleDeleteClick = (id) => {
        Swal.fire({
            title: 'Bạn chắc chắn chứ?',
            text: "Yêu cầu này sẽ bị hủy và không thể hoàn tác!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#2563eb',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Vâng, hủy nó!',
            cancelButtonText: 'Hủy bỏ'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    await InternSupportRequestService.cancelRequest(id);

                    // Cập nhật state
                    setRequests((prev) => prev.filter((req) => req.id !== id));
                    if (editingId === id) {
                        handleCancelEdit();
                    }
                    Swal.fire(
                        'Đã hủy!',
                        'Yêu cầu hỗ trợ đã được hủy thành công.',
                        'success'
                    );
                } catch {
                    Swal.fire(
                        'Lỗi!',
                        'Không thể hủy yêu cầu lúc này.',
                        'error'
                    );
                }
            }
        });
    };

    const handleCancelEdit = () => {
        setEditingId(null);
        setTitle("");
        setDescription("");
        setFile(null);
        setFileName("");
        const fileInput = document.getElementById("file-upload");
        if (fileInput) fileInput.value = "";
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!title.trim()) return toast.warning("Vui lòng nhập tiêu đề");
        if (!description.trim()) return toast.warning("Vui lòng nhập mô tả");

        setIsSubmitting(true);
        try {
            if (editingId) {
                const updatedReq = await InternSupportRequestService.updateRequest(editingId, {
                    title: title.trim(),
                    description: description.trim(),
                    file: file,
                });
                setRequests(prev => prev.map(r => r.id === editingId ? updatedReq : r));
                handleCancelEdit();
            } else {
                const newReq = await InternSupportRequestService.submitRequest({
                    title: title.trim(),
                    description: description.trim(),
                    file: file,
                });
                setRequests(prev => [newReq, ...prev]);
                handleCancelEdit();
            }
        } catch (err) {
            console.error(err);
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderStatus = (status) => {
        let className = "status-badge status-pending";
        let icon = <FaClock />;
        let text = "Chờ xử lý";

        if (status === "RESOLVED") { className = "status-badge status-resolved"; icon = <FaCheckCircle />; text = "Đã xử lý"; }
        else if (status === "REJECTED") { className = "status-badge status-rejected"; icon = <FaTimesCircle />; text = "Từ chối"; }
        else if (status === "IN_PROGRESS") { className = "status-badge status-progress"; icon = <FaClock />; text = "Đang xử lý"; }

        return (
            <span className={className}>
                {icon} {text}
            </span>
        );
    };

    if (loading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
                <p>Đang tải dữ liệu...</p>
            </div>
        );
    }

    return (
        <div className="page-container">

            <div className="content-wrapper">

                {/* === CỘT TRÁI === */}
                <div className="left-column">
                    <div className="section-title">
                        <FaHistory /> Lịch sử yêu cầu ({requests.length})
                    </div>

                    <div className="list-container">
                        {requests.length === 0 ? (
                            <div className="empty-state">
                                <FaInbox size={40} opacity={0.3} />
                                <p>Bạn chưa gửi yêu cầu nào.</p>
                            </div>
                        ) : (
                            requests.map((req) => (
                                <div key={req.id} className="request-card">
                                    <div className="card-header">
                                        <h4 className="card-title">{req.title}</h4>
                                        <div className="card-actions">
                                            {renderStatus(req.status)}

                                            {req.status === "PENDING" && (
                                                <>
                                                    <button className="icon-btn" onClick={() => handleEditClick(req)} title="Sửa yêu cầu">
                                                        <FaEdit color="#2563eb" />
                                                    </button>
                                                    <button className="icon-btn delete-btn" onClick={() => handleDeleteClick(req.id)} title="Hủy yêu cầu">
                                                        <FaTrashAlt color="#ef4444" />
                                                    </button>
                                                </>
                                            )}
                                        </div>
                                    </div>

                                    <div className="card-meta">
                                        <span>{req.createdAt}</span>
                                        {req.handlerName && <span> • Xử lý bởi: <b>{req.handlerName}</b></span>}
                                    </div>

                                    <div className="card-body">
                                        <p><strong>Mô tả:</strong> {req.description}</p>
                                        {req.hrResponse && (
                                            <div className="hr-response-box">
                                                <strong style={{ color: '#1d4ed8', display: 'block', marginBottom: '4px' }}>
                                                    Phản hồi từ HR:
                                                </strong>
                                                {req.hrResponse}
                                            </div>
                                        )}
                                    </div>

                                    {req.evidenceFileUrl && (
                                        <div className="card-footer">
                                            <a href={req.evidenceFileUrl} target="_blank" rel="noreferrer" className="file-link">
                                                <FaFileAlt /> Xem file đính kèm
                                            </a>
                                        </div>
                                    )}
                                </div>
                            ))
                        )}
                    </div>
                </div>

                {/* === CỘT PHẢI === */}
                <div className="right-column">
                    <div className="form-card">
                        <h3 className="form-title">
                            {editingId ? "Cập nhật yêu cầu" : "Tạo yêu cầu mới"}
                        </h3>

                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label className="form-label">Tiêu đề <span style={{ color: 'red' }}>*</span></label>
                                <input
                                    className="form-input"
                                    value={title}
                                    onChange={e => setTitle(e.target.value)}
                                    placeholder="Ví dụ: Xin xác nhận thực tập..."
                                    maxLength={100}
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label">Mô tả chi tiết <span style={{ color: 'red' }}>*</span></label>
                                <textarea
                                    className="form-textarea"
                                    rows="6"
                                    value={description}
                                    onChange={e => setDescription(e.target.value)}
                                    placeholder="Mô tả rõ vấn đề..."
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label">Đính kèm <small style={{ fontWeight: 'normal', color: '#94a3b8' }}>(Ảnh/PDF, max 5MB)</small></label>
                                <div className="file-wrapper">
                                    <input type="file" id="file-upload" hidden onChange={handleFileChange} accept="image/*,.pdf,.doc,.docx" />
                                    <label
                                        htmlFor="file-upload"
                                        className={`file-dropzone ${fileName ? 'active' : ''}`}
                                    >
                                        <FaFileUpload size={24} style={{ marginBottom: 8 }} />
                                        {fileName ? <b>{fileName}</b> : "Nhấn để chọn file"}
                                    </label>
                                    {fileName && (
                                        <button type="button" className="remove-file-btn" onClick={() => { setFile(null); setFileName(""); }}>
                                            <FaTrashAlt />
                                        </button>
                                    )}
                                </div>
                            </div>

                            <button type="submit" className="submit-btn" disabled={isSubmitting}>
                                {isSubmitting ? "Đang xử lý..." : (editingId ? "Cập nhật" : <> <FaPaperPlane /> Gửi yêu cầu </>)}
                            </button>

                            {editingId && (
                                <button type="button" className="cancel-btn" onClick={handleCancelEdit}>
                                    <FaUndo /> Hủy chỉnh sửa
                                </button>
                            )}
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default InternSupportRequest;