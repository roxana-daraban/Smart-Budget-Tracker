import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar/Navbar';
import '../DashboardPage/Dashboard.css';
import './Profile.css';

export default function Profile() {
  const { user, updateProfile } = useAuth();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (user) {
      setUsername(user.username ?? '');
      setEmail(user.email ?? '');
    }
  }, [user]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess(false);
    setLoading(true);
    try {
      const result = await updateProfile({ username: username.trim(), email: email.trim() });
      if (result.success) {
        setSuccess(true);
      } else {
        setError(result.error ?? 'Update failed');
      }
    } catch (_) {
      setError('Update failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-page profile-page">
      <Navbar />
      <main className="dashboard-main profile-main">
        <div className="profile-content">
          <h1 className="profile-title">Profile</h1>
          <p className="profile-subtitle">Manage your account details.</p>

          <div className="profile-card">
            <h2 className="profile-card-title">Account details</h2>
            <form className="profile-form" onSubmit={handleSubmit}>
              {error && <p className="profile-form-error">{error}</p>}
              {success && <p className="profile-form-success">Profile updated successfully.</p>}
              <div className="profile-form-row">
                <label className="profile-details-label" htmlFor="profile-username">Username</label>
                <input
                  id="profile-username"
                  type="text"
                  className="profile-form-input"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  minLength={3}
                  maxLength={50}
                  required
                />
              </div>
              <div className="profile-form-row">
                <label className="profile-details-label" htmlFor="profile-email">Email</label>
                <input
                  id="profile-email"
                  type="email"
                  className="profile-form-input"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="profile-form-submit" disabled={loading}>
                {loading ? 'Saving…' : 'Save changes'}
              </button>
            </form>
          </div>
        </div>
      </main>
    </div>
  );
}