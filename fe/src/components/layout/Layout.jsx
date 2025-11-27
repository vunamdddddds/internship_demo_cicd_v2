import React, { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import { Outlet, Link, useLocation, useNavigate } from "react-router-dom";
import ChangePassword from "../ChangePassword";
import { getInfo } from "~/services/UserService";
import UserInfo from "../UserInfo";
import {
  Menu,
  X,
  UserCheck,
  Bell,
  FileText,
  Award,
  LogOut,
  User,
  KeyRound,
  MessageSquare,
  Users,
  Calendar,
  ClipboardList,
  FileQuestion,
  LifeBuoy,
  FileSpreadsheet,
  ChevronDown,
  DollarSign,
} from "lucide-react";
import "./Layout.css";

// Helper component for the collapsible submenu
const SubMenuItem = ({ item, sidebarOpen, location }) => {
  const [isOpen, setIsOpen] = useState(false);

  const hasActiveChild = item.children.some(
    (child) => location.pathname === child.path
  );

  useEffect(() => {
    // Automatically open the submenu if one of its children is active
    if (hasActiveChild) {
      setIsOpen(true);
    }
  }, [hasActiveChild]);

  const ParentIcon = item.icon;

  return (
    <div className={`nav-item-container ${hasActiveChild ? "parent-active" : ""}`}>
      <button 
        className={`nav-item-parent ${isOpen || hasActiveChild ? "active" : ""}`} 
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="nav-item-parent-title">
          <ParentIcon className="nav-icon" size={20} />
          {sidebarOpen && <span className="nav-label">{item.label}</span>}
        </div>
        {sidebarOpen && (
          <ChevronDown
            className={`nav-chevron ${isOpen ? "open" : ""}`}
            size={16}
          />
        )}
      </button>
      {sidebarOpen && (
        <div className={`submenu ${isOpen ? "open" : ""}`}>
          {item.children.map((child) => {
            const ChildIcon = child.icon;
            const isChildActive = location.pathname === child.path;
            return (
              <Link
                key={child.path}
                to={child.path}
                className={`submenu-item ${isChildActive ? "active" : ""}`}
              >
                <ChildIcon className="nav-icon" size={20} />
                <span className="nav-label">{child.label}</span>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
};


const rolePermissions = {
  ADMIN: [
    "/user",
    "/intern",
    "/internshipApplication",
    "/internshipProgram",
    "/chat",
    "/mentor",
    "/team",
    "/scheduleManagement",
  ],
  MENTOR: ["/mentor/tasks", "/mentor/evaluation"], // Added /mentor/tasks for MENTOR role

  INTERN: ["/myCalendar", "/leaveRequest", "/workProgress", "/my-work", "/my-allowance-history" , "/support-request"],
  HR: [
    "/intern",
    "/user",
    "/internshipProgram",
    "/chat",
    "/scheduleManagement",
    "/admin/team-schedule",
    "/diligenceHr",
    "/leaveRequestManagement",
    "/evaluationReport",
    "/supportRequestList",
    "/allowance-report",
    "/allowance-package",
  ],
  VISITOR: [],
};

const menuItems = [
  { path: "/user", icon: UserCheck, label: "Quản lý người dùng" },
  { path: "/intern", icon: UserCheck, label: "Quản lý thực tập sinh" },
  { path: "/mentor", icon: UserCheck, label: "Quản lý Mentor" },
  { path: "/team", icon: Users, label: "Quản lý nhóm" },
  {
    path: "/internshipApplication",
    icon: FileText,
    label: "Quản lý đơn xin thực tập",
  },
  { path: "/myCalendar", icon: Calendar, label: "Lịch của tôi" },
  { path: "/my-work", icon: ClipboardList, label: "Nhiệm vụ của tôi" },
  { path: "/my-allowance-history", icon: FileText, label: "Lịch sử phụ cấp" },
  {
    path: "/scheduleManagement",
    icon: Calendar,
    label: "Quản lí lịch thực tập",
  },
  { path: "/internshipProgram", icon: Award, label: "Quản lý kỳ thực tập" },
  {
    id: "allowance",
    icon: DollarSign,
    label: "Quản lí phụ cấp",
    children: [
      { path: "/allowance-package", icon: Award, label: "Quản lí gói phụ cấp" },
      { path: "/allowance-report", icon: FileSpreadsheet, label: "Báo cáo phụ cấp" },
    ],
  },
  { path: "/schedule", icon: Calendar, label: "Lịch" },
  { path: "/browseLeave", icon: FileText, label: "Duyệt đơn nghỉ phép" },
  { path: "/leaveRequest", icon: FileText, label: "Xin phép" },
  {
    path: "/leaveRequestManagement",
    icon: ClipboardList,
    label: "Quản lý đơn xin phép",
  },
  { path: "/diligenceHr", icon: FileText, label: "Quản lý chuyên cần" },
  {
    path: "/mentor/evaluation",
    icon: ClipboardList,
    label: "Đánh giá thực tập sinh",
  },
  { path: "/mentor/tasks", icon: ClipboardList, label: "Quản lý công việc" },
  {
    path: "/evaluationReport",
    icon: FileText,
    label: "Báo cáo đánh giá thực tập",
  },
  { path: "/support-request", icon: LifeBuoy, label: "Yêu cầu hỗ trợ" },
  { path: "/supportRequestList", icon: FileQuestion, label: "Yêu cầu hỗ trợ" },     
  { path: "/chat", icon: MessageSquare, label: "Tin nhắn" },
];
const Layout = () => {
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const location = useLocation();
  const [role, setRole] = useState(null);
  const [filteredMenu, setFilteredMenu] = useState([]);
  const [showMenu, setShowMenu] = useState(false);
  const [showInfo, setShowInfo] = useState(false);
  const [user, setUser] = useState([]);
  const [showChangePassword, setShowChangePassword] = useState(false);
  const toggleUserMenu = () => setShowMenu(!showMenu);

  useEffect(() => {
    const token = localStorage.getItem("AccessToken");
    if (token) {
      const decoded = jwtDecode(token);
      setRole(decoded.scope);
    } else {
      navigate("/auth/login");
    }
  }, [navigate]);

  const fetchUser = async () => {
    const data = await getInfo({});
    setUser(data);
  };

  useEffect(() => {
    fetchUser();
  }, []);

  // Frontend route protection
  useEffect(() => {
    if (
      role &&
      location.pathname !== "/auth/login" &&
      location.pathname !== "/verify"
    ) {
      // Don't protect auth/verify pages
      const allowedPaths = rolePermissions[role] || [];
      // Check if the current path (or its base path if it's a detail page like /diligenceHr/detail/1) is allowed
      const isPathAllowed = allowedPaths.some((allowedPath) => {
        if (allowedPath.includes(":")) {
          // Handle dynamic routes like /diligenceHr/detail/:internId
          const regex = new RegExp(
            `^${allowedPath.replace(/:\w+/g, "[^/]+")}$`
          );
          return regex.test(location.pathname);
        }
        return location.pathname.startsWith(allowedPath);
      });

      if (!isPathAllowed) {
        // If the current path is not allowed for the user's role, redirect to home or an unauthorized page
        navigate("/");
      }
    }
  }, [role, location.pathname, navigate]);

  useEffect(() => {
    if (role) {
      const allowedPaths = new Set(rolePermissions[role] || []);
      const finalMenu = menuItems.filter(item => {
        // If it's a direct link, check if its path is allowed
        if (item.path) {
          return allowedPaths.has(item.path);
        }
        // If it has children, check if at least one child's path is allowed
        if (item.children) {
          return item.children.some(child => allowedPaths.has(child.path));
        }
        return false;
      });
      setFilteredMenu(finalMenu);
    }
  }, [role]);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const handleLogout = () => {
    localStorage.clear("AccessToken");
    navigate("/auth/login");
  };

  return (
    <>
      <div className="layout-container">
        {/* Sidebar */}
        <aside className={`sidebar ${sidebarOpen ? "open" : "closed"}`}>
          <div className="sidebar-header">
            {sidebarOpen && (
              <div className="logo">
                <div className="logo-icon">IS</div>
                <span className="logo-text">INTERNSHIP</span>
              </div>
            )}
            {!sidebarOpen && <div className="logo-icon">IS</div>}
          </div>

          <nav className="sidebar-nav">
            {filteredMenu.map((item) => {
              if (item.children) {
                return (
                  <SubMenuItem
                    key={item.id}
                    item={item}
                    sidebarOpen={sidebarOpen}
                    location={location}
                  />
                );
              }
              
              const Icon = item.icon;
              const isActive = location.pathname === item.path;

              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`nav-item ${isActive ? "active" : ""}`}
                  title={!sidebarOpen ? item.label : ""}
                >
                  <Icon className="nav-icon" size={20} />
                  {sidebarOpen && (
                    <span className="nav-label">{item.label}</span>
                  )}
                </Link>
              );
            })}
          </nav>
        </aside>

        {/* Main Content */}
        <div className="main-wrapper">
          {/* Header */}
          <header className="header">
            <div className="header-left">
              <button className="menu-toggle" onClick={toggleSidebar}>
                {sidebarOpen ? <X size={24} /> : <Menu size={24} />}
              </button>
            </div>

            <div className="header-actions">
              <button className="icon-button">
                <Bell size={20} />
                <span className="notification-badge"></span>
              </button>
              <div
                className={`user-profile ${showMenu ? "active" : ""}`}
                onClick={() => toggleUserMenu()}
              >
                <div className="avatar">
                  <img src={user.avatarUrl || "src/assets/avatarDefault.jpg"} />
                </div>
                <span className="user-name">{user.fullName}</span>

                {showMenu && (
                  <div className="user-dropdown">
                    <button
                      className="dropdown-item"
                      onClick={() => setShowInfo(true)}
                    >
                      <User size={18} style={{ marginRight: 8 }} />
                      Thông tin tài khoản
                    </button>

                    <button
                      className="dropdown-item"
                      onClick={() => setShowChangePassword(true)}
                    >
                      <KeyRound size={18} style={{ marginRight: 8 }} />
                      Đổi mật khẩu
                    </button>

                    <button
                      className="dropdown-item"
                      onClick={handleLogout}
                      style={{ color: "#dc2626" }}
                    >
                      <LogOut size={18} style={{ marginRight: 8 }} /> Logout
                    </button>
                  </div>
                )}
              </div>
            </div>
          </header>

          {/* Page Content */}
          <main className="main-content">
            <Outlet context={{ user }} />
          </main>
        </div>
      </div>

      {showInfo && (
        <UserInfo
          user={user}
          setUser={setUser}
          onClose={() => setShowInfo(false)}
        />
      )}
      {showChangePassword && (
        <ChangePassword onClose={() => setShowChangePassword(false)} />
      )}
    </>
  );
};

export default Layout;
