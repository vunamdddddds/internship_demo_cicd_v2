import { BrowserRouter } from "react-router-dom";
import { createRoot } from "react-dom/client";
import { GoogleOAuthProvider } from "@react-oauth/google";
import "./index.css";
import App from "~/App.jsx";
const googleClientId =
  "328284228414-bj3p8ofg94an1h3dguniipme1a1f961l.apps.googleusercontent.com"; // tạm thời hard code - thay bằng google client id của bạn 
createRoot(document.getElementById("root")).render(
  <GoogleOAuthProvider clientId={googleClientId}>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </GoogleOAuthProvider>
);                                          