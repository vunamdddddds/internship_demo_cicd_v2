import AuthApi from "~/api/AuthApi";
import { toast } from "react-toastify";
import { jwtDecode } from "jwt-decode";

export const login = async ({ identifier, password, navigate }) => {
  try {
    const res = await AuthApi.login({ identifier, password });

    const decoded = jwtDecode(res.accessToken);
    if (decoded.scope === "VISITOR") {
      toast.error("Tài khoản của bạn không có quyền truy cập.");
      return;
    }

    localStorage.setItem("AccessToken", res.accessToken);
    localStorage.setItem("RefreshToken", res.refreshToken);

    navigate("/");
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data || "Đăng nhập thất bại");
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const register = async ({
  username,
  email,
  password,
  fullName,
  navigate,
}) => {
  try {
    await AuthApi.register({ username, email, password, fullName });
    navigate("/verify");
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data);
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};

export const loginWithGoogle = async ({ idToken, navigate }) => {
  try {
    const res = await AuthApi.googleLogin({ idToken });

    const decoded = jwtDecode(res.accessToken);
    if (decoded.scope === "VISITOR") {
      toast.error("Tài khoản của bạn không có quyền truy cập.");
      return;
    }

    localStorage.setItem("AccessToken", res.accessToken);
    localStorage.setItem("RefreshToken", res.refreshToken);
    navigate("/");
  } catch (err) {
    if (err.response) {
      toast.error(err.response.data || "Đăng nhập thất bại");
    } else {
      toast.error("Không thể kết nối đến server");
    }
  }
};
