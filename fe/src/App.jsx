import { Routes, Route, Navigate } from "react-router-dom";
import Login from "~/pages/auth/Login";
import Register from "~/pages/auth/Register";
import ForgotPassword from "~/pages/auth/ForgotPassword";
import Verify from "~/pages/auth/Verify";
import AuthLayout from "~/components/authLayout/AuthLayout";
import Layout from "./components/layout/Layout";
import InternManagement from "./pages/internManagement/InternManagement";
import MentorManagement from "./pages/mentorManagement/MentorManagement";
import UserManagement from "./pages/userManagement/UserManagement";
import TeamManagement from "./pages/teamManagement/TeamManagement";
import InternshipApplicationManagement from "./pages/internshipApplicationManagement/InternshipApplicationManagement";
import ChatManagement from "./pages/chatManagement/ChatManagement";
import MyCalendar from "~/pages/myCalendar/MyCalendar";
import MyWorkPage from "~/pages/intern/MyWorkPage";
import MyAllowanceHistory from "~/pages/myAllowanceHistory/MyAllowanceHistory";
import TaskManagementPage from "~/pages/mentor/TaskManagementPage";

import ScheduleManagement from "./pages/scheduleManagement/ScheduleManagement";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import InternshipProgramManagament from "./pages/internshipProgramManagament/InternshipProgramManagament";
import DiligenceReport from "./pages/diligenceHr/DiligenceReport";
import DiligenceDetail from "~/pages/diligenceHr/DiligenceDetail";
import EvaluationReport from "./pages/evaluationReport/EvaluationReport";
import EvaluationDiligenceDetail from "./pages/evaluationReport/DiligenceDetail";
import LeaveRequest from "./pages/leaveRequest/LeaveRequest";
import MentorEvaluation from "./pages/MentorEvaluation/MentorEvaluation";
import LeaveRequestManagement from "./pages/leaveRequestManagement/LeaveRequestManagement";
import InternSupportRequest from "~/pages/internSupportRequest/InternSupportRequest"; 
import SupportRequestList from "./pages/supportRequest/SupportRequestList";
import SupportRequestDetail from "./pages/supportRequest/SupportRequestDetail";
import AllowanceManagement from "./pages/allowance/AllowanceManagement";
import AllowancePackageManagement from "./pages/allowancePackage/AllowancePackageManagement";
import AllowanceReportManagement from "./pages/allowanceReport/AllowanceReportManagement";

function App() {
  return (
    <>
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />

      <Routes>
        {/* === AUTH === */}
        <Route path="/auth" element={<AuthLayout />}>
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route path="forgot-password" element={<ForgotPassword />} />
          <Route index element={<Navigate to="/auth/login" />} />
        </Route>

        <Route path="/verify" element={<Verify />} />

        {/* === ỨNG DỤNG CHÍNH (cần đăng nhập) === */}
        <Route path="/" element={<Layout />}>
          {/* Quản lý */}
          <Route path="intern" element={<InternManagement />} />
          <Route path="user" element={<UserManagement />} />
          <Route path="mentor" element={<MentorManagement />} />
          <Route path="team" element={<TeamManagement />} />
          <Route
            path="internshipApplication"
            element={<InternshipApplicationManagement />}
          />
          {/* <Route path="allowance" element={<AllowanceManagement />} /> */}
          <Route path="allowance-report" element={<AllowanceReportManagement />} />
          <Route path="allowance-package" element={<AllowancePackageManagement />} />
          <Route
            path="internshipProgram"
            element={<InternshipProgramManagament />}
          />
          <Route path="chat" element={<ChatManagement />} />
          {/* Cá nhân */}
          <Route path="myCalendar" element={<MyCalendar />} />
          <Route path="my-work" element={<MyWorkPage />} />
          <Route path="my-allowance-history" element={<MyAllowanceHistory />} />
          {/* Mentor */}
          <Route path="mentor/tasks" element={<TaskManagementPage />} />
          <Route path="mentor/evaluation" element={<MentorEvaluation />} />{" "}
          {/* CHUẨN */}
          {/* Lịch & Nghỉ phép */}
          <Route path="scheduleManagement" element={<ScheduleManagement />} />
          <Route path="leaveRequest" element={<LeaveRequest />} />
          <Route
            path="leaveRequestManagement"
            element={<LeaveRequestManagement />}
          />
          <Route path="diligenceHr" element={<DiligenceReport />} />
          <Route
            path="/diligenceHr/detail/:internId"
            element={<DiligenceDetail />}
          />
          {/* Trang mặc định */}
          <Route path="evaluationReport" element={<EvaluationReport />} />
          <Route path="supportRequestList" element={<SupportRequestList />} />
          <Route path="supportRequestDetail/:id" element={<SupportRequestDetail />} />
          <Route
            path="/evaluationReport/diligence/:internId/:internshipProgramId"
            element={<EvaluationDiligenceDetail />}
          />
          <Route path="support-request" element={<InternSupportRequest />} />
          {/* <Route index element={<Navigate to="/intern" />} /> */}
        </Route>
      </Routes>
    </>
  );
}

export default App;
