// src/pages/auth/ForgetPassword.jsx
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { toast } from "react-toastify";
import AuthApi from "../../api/AuthApi";
import "../../components/authLayout/Auth.css";

export default function ForgetPassword() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      await AuthApi.forgetPassword(formData);
      toast.success("Đặt lại mật khẩu thành công!");
      navigate("/auth/login");
    } catch (error) {
      console.error("Lỗi quên mật khẩu:", error);
      toast.error("Không thể đặt lại mật khẩu. Vui lòng thử lại!");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="auth-form-container">
      <h2>ĐẶT LẠI MẬT KHẨU</h2>
      <p className="subtitle"></p>

      <form onSubmit={handleSubmit}>
        {/* Email */}
        <div className="input-group">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            name="email"
            placeholder="Nhập email của bạn"
            required
            value={formData.email}
            onChange={(e) =>
              setFormData({ ...formData, email: e.target.value })
            }
          />
        </div>

        {/* Mật khẩu mới */}
        <div className="input-group">
          <label htmlFor="password">Mật khẩu mới</label>
          <div className="password-input">
            <input
              id="password"
              type={showPassword ? "text" : "password"}
              name="password"
              placeholder="Nhập mật khẩu mới"
              required
              value={formData.password}
              onChange={(e) =>
                setFormData({ ...formData, password: e.target.value })
              }
            />
            <span
              className="eye-icon"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
        </div>

        {/* Nút gửi */}
        <button
          type="submit"
          className="btn-primary"
          disabled={isSubmitting}
          style={{ marginTop: "10px" }}
        >
          {isSubmitting ? "Đang gửi..." : "ĐẶT LẠI MẬT KHẨU"}
        </button>
      </form> 
      <p className="subtitle"></p>
      <div className="options-container">
        <Link to="/auth/login" className="auth-link">
          Quay lại đăng nhập
        </Link>
      </div>
    </div>
  );
}
