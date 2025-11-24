import AxiosClient from "./AxiosClient";

const authApi = {
  login: (data) => {
    return AxiosClient.post("/auth/login", data);
  },
  register: (data) => {
    return AxiosClient.post("/auth/register", data);
  },
  googleLogin: (data) => {
    return AxiosClient.post("/auth/google-login", data);
  },
  forgetPassword: (data) => {
    return AxiosClient.post("/auth/forgetPasswordForFEMagager", data);
  },
 
 
  changePassword: (data) => {
  return AxiosClient.put("/auth/changePassword", data);
},
 }

export default authApi;
