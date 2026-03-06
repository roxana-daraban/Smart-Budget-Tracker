import React from 'react';
import { useNavigate, NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { HiWallet, HiBell } from 'react-icons/hi2';

/**
 * Navbar comun pentru Dashboard, Transactions, Profile.
 * Pagina care îl folosește trebuie să importe Dashboard.css ca navbar-ul să aibă stiluri.
 */
export default function AppNavbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="dashboard-nav">
      <div className="dashboard-nav-container">
        <div className="dashboard-nav-left">
          <div className="dashboard-logo">
            <div className="logo-icon">
              <HiWallet />
            </div>
            <span className="logo-text">BudgetSmart</span>
          </div>
          <div className="dashboard-nav-links">
            <NavLink
              to="/dashboard"
              className={({ isActive }) =>
                isActive ? 'nav-link nav-link-active' : 'nav-link'
              }
            >
              Dashboard
            </NavLink>
            <NavLink
              to="/transactions"
              className={({ isActive }) =>
                isActive ? 'nav-link nav-link-active' : 'nav-link'
              }
            >
              Transactions
            </NavLink>
            <a className="nav-link" href="#">Reports</a>
            <NavLink
              to="/profile"
              className={({ isActive }) =>
                isActive ? 'nav-link nav-link-active' : 'nav-link'
              }
            >
              Profile
            </NavLink>
          </div>
        </div>
        <div className="dashboard-nav-right">
          <button className="nav-notification-btn" type="button">
            <span className="sr-only">View notifications</span>
            <HiBell />
          </button>
          <div className="nav-user">
            <div className="nav-user-info">
              <p className="nav-user-name">
                {user?.username || user?.email || 'User'}
              </p>
              <p className="nav-user-role">Member</p>
            </div>
            <button
              type="button"
              className="nav-logout-btn"
              onClick={handleLogout}
            >
              Log out
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
}