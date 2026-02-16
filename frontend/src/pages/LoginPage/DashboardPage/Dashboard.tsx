import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../context/AuthContext';
import { dashboardService } from '../../../services/dashboardService';
import './Dashboard.css';
import {
  HiArrowUp,
  HiArrowDown,
  HiWallet,
  HiBell,
  HiCalendar,
  HiChevronDown,
  HiPlus,
  HiArrowTrendingUp,
  HiArrowTrendingDown,
  HiBanknotes,
} from "react-icons/hi2";

interface DashboardStatistics {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  expensesByCategory?: Array<{
    categoryName: string;
    categoryId: number;
    totalAmount: number;
  }>;
}

interface Transaction {
  id: number;
  description: string;
  amount: number;
  date: string;
  categoryType: string;
}

export default function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [statistics, setStatistics] = useState<DashboardStatistics | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    setError('');
    Promise.all([
      dashboardService.getStatistics(),
      dashboardService.getTransactions(),
    ])
      .then(([stats, txList]) => {
        setStatistics(stats);
        setTransactions(txList || []);
      })
      .catch(() => setError('Nu s-au putut încărca datele.'))
      .finally(() => setLoading(false));
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const formatMoney = (value: number | string | null | undefined) =>
    Number(value ?? 0).toFixed(2);

  const totalExpenseForChart =
    statistics?.expensesByCategory?.reduce(
      (sum: number, c: { totalAmount: number }) =>
        sum + Number(c.totalAmount ?? 0),
      0
    ) ?? 0;

  const CHART_COLORS = ['#1c5cf2', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

  return (
    <div className="dashboard-page">
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
              <a className="nav-link nav-link-active" href="#">Dashboard</a>
              <a className="nav-link" href="#">Transactions</a>
              <a className="nav-link" href="#">Reports</a>
              <a className="nav-link" href="#">Settings</a>
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

      <main className="dashboard-main">
        <div className="dashboard-content">
          {loading && (
            <p className="dashboard-loading">Se încarcă datele…</p>
          )}
          {error && <p className="dashboard-error">{error}</p>}
          {!loading && !error && (
            <>
              <div className="dashboard-header">
                <div className="dashboard-header-left">
                  <h2 className="dashboard-title">Financial Overview</h2>
                  <p className="dashboard-subtitle">
                    Track your spending and manage your wealth effectively.
                  </p>
                </div>
                <div className="dashboard-header-right">
                  <div className="date-picker-wrapper">
                    <button className="date-picker-btn" type="button">
                      <span className="date-picker-content">
                        <HiCalendar className="date-picker-icon" />
                        Luna curentă
                      </span>
                      <HiChevronDown className="date-picker-arrow" />
                    </button>
                  </div>
                  <button className="add-transaction-btn" type="button">
                    <HiPlus className="add-transaction-icon" />
                    Add Transaction
                  </button>
                </div>
              </div>

              <div className="stats-grid">
                <div className="stat-card">
                  <div className="stat-card-content">
                  <div className="stat-card-icon stat-card-icon-income">
  <HiArrowUp />
</div>
                    <div className="stat-card-info">
                      <dt className="stat-card-label">Total Income</dt>
                      <dd className="stat-card-value">
                        ${formatMoney(statistics?.totalIncome)}
                      </dd>
                    </div>
                  </div>
                  <div className="stat-card-footer stat-card-footer-income">
                    <div className="stat-card-trend stat-card-trend-up">
                      <HiArrowTrendingUp className="trend-icon" />
                      <span className="trend-text">Luna curentă</span>
                    </div>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-card-content">
                  <div className="stat-card-icon stat-card-icon-expense">
  <HiArrowDown />
</div>
                    <div className="stat-card-info">
                      <dt className="stat-card-label">Total Expenses</dt>
                      <dd className="stat-card-value">
                        ${formatMoney(statistics?.totalExpense)}
                      </dd>
                    </div>
                  </div>
                  <div className="stat-card-footer stat-card-footer-expense">
                    <div className="stat-card-trend stat-card-trend-down">
                      <HiArrowTrendingDown className="trend-icon" />
                      <span className="trend-text">Luna curentă</span>
                    </div>
                  </div>
                </div>

                <div className="stat-card stat-card-balance">
                  <div className="balance-card-bg balance-card-bg-1"></div>
                  <div className="balance-card-bg balance-card-bg-2"></div>
                  <div className="stat-card-content">
                    <div className="stat-card-icon stat-card-icon-balance">
                      <HiBanknotes />
                    </div>
                    <div className="stat-card-info">
                      <dt className="stat-card-label stat-card-label-balance">
                        Total Balance
                      </dt>
                      <dd className="stat-card-value stat-card-value-balance">
                        ${formatMoney(statistics?.balance)}
                      </dd>
                    </div>
                  </div>
                  <div className="stat-card-footer stat-card-footer-balance">
                    <div className="stat-card-trend stat-card-trend-balance">
                      <HiBanknotes className="trend-icon" />
                      Sold curent
                    </div>
                  </div>
                </div>
              </div>

              <div className="dashboard-grid">
                <div className="chart-section">
                  <div className="chart-header">
                    <h3 className="chart-title">Expenses by Category</h3>
                    <button className="chart-view-btn">View Full Report</button>
                  </div>
                  <div className="chart-content">
                    <div className="chart-donut-wrapper">
                      <div className="chart-donut">
                        <div className="chart-donut-inner">
                          <span className="chart-donut-label">
                            Total Spent
                          </span>
                          <span className="chart-donut-value">
                            ${formatMoney(totalExpenseForChart)}
                          </span>
                        </div>
                      </div>
                    </div>
                    <div className="chart-legend">
                      {statistics?.expensesByCategory?.length ? (
                        statistics.expensesByCategory.map(
                          (
                            cat: {
                              categoryName: string;
                              categoryId: number;
                              totalAmount: number;
                            },
                            idx: number
                          ) => {
                            const pct =
                              totalExpenseForChart > 0
                                ? (
                                    (Number(cat.totalAmount) /
                                      totalExpenseForChart) *
                                    100
                                  ).toFixed(0)
                                : '0';
                            return (
                              <div
                                key={cat.categoryId ?? idx}
                                className="legend-item"
                              >
                                <span
                                  className="legend-color"
                                  style={{
                                    backgroundColor: CHART_COLORS[idx % 5],
                                  }}
                                />
                                <div>
                                  <p className="legend-name">
                                    {cat.categoryName}
                                  </p>
                                  <p className="legend-detail">
                                    {pct}% • $
                                    {formatMoney(cat.totalAmount)}
                                  </p>
                                </div>
                              </div>
                            );
                          }
                        )
                      ) : (
                        <p className="legend-name">
                          Nicio cheltuială în această perioadă
                        </p>
                      )}
                    </div>
                  </div>
                </div>

                <div className="activity-section">
                  <div className="activity-header">
                    <h3 className="activity-title">Recent Activity</h3>
                    <button className="activity-view-btn">View All</button>
                  </div>
                  <div className="activity-list-wrapper">
                    <ul className="activity-list">
                      {transactions.length === 0 ? (
                        <li className="activity-item">
                          <p className="activity-name">Nicio tranzacție</p>
                        </li>
                      ) : (
                        transactions.slice(0, 5).map(
                          (tx: {
                            id: number;
                            description: string;
                            amount: number;
                            date: string;
                            categoryType: string;
                          }) => (
                            <li key={tx.id} className="activity-item">
                              <div className="activity-item-left">
                                <div
                                  className={
                                    tx.categoryType === 'INCOME'
                                      ? 'activity-icon activity-icon-3'
                                      : 'activity-icon activity-icon-1'
                                  }
                                >
                                  {tx.categoryType === 'INCOME' ? <HiArrowUp /> : <HiArrowDown />}
                                </div>
                                <div>
                                  <p className="activity-name">
                                    {tx.description || 'Tranzacție'}
                                  </p>
                                  <p className="activity-date">{tx.date}</p>
                                </div>
                              </div>
                              <span
                                className={
                                  tx.categoryType === 'INCOME'
                                    ? 'activity-amount activity-amount-income'
                                    : 'activity-amount activity-amount-expense'
                                }
                              >
                                {tx.categoryType === 'INCOME' ? '+' : '-'}$
                                {formatMoney(Math.abs(Number(tx.amount)))}
                              </span>
                            </li>
                          )
                        )
                      )}
                    </ul>
                  </div>
                  <div className="activity-footer">
                    <button
                      className="activity-history-btn"
                      type="button"
                    >
                      View Transaction History
                    </button>
                  </div>
                </div>
              </div>

              <div className="budget-section">
                <h3 className="budget-title">Monthly Budgets</h3>
                <div className="budget-grid">
                  <div className="budget-item">
                    <div className="budget-item-header">
                      <span className="budget-item-name">Groceries</span>
                      <span className="budget-item-amount">$0 / $500</span>
                    </div>
                    <div className="budget-progress-bar">
                      <div
                        className="budget-progress-fill budget-progress-fill-1"
                        style={{ width: '0%' }}
                      />
                    </div>
                  </div>
                  <div className="budget-item">
                    <div className="budget-item-header">
                      <span className="budget-item-name">Dining Out</span>
                      <span className="budget-item-amount">$0 / $200</span>
                    </div>
                    <div className="budget-progress-bar">
                      <div
                        className="budget-progress-fill budget-progress-fill-2"
                        style={{ width: '0%' }}
                      />
                    </div>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </main>
    </div>
  );
}