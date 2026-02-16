import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/LoginPage/Login';
import Dashboard from './pages/LoginPage/DashboardPage/Dashboard';

/** Protejează rutele care cer autentificare. Dacă user nu e logat, redirect la /login. */
function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return null; // sau un spinner
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/login" element={<Login />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route path="/register" element={<div style={{ padding: '2rem', textAlign: 'center' }}>Register – în curând</div>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}