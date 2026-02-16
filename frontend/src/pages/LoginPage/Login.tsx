import { useState, FormEvent, ChangeEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Login.css';
import React from 'react';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    const result = await login(email, password);
    if (result.success) navigate('/dashboard');
    else setError(result.error || 'Login failed.');
    setLoading(false);
  };

  return (
    <div className="login-page">
      <div className="login-container">
        {/* Left: Branding */}
        <div className="login-branding">
          <div className="branding-overlay" />
          <div className="branding-content">
            <div className="branding-header">
              <span className="material-icons-outlined branding-icon">account_balance_wallet</span>
              <span className="branding-title">SmartBudget</span>
            </div>
            <h2 className="branding-heading">Take control of your <br />financial future.</h2>
            <p className="branding-text">
              Track expenses, set savings goals, and visualize your spending habits all in one secure place.
            </p>
          </div>
          <div className="branding-footer">
            <div className="stars">
              {[1, 2, 3, 4, 5].map((i) => (
                <span key={i} className="material-icons-outlined star">star</span>
              ))}
            </div>
            <p className="testimonial">"The best way to manage personal finances without the headache."</p>
            <div className="testimonial-author">
              <div className="author-avatar"><span className="author-initials">JD</span></div>
              <div>
                <p className="author-name">Jane Doe</p>
                <p className="author-role">Financial Analyst</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right: Form */}
        <div className="login-form-side">
          <div className="mobile-logo">
            <span className="material-icons-outlined mobile-logo-icon">account_balance_wallet</span>
            <span className="mobile-logo-text">SmartBudget</span>
          </div>
          <div className="login-form-wrap">
            <h1 className="login-title">Welcome Back</h1>
            <p className="login-subtitle">Please enter your details to sign in.</p>

            {error && (
              <div className="login-error">
                <p className="login-error-text">{error}</p>
              </div>
            )}

            <form onSubmit={handleSubmit} className="login-form">
              <div className="form-group">
                <label className="form-label" htmlFor="email">Email Address</label>
                <div className="input-wrap">
                  <div className="input-icon"><span className="material-icons-outlined">mail</span></div>
                  <input
                    className="form-input"
                    id="email"
                    type="email"
                    placeholder="name@company.com"
                    value={email}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
                    required
                  />
                </div>
              </div>
              <div className="form-group">
                <div className="label-row">
                  <label className="form-label" htmlFor="password">Password</label>
                  <a className="forgot-link" href="#">Forgot password?</a>
                </div>
                <div className="input-wrap">
                  <div className="input-icon"><span className="material-icons-outlined">lock</span></div>
                  <input
                    className="form-input"
                    id="password"
                    type="password"
                    placeholder="••••••••"
                    value={password}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
                    required
                  />
                </div>
              </div>
              <div className="checkbox-row">
                <input
                  className="form-checkbox"
                  id="remember-me"
                  type="checkbox"
                  checked={rememberMe}
                  onChange={(e: ChangeEvent<HTMLInputElement>) => setRememberMe(e.target.checked)}
                />
                <label className="checkbox-label" htmlFor="remember-me">Remember me for 30 days</label>
              </div>
              <button className="submit-btn" type="submit" disabled={loading}>
                {loading ? 'Signing in...' : 'Sign in'}
              </button>
            </form>

            <p className="register-p">
              Don't have an account? <Link className="register-link" to="/register">Create an account</Link>
            </p>
          </div>
          <div className="legal-links">
            <a className="legal-link" href="#">Privacy Policy</a>
            <a className="legal-link" href="#">Terms of Service</a>
          </div>
        </div>
      </div>
    </div>
  );
}
