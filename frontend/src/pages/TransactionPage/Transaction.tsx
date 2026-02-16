import React, { useEffect, useState } from 'react';
import { useNavigate, NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { transactionService } from '../../services/transactionService';
import { dashboardService } from '../../services/dashboardService';
import {
  HiWallet,
  HiBell,
  HiPlus,
  HiMagnifyingGlass,
  HiPencilSquare,
  HiTrash,
  HiDocumentArrowDown,
} from 'react-icons/hi2';
import '../DashboardPage/Dashboard.css';
import './Transactions.css';

interface TransactionItem {
  id: number;
  description: string;
  amount: number;
  date: string;
  categoryId: number;
  categoryName: string;
  categoryType: string;
  currency?: string;
}

interface Category {
  id: number;
  name: string;
  type: string;
}

export default function Transactions() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [transactions, setTransactions] = useState<TransactionItem[]>([]);
    const [categoriesIncome, setCategoriesIncome] = useState<Category[]>([]);
    const [categoriesExpense, setCategoriesExpense] = useState<Category[]>([]);
    const [stats, setStats] = useState<{ totalIncome: number; totalExpense: number; balance: number } | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [search, setSearch] = useState('');
    const [filterCategory, setFilterCategory] = useState<string>('');
    const [filterPeriod, setFilterPeriod] = useState<string>('30');
    const [page, setPage] = useState(1);
    const [modalOpen, setModalOpen] = useState(false);
    const [formType, setFormType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');
    const [formAmount, setFormAmount] = useState('');
    const [formCategoryId, setFormCategoryId] = useState<string>('');
    const [formDate, setFormDate] = useState(() => new Date().toISOString().slice(0, 10));
    const [formDescription, setFormDescription] = useState('');
    const [formSubmitting, setFormSubmitting] = useState(false);
    const [formError, setFormError] = useState('');
  
    const perPage = 10;
  
    useEffect(() => {
      setLoading(true);
      setError('');
      Promise.all([
        transactionService.getTransactions(),
        transactionService.getCategories('INCOME'),
        transactionService.getCategories('EXPENSE'),
        dashboardService.getStatistics(),
      ])
        .then(([txList, catIn, catExp, statistics]) => {
          setTransactions(txList || []);
          setCategoriesIncome(catIn || []);
          setCategoriesExpense(catExp || []);
          setStats({
            totalIncome: Number(statistics?.totalIncome ?? 0),
            totalExpense: Number(statistics?.totalExpense ?? 0),
            balance: Number(statistics?.balance ?? 0),
          });
        })
        .catch(() => setError('Could not load data.'))
        .finally(() => setLoading(false));
    }, []);
  
    const handleLogout = () => {
      logout();
      navigate('/login');
    };
  
    const formatMoney = (value: number | string | null | undefined) =>
      Number(value ?? 0).toFixed(2);
  
    const filteredTransactions = transactions.filter((tx) => {
      const matchSearch =
        !search ||
        (tx.description || '').toLowerCase().includes(search.toLowerCase()) ||
        (tx.categoryName || '').toLowerCase().includes(search.toLowerCase());
      const matchCategory =
        !filterCategory ||
        String(tx.categoryId) === filterCategory ||
        tx.categoryName === filterCategory;
      if (!matchSearch || !matchCategory) return false;
      if (filterPeriod === '30') {
        const d = new Date(tx.date);
        const now = new Date();
        const diff = (now.getTime() - d.getTime()) / (1000 * 60 * 60 * 24);
        return diff <= 30;
      }
      if (filterPeriod === 'month') {
        const d = new Date(tx.date);
        const now = new Date();
        return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear();
      }
      if (filterPeriod === 'lastMonth') {
        const d = new Date(tx.date);
        const now = new Date();
        const last = new Date(now.getFullYear(), now.getMonth() - 1);
        return d.getMonth() === last.getMonth() && d.getFullYear() === last.getFullYear();
      }
      if (filterPeriod === 'year') {
        const d = new Date(tx.date);
        return d.getFullYear() === new Date().getFullYear();
      }
      return true;
    });
  
    const totalFiltered = filteredTransactions.length;
    const paginated = filteredTransactions.slice((page - 1) * perPage, page * perPage);
    const totalPages = Math.max(1, Math.ceil(totalFiltered / perPage));
  
    const openModal = () => {
      setFormType('EXPENSE');
      setFormAmount('');
      setFormCategoryId(categoriesExpense[0]?.id ? String(categoriesExpense[0].id) : '');
      setFormDate(new Date().toISOString().slice(0, 10));
      setFormDescription('');
      setFormError('');
      setModalOpen(true);
    };
  
    const closeModal = () => {
      setModalOpen(false);
      setFormError('');
    };
  
    const currentCategories = formType === 'INCOME' ? categoriesIncome : categoriesExpense;
  
    const handleSubmitTransaction = async (e: React.FormEvent) => {
      e.preventDefault();
      setFormError('');
      const amount = parseFloat(formAmount);
      if (isNaN(amount) || amount < 0.01) {
        setFormError('Amount must be at least 0.01.');
        return;
      }
      const categoryId = formCategoryId ? Number(formCategoryId) : currentCategories[0]?.id;
      if (!categoryId) {
        setFormError('Please select a category.');
        return;
      }
      const amountToSend = Math.abs(amount);
      setFormSubmitting(true);
      try {
        await transactionService.createTransaction({
          description: formDescription || 'Transaction',
          amount: amountToSend,
          currency: 'USD',
          date: formDate,
          categoryId,
        });
        const updated = await transactionService.getTransactions();
        setTransactions(updated || []);
        const statsRes = await dashboardService.getStatistics();
        setStats({
          totalIncome: Number(statsRes?.totalIncome ?? 0),
          totalExpense: Number(statsRes?.totalExpense ?? 0),
          balance: Number(statsRes?.balance ?? 0),
        });
        closeModal();
      } catch (err: any) {
        setFormError(err.response?.data?.message || 'Failed to save transaction.');
      } finally {
        setFormSubmitting(false);
      }
    };
  
    const handleDelete = async (id: number) => {
      if (!window.confirm('Delete this transaction?')) return;
      try {
        await transactionService.deleteTransaction(id);
        setTransactions((prev) => prev.filter((t) => t.id !== id));
        const statsRes = await dashboardService.getStatistics();
        setStats({
          totalIncome: Number(statsRes?.totalIncome ?? 0),
          totalExpense: Number(statsRes?.totalExpense ?? 0),
          balance: Number(statsRes?.balance ?? 0),
        });
      } catch {
        setError('Failed to delete.');
      }
    };
    return (
        <div className="dashboard-page transactions-page">
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
                  <button type="button" className="nav-logout-btn" onClick={handleLogout}>
                    Log out
                  </button>
                </div>
              </div>
            </div>
          </nav>
    
          <main className="dashboard-main transactions-main">
            <div className="transactions-content">
            {loading && <p className="transactions-loading">Loading…</p>}
          {error && <p className="transactions-error">{error}</p>}
          {!loading && !error && (
            <>
              <div className="transactions-header">
                <div>
                  <h1 className="transactions-title">Transactions</h1>
                  <p className="transactions-subtitle">
                    Manage your income and expenses efficiently.
                  </p>
                </div>
                <div className="transactions-actions">
                  <button type="button" className="transactions-export-btn">
                    <HiDocumentArrowDown />
                    Export
                  </button>
                  <button
                    type="button"
                    className="transactions-add-btn"
                    onClick={openModal}
                  >
                    <HiPlus />
                    Add Transaction
                  </button>
                </div>
              </div>

              <div className="transactions-summary">
                <div className="transactions-summary-card">
                  <p className="transactions-summary-label">Total Income</p>
                  <p className="transactions-summary-value transactions-summary-income">
                    +${formatMoney(stats?.totalIncome)}
                  </p>
                </div>
                <div className="transactions-summary-card">
                  <p className="transactions-summary-label">Total Expenses</p>
                  <p className="transactions-summary-value transactions-summary-expense">
                    -${formatMoney(Math.abs(Number(stats?.totalExpense)))}
                  </p>
                </div>
                <div className="transactions-summary-card">
                  <p className="transactions-summary-label">Net Balance</p>
                  <p className="transactions-summary-value">
                    ${formatMoney(stats?.balance)}
                  </p>
                </div>
              </div>

              <div className="transactions-filters">
                <div className="transactions-search-wrap">
                  <HiMagnifyingGlass className="transactions-search-icon" />
                  <input
                    type="text"
                    placeholder="Search transactions..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    className="transactions-search-input"
                  />
                </div>
                <div className="transactions-filters-row">
                  <select
                    value={filterCategory}
                    onChange={(e) => setFilterCategory(e.target.value)}
                    className="transactions-select"
                  >
                    <option value="">All Categories</option>
                    {Array.from(
                      new Set(transactions.map((t) => t.categoryName))
                    ).map((name) => (
                      <option key={name} value={name}>
                        {name}
                      </option>
                    ))}
                  </select>
                  <select
                    value={filterPeriod}
                    onChange={(e) => setFilterPeriod(e.target.value)}
                    className="transactions-select"
                  >
                    <option value="30">Last 30 Days</option>
                    <option value="month">This Month</option>
                    <option value="lastMonth">Last Month</option>
                    <option value="year">This Year</option>
                    <option value="all">All time</option>
                  </select>
                </div>
              </div>
              <div className="transactions-table-card">
                <div className="transactions-table-wrap">
                  <table className="transactions-table">
                    <thead>
                      <tr>
                        <th>Date</th>
                        <th>Description</th>
                        <th>Category</th>
                        <th className="transactions-th-amount">Amount</th>
                        <th className="transactions-th-actions">Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {paginated.map((tx) => (
                        <tr key={tx.id} className="transactions-row">
                          <td>{tx.date}</td>
                          <td>
                            <div className="transactions-desc-cell">
                              <span
                                className={`transactions-type-icon transactions-type-icon-${tx.categoryType === 'INCOME' ? 'income' : 'expense'}`}
                              >
                                {tx.categoryType === 'INCOME' ? '+' : '−'}
                              </span>
                              <span>{tx.description || '—'}</span>
                            </div>
                          </td>
                          <td>
                            <span className="transactions-category-badge">
                              {tx.categoryName}
                            </span>
                          </td>
                          <td className="transactions-amount-cell">
                            <span
                              className={
                                tx.categoryType === 'INCOME'
                                  ? 'transactions-amount-income'
                                  : 'transactions-amount-expense'
                              }
                            >
                              {tx.categoryType === 'INCOME' ? '+' : '-'}$
                              {formatMoney(Math.abs(Number(tx.amount)))}
                            </span>
                          </td>
                          <td className="transactions-actions-cell">
                            <button
                              type="button"
                              className="transactions-btn-icon"
                              title="Edit"
                              onClick={() => {}}
                            >
                              <HiPencilSquare />
                            </button>
                            <button
                              type="button"
                              className="transactions-btn-icon transactions-btn-delete"
                              title="Delete"
                              onClick={() => handleDelete(tx.id)}
                            >
                              <HiTrash />
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <div className="transactions-mobile-list">
                  {paginated.map((tx) => (
                    <div key={tx.id} className="transactions-mobile-item">
                      <div className="transactions-mobile-item-top">
                        <div className="transactions-mobile-item-left">
                          <span
                            className={`transactions-type-icon transactions-type-icon-${tx.categoryType === 'INCOME' ? 'income' : 'expense'}`}
                          >
                            {tx.categoryType === 'INCOME' ? '+' : '−'}
                          </span>
                          <div>
                            <p className="transactions-mobile-desc">
                              {tx.description || '—'}
                            </p>
                            <p className="transactions-mobile-date">{tx.date}</p>
                          </div>
                        </div>
                        <span
                          className={
                            tx.categoryType === 'INCOME'
                              ? 'transactions-amount-income'
                              : 'transactions-amount-expense'
                          }
                        >
                          {tx.categoryType === 'INCOME' ? '+' : '-'}$
                          {formatMoney(Math.abs(Number(tx.amount)))}
                        </span>
                      </div>
                      <div className="transactions-mobile-item-bottom">
                        <span className="transactions-category-badge">
                          {tx.categoryName}
                        </span>
                        <div>
                          <button
                            type="button"
                            className="transactions-btn-icon"
                            onClick={() => {}}
                          >
                            <HiPencilSquare />
                          </button>
                          <button
                            type="button"
                            className="transactions-btn-icon transactions-btn-delete"
                            onClick={() => handleDelete(tx.id)}
                          >
                            <HiTrash />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                <div className="transactions-pagination">
                  <p className="transactions-pagination-info">
                    Showing{' '}
                    <span>
                      {totalFiltered === 0
                        ? 0
                        : (page - 1) * perPage + 1}
                    </span>
                    -<span>{Math.min(page * perPage, totalFiltered)}</span> of{' '}
                    <span>{totalFiltered}</span> results
                  </p>
                  <div className="transactions-pagination-btns">
                    <button
                      type="button"
                      className="transactions-page-btn"
                      disabled={page <= 1}
                      onClick={() => setPage((p) => p - 1)}
                    >
                      ←
                    </button>
                    {Array.from({ length: totalPages }, (_, i) => i + 1).map(
                      (p) => (
                        <button
                          key={p}
                          type="button"
                          className={`transactions-page-btn ${p === page ? 'transactions-page-btn-active' : ''}`}
                          onClick={() => setPage(p)}
                        >
                          {p}
                        </button>
                      )
                    )}
                    <button
                      type="button"
                      className="transactions-page-btn"
                      disabled={page >= totalPages}
                      onClick={() => setPage((p) => p + 1)}
                    >
                      →
                    </button>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </main>
      {modalOpen && (
        <div className="transactions-modal-overlay" onClick={closeModal}>
          <div
            className="transactions-modal"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="transactions-modal-header">
              <h3 className="transactions-modal-title">Add Transaction</h3>
              <button
                type="button"
                className="transactions-modal-close"
                onClick={closeModal}
              >
                ×
              </button>
            </div>
            <form onSubmit={handleSubmitTransaction} className="transactions-modal-form">
              <div className="transactions-type-tabs">
                <label className="transactions-type-tab">
                  <input
                    type="radio"
                    name="type"
                    checked={formType === 'EXPENSE'}
                    onChange={() => {
                      setFormType('EXPENSE');
                      setFormCategoryId(
                        categoriesExpense[0]?.id
                          ? String(categoriesExpense[0].id)
                          : ''
                      );
                    }}
                  />
                  <span className={formType === 'EXPENSE' ? 'active expense' : ''}>
                    Expense
                  </span>
                </label>
                <label className="transactions-type-tab">
                  <input
                    type="radio"
                    name="type"
                    checked={formType === 'INCOME'}
                    onChange={() => {
                      setFormType('INCOME');
                      setFormCategoryId(
                        categoriesIncome[0]?.id
                          ? String(categoriesIncome[0].id)
                          : ''
                      );
                    }}
                  />
                  <span className={formType === 'INCOME' ? 'active income' : ''}>
                    Income
                  </span>
                </label>
              </div>
              {formError && (
                <p className="transactions-form-error">{formError}</p>
              )}
              <div className="transactions-form-group">
                <label htmlFor="amount">Amount</label>
                <div className="transactions-input-amount-wrap">
                  <span className="transactions-input-prefix">$</span>
                  <input
                    id="amount"
                    type="number"
                    step="0.01"
                    min="0.01"
                    placeholder="0.00"
                    value={formAmount}
                    onChange={(e) => setFormAmount(e.target.value)}
                    className="transactions-input-amount"
                  />
                  <span className="transactions-input-suffix">USD</span>
                </div>
              </div>
              <div className="transactions-form-row">
                <div className="transactions-form-group">
                  <label htmlFor="category">Category</label>
                  <select
                    id="category"
                    value={formCategoryId}
                    onChange={(e) => setFormCategoryId(e.target.value)}
                    className="transactions-form-select"
                  >
                    {currentCategories.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="transactions-form-group">
                  <label htmlFor="date">Date</label>
                  <input
                    id="date"
                    type="date"
                    value={formDate}
                    onChange={(e) => setFormDate(e.target.value)}
                    className="transactions-form-input"
                  />
                </div>
              </div>
              <div className="transactions-form-group">
                <label htmlFor="description">Description</label>
                <input
                  id="description"
                  type="text"
                  placeholder="e.g. Weekly grocery run"
                  value={formDescription}
                  onChange={(e) => setFormDescription(e.target.value)}
                  className="transactions-form-input"
                />
              </div>
              <div className="transactions-modal-footer">
                <button
                  type="button"
                  className="transactions-modal-cancel"
                  onClick={closeModal}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="transactions-modal-submit"
                  disabled={formSubmitting}
                >
                  {formSubmitting ? 'Saving…' : 'Save Transaction'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}