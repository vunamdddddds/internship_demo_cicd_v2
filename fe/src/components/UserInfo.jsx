import { useState } from "react";
import { X, Pencil, Save, Undo, Camera, Loader } from "lucide-react"; // thêm Loader icon
import { useDropzone } from "react-dropzone";
import { updateInfo } from "~/services/UserService";

const UserInfo = ({ user, onClose, setUser }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editInfo, setEditInfo] = useState({ ...user });
  const [previewAvatar, setPreviewAvatar] = useState(user.avatarUrl);
  const [loading, setLoading] = useState(false); // thêm trạng thái loading

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEditInfo({ ...editInfo, [name]: value });
  };

  const onDrop = (acceptedFiles) => {
    const file = acceptedFiles[0];
    if (file) {
      const imageUrl = URL.createObjectURL(file);
      setPreviewAvatar(imageUrl);
      setEditInfo({ ...editInfo, avatarFile: file });
    }
  };

  const { getRootProps, getInputProps } = useDropzone({
    accept: { "image/*": [] },
    onDrop,
  });

  const handleSave = async () => {
    setLoading(true); // bắt đầu loading
    const data = await updateInfo({
      fullName: editInfo.fullName,
      phone: editInfo.phone,
      address: editInfo.address,
      avatarFile: editInfo.avatarFile,
    });
    if (data) {
      setUser(data);
      setEditInfo({ ...editInfo, avatarFile: null });
      setIsEditing(false);
    }
    setLoading(false); // kết thúc loading
  };

  const handleCancel = () => {
    setEditInfo({ ...user });
    setPreviewAvatar(user.avatarUrl);
    setIsEditing(false);
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        {/* Header */}
        <div className="modal-header">
          <h3>Thông tin tài khoản</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {/* Avatar */}
        <div className="profile-section">
          <div className="avatar-wrapper">
            <div className="profile-avatar">
              <img
                src={previewAvatar || "src/assets/avatarDefault.jpg"}
                alt="Avatar"
              />
            </div>
            {isEditing && (
              <div className="camera-overlay" {...getRootProps()}>
                <input {...getInputProps()} />
                <Camera size={20} color="white" />
              </div>
            )}
          </div>

          {isEditing ? (
            <input
              type="text"
              name="fullName"
              value={editInfo.fullName}
              onChange={handleChange}
              className="edit-input name-input"
              required
            />
          ) : (
            <h4 className="profile-name">{user.fullName}</h4>
          )}
          <p className="profile-role">{user.role}</p>
        </div>

        {/* Detail info */}
        <div className="detail-container">
          <div className="detail-row">
            <span className="label">Email:</span>
            <span className="value">{user.email}</span>
          </div>
          <div className="detail-row">
            <span className="label">Số điện thoại:</span>
            {isEditing ? (
              <input
                type="text"
                name="phone"
                value={editInfo.phone || ""}
                onChange={handleChange}
                className="edit-input"
              />
            ) : (
              <span className="value">{user.phone || "--"}</span>
            )}
          </div>
          <div className="detail-row">
            <span className="label">Địa chỉ:</span>
            {isEditing ? (
              <input
                type="text"
                name="address"
                value={editInfo.address || ""}
                onChange={handleChange}
                className="edit-input"
              />
            ) : (
              <span className="value">{user.address || "--"}</span>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="modal-footer">
          {isEditing ? (
            <>
              <button
                className="cancel-btn"
                onClick={handleSave}
                disabled={loading} // disable khi loading
              >
                {loading ? (
                  <Loader size={18} className="spin" />
                ) : (
                  <Save size={18} />
                )}
                <span>{loading ? "Đang lưu..." : "Lưu"}</span>
              </button>
              <button
                className="cancel-btn"
                onClick={handleCancel}
                disabled={loading}
              >
                <Undo size={18} />
                <span>Hủy</span>
              </button>
            </>
          ) : (
            <button className="update-btn" onClick={() => setIsEditing(true)}>
              <Pencil size={18} />
              <span>Cập nhật</span>
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default UserInfo;
