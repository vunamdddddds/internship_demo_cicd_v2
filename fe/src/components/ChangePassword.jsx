import React, { useState } from "react";
import { X, Eye, EyeOff, Save, Undo } from "lucide-react";
import authApi from "~/api/AuthApi";
import { toast } from "react-toastify";


const ChangePassword = ({ onClose }) => {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showOld, setShowOld] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
  e.preventDefault();

  if (!oldPassword || !newPassword || !confirmPassword) {
    toast.error("Vui lòng nhập đầy đủ thông tin");
    return;
  }
  if (newPassword !== confirmPassword) {
    toast.error("Mật khẩu xác nhận không khớp");
    return;
  }

  try {
    setLoading(true);
    await authApi.changePassword({
      oldPassword,
      newPassword,
    });
    toast.success("Đổi mật khẩu thành công!");
    onClose();
  } catch (err) {
    toast.error(
      err.response?.data || "Không thể đổi mật khẩu. Vui lòng thử lại."
    );
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h3>Đổi mật khẩu</h3>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>
          <label>Mật khẩu cũ</label>
          <div className="password-input" style={{ position: "relative" }}>
            <input
              type={showOld ? "text" : "password"}
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
              placeholder="Nhập mật khẩu cũ"
            />
            {showOld ? (
              <EyeOff
                className="eye-icon"
                onClick={() => setShowOld(false)}
                size={18}
              />
            ) : (
              <Eye
                className="eye-icon"
                onClick={() => setShowOld(true)}
                size={18}
              />
            )}
          </div>

          <label>Mật khẩu mới</label>
          <div className="password-input" style={{ position: "relative" }}>
            <input
              type={showNew ? "text" : "password"}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="Nhập mật khẩu mới"
            />
            {showNew ? (
              <EyeOff
                className="eye-icon"
                onClick={() => setShowNew(false)}
                size={18}
              />
            ) : (
              <Eye
                className="eye-icon"
                onClick={() => setShowNew(true)}
                size={18}
              />
            )}
          </div>

          <label>Xác nhận mật khẩu mới</label>
          <div className="password-input" style={{ position: "relative" }}>
            <input
              type={showConfirm ? "text" : "password"}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Nhập lại mật khẩu mới"
            />
            {showConfirm ? (
              <EyeOff
                className="eye-icon"
                onClick={() => setShowConfirm(false)}
                size={18}
              />
            ) : (
              <Eye
                className="eye-icon"
                onClick={() => setShowConfirm(true)}
                size={18}
              />
            )}
          </div>

          <div className="modal-actions">
            <button type="button" className="btn-cancel" onClick={onClose}>
              <Undo size={16} /> Hủy
            </button>
            <button type="submit" className="btn-save" disabled={loading}>
              <Save size={16} /> {loading ? "Đang lưu..." : "Lưu thay đổi"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ChangePassword;
