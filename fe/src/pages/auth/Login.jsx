// src/components/SignIn.jsx

import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { login, loginWithGoogle } from "../../services/AuthService";
import { GoogleLogin } from "@react-oauth/google";

const Login = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    await login({ identifier, password, navigate });
  };

  const handleGoogleLogin = async (credentialResponse) => {
    await loginWithGoogle({
      idToken: credentialResponse.credential,
      navigate,
    });
  };

  return (
    <div className="auth-form-container">
      <h2>ĐĂNG NHẬP</h2>
      <p className="subtitle">Chào mừng trở lại</p>
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="email">Email</label>
          <input
            type="text"
            id="email"
            value={identifier}
            onChange={(e) => setIdentifier(e.target.value)}
          />
        </div>
        <div className="input-group">
          <label htmlFor="password">Mật khẩu</label>
          <div className="password-input">
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <span
              className="eye-icon"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
        </div>
        <div className="options-container">
          <Link to="/forgot-password" className="auth-link">
            Quên mật khẩu?
          </Link>
        </div>
        <button type="submit" className="btn-primary">
          ĐĂNG NHẬP
        </button>
      </form>
      <div className="separator">HOẶC</div>
      <GoogleLogin
        onSuccess={handleGoogleLogin}
        onError={() => {
          console.log("Login Failed");
        }}
        useOneTap
      />
    </div>
  );
};

export default Login;
