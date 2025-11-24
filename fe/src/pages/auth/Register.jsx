// src/components/SignUp.jsx
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { register, loginWithGoogle } from "~/services/AuthService";
import { toast } from "react-toastify";
import { GoogleLogin } from "@react-oauth/google";

const Register = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    username: "",
    fullName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.id]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.password !== form.confirmPassword) {
      toast.error("Mật khẩu xác nhận không khớp");
      return;
    }
    setLoading(true);
    await register({
      username: form.username,
      email: form.email,
      password: form.password,
      fullName: form.fullName,
      navigate,
    });
    setLoading(false);
  };

  const handleGoogleLogin = async (credentialResponse) => {
    await loginWithGoogle({
      idToken: credentialResponse.credential,
      navigate,
    });
  };

  return (
    <div className="auth-form-container">
      <h2>ĐĂNG KÝ</h2>
      <p className="subtitle">Nhập thông tin của bạn để đăng ký</p>
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="username">Tên đăng nhập</label>
          <input
            type="text"
            id="username"
            value={form.username}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="fullname">Họ và tên</label>
          <input
            type="text"
            id="fullName"
            value={form.fullName}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            value={form.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="password">Mật khẩu</label>
          <div className="password-input">
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              value={form.password}
              onChange={handleChange}
              required
            />
            <span
              className="eye-icon"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
        </div>
        <div className="input-group">
          <label htmlFor="confirm-password">Xác nhận mật khẩu</label>
          <div className="password-input">
            <input
              type={showConfirmPassword ? "text" : "password"}
              id="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              required
            />
            <span
              className="eye-icon"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            >
              {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
        </div>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? "Đang xử lý..." : "ĐĂNG KÍ"}
        </button>
      </form>
      <div className="separator">HOẶC</div>
      <GoogleLogin
        onSuccess={handleGoogleLogin}
        onError={() => {
          console.log("Login Failed");
          toast.error("Đăng nhập thất bại");
        }}
      />
      <p className="switch-form-text">
        Đã có tài khoản?{" "}
        <Link to="/auth/login" className="auth-link">
          ĐĂNG NHẬP NGAY
        </Link>
      </p>
    </div>
  );
};

export default Register;
