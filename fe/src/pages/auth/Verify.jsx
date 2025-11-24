// src/pages/auth/Verify.jsx
import React from "react";
import { Link } from "react-router-dom";
import { FiMail, FiCheckCircle } from "react-icons/fi";

const Verify = () => {
  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.iconWrapper}>
          <FiMail size={60} color="#4CAF50" style={styles.mainIcon} />
          <FiCheckCircle size={24} color="#4CAF50" style={styles.checkIcon} />
        </div>
        <h2 style={styles.title}>Xác thực tài khoản</h2>
        <p style={styles.message}>
          Email xác thực đã được gửi thành công! <br />
          Vui lòng kiểm tra hộp thư (bao gồm thư rác) và làm theo hướng dẫn để
          kích hoạt tài khoản của bạn.
        </p>
      </div>
    </div>
  );
};

const styles = {
  container: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    minHeight: "100vh",
    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
    padding: 20,
    fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
  },
  card: {
    backgroundColor: "#fff",
    padding: "60px 40px",
    borderRadius: 20,
    boxShadow: "0 20px 40px rgba(0,0,0,0.1), 0 0 0 1px rgba(255,255,255,0.2)",
    textAlign: "center",
    maxWidth: 450,
    width: "100%",
    position: "relative",
    overflow: "hidden",
    backdropFilter: "blur(10px)",
  },
  iconWrapper: {
    background: "linear-gradient(135deg, #e8f5e9, #c8e6c9)",
    borderRadius: "50%",
    width: 100,
    height: 100,
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    margin: "0 auto 25px auto",
    position: "relative",
    boxShadow: "0 8px 20px rgba(76, 175, 80, 0.3)",
    transition: "transform 0.3s ease, box-shadow 0.3s ease",
  },
  iconWrapperHover: {
    // Note: For hover, you'd need CSS or onMouseEnter in React
    transform: "scale(1.05)",
    boxShadow: "0 12px 30px rgba(76, 175, 80, 0.4)",
  },
  mainIcon: {
    zIndex: 2,
  },
  checkIcon: {
    position: "absolute",
    top: 5,
    right: 5,
    zIndex: 3,
    animation: "pulse 2s infinite", // You'd need to add this keyframe in CSS
  },
  title: {
    margin: 0,
    fontSize: 28,
    marginBottom: 15,
    color: "#2c3e50",
    fontWeight: "600",
    letterSpacing: "-0.5px",
  },
  message: {
    fontSize: 16,
    color: "#555",
    marginBottom: 15,
    lineHeight: 1.6,
    fontWeight: "400",
  },

  // Hover effects (add onMouseEnter/onMouseLeave to elements in JSX for interactivity)
  linkHover: {
    transform: "translateY(-2px)",
    boxShadow: "0 6px 20px rgba(0,0,0,0.2)",
  },
};

export default Verify;
