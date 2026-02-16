import React, { useState, FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { HiUser, HiEnvelope, HiLockClosed, HiEye, HiEyeSlash } from 'react-icons/hi2';
import { HiWallet, HiShieldCheck } from 'react-icons/hi2';
import './Register.css';

export default function Register() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (!termsAccepted) {
      setError('You must accept the Terms of Service and Privacy Policy.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    setLoading(true);
    const result = await register(username, email, password);
    setLoading(false);

    if (result.success) navigate('/dashboard');
    else setError(result.error || 'Registration failed.');
  };

  return (
    <div className="register-page">
      <div className="register-container">
        {/* Left: Branding */}
        <div className="register-branding">
          <div className="register-branding-overlay" />
          <div className="register-branding-content">
            <div className="register-branding-header">
              <div className="register-branding-icon-wrap">
                <HiWallet className="register-branding-icon" />
              </div>
              <h1 className="register-branding-title">Smart Budget</h1>
            </div>
            <h2 className="register-branding-heading">
              Take control of your financial future today.
            </h2>
            <p className="register-branding-text">
              Join thousands of users who are tracking expenses, saving more, and hitting their financial goals with ease.
            </p>
          </div>
          <div className="register-branding-footer">
            <div className="register-chart-card">
              <div className="register-chart-bars">
                <div className="register-chart-bar" style={{ height: '40%' }} />
                <div className="register-chart-bar" style={{ height: '65%' }} />
                <div className="register-chart-bar" style={{ height: '50%' }} />
                <div className="register-chart-bar" style={{ height: '80%' }} />
                <div className="register-chart-bar register-chart-bar-highlight" style={{ height: '95%' }} />
              </div>
              <div className="register-chart-label">
                <span>Monthly Savings</span>
                <span className="register-chart-value">+24%</span>
              </div>
            </div>
            <div className="register-security">
              <HiShieldCheck className="register-security-icon" />
              <span>Bank-level security encryption</span>
            </div>
          </div>
        </div>

        {/* Right: Form */}
        <div className="register-form-side">
          <div className="register-mobile-logo">
            <HiWallet className="register-mobile-logo-icon" />
            <span className="register-mobile-logo-text">Smart Budget</span>
          </div>
          <div className="register-form-wrap">
            <h2 className="register-title">Create Account</h2>
            <p className="register-subtitle">Start managing your budget in seconds.</p>

            {error && (
              <div className="register-error">
                <p className="register-error-text">{error}</p>
              </div>
            )}

            <form onSubmit={handleSubmit} className="register-form">
              <div className="register-form-group">
                <label className="register-form-label" htmlFor="username">Username</label>
                <div className="register-input-wrap">
                  <div className="register-input-icon">
                    <HiUser />
                  </div>
                  <input
                    id="username"
                    name="username"
                    type="text"
                    placeholder="johndoe"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="register-form-input"
                    required
                  />
                </div>
              </div>

              <div className="register-form-group">
                <label className="register-form-label" htmlFor="email">Email Address</label>
                <div className="register-input-wrap">
                  <div className="register-input-icon">
                    <HiEnvelope />
                  </div>
                  <input
                    id="email"
                    name="email"
                    type="email"
                    placeholder="john@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="register-form-input"
                    required
                  />
                </div>
              </div>

              <div className="register-form-row">
                <div className="register-form-group">
                  <label className="register-form-label" htmlFor="password">Password</label>
                  <div className="register-input-wrap">
                    <div className="register-input-icon">
                      <HiLockClosed />
                    </div>
                    <input
                      id="password"
                      name="password"
                      type={showPassword ? 'text' : 'password'}
                      placeholder="••••••••"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="register-form-input register-form-input-password"
                      required
                    />
                    <button
                      type="button"
                      className="register-password-toggle"
                      onClick={() => setShowPassword(!showPassword)}
                      aria-label={showPassword ? 'Hide password' : 'Show password'}
                    >
                      {showPassword ? <HiEyeSlash /> : <HiEye />}
                    </button>
                  </div>
                </div>
                <div className="register-form-group">
                  <label className="register-form-label" htmlFor="confirm-password">Confirm Password</label>
                  <div className="register-input-wrap">
                    <div className="register-input-icon">
                      <HiLockClosed />
                    </div>
                    <input
                      id="confirm-password"
                      name="confirmPassword"
                      type="password"
                      placeholder="••••••••"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className="register-form-input"
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="register-terms">
                <input
                  id="terms"
                  name="terms"
                  type="checkbox"
                  checked={termsAccepted}
                  onChange={(e) => setTermsAccepted(e.target.checked)}
                  className="register-terms-checkbox"
                />
                <label htmlFor="terms" className="register-terms-label">
                  I agree to the <a className="register-terms-link" href="#">Terms of Service</a> and{' '}
                  <a className="register-terms-link" href="#">Privacy Policy</a>
                </label>
              </div>

              <button
                type="submit"
                className="register-submit-btn"
                disabled={loading}
              >
                {loading ? 'Creating…' : 'Create Account'}
              </button>
            </form>

            <div className="register-divider">
              <span className="register-divider-text">Or continue with</span>
            </div>
            <div className="register-social">
              <button type="button" className="register-social-btn">
                <img src="https://www.google.com/favicon.ico" alt="" className="register-social-icon" />
                Google
              </button>
              <button type="button" className="register-social-btn">
                <img src="https://www.linkedin.com/favicon.ico" alt="" className="register-social-icon" />
                LinkedIn
              </button>
            </div>

            <p className="register-login-link">
              Already have an account? <Link to="/login" className="register-login-link-a">Log in</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}