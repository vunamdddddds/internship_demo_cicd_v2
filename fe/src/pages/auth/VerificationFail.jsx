import React from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { XCircle } from 'lucide-react';
import '../../components/authLayout/Auth.css';

const VerificationFail = () => {
  const [searchParams] = useSearchParams();
  const reason = searchParams.get('reason');

  let message = 'Xác thực thất bại. Vui lòng thử lại.';
  if (reason === 'not_found') {
    message = 'Yêu cầu không hợp lệ hoặc không tồn tại.';
  } else if (reason === 'expired') {
    message = 'Yêu cầu đã hết hạn. Vui lòng thực hiện lại.';
  }

  return (
    <div className="auth-form-container">
      <XCircle size={50} color="#dc3545" style={{ marginBottom: '20px' }} />
      <h2>Thất Bại!</h2>
      <p className="subtitle" style={{ marginBottom: '30px' }}>{message}</p>
      <Link to="/auth/login" className="btn-primary" style={{ textDecoration: 'none', display: 'block' }}>
        Quay lại
      </Link>
    </div>
  );
};

export default VerificationFail;