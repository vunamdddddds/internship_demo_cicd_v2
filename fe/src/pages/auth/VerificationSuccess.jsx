import React from 'react';
import { Link } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';
import '../../components/authLayout/Auth.css';

const VerificationSuccess = () => {
  return (
    <div className="auth-form-container">
      <CheckCircle size={50} color="#28a745" style={{ marginBottom: '20px' }} />
      <h2>Thành Công!</h2>
      <p className="subtitle" style={{ marginBottom: '30px' }}>
        Thao tác của bạn đã được xác thực thành công.
      </p>
      <Link to="/auth/login" className="btn-primary" style={{ textDecoration: 'none', display: 'block' }}>
        Quay lại Đăng nhập
      </Link>
    </div>
  );
};

export default VerificationSuccess;