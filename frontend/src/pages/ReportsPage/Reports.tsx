import React, { useEffect, useState } from 'react';
import Navbar from '../../components/Navbar/Navbar';
import { useAuth } from '../../context/AuthContext';
import { dashboardService } from '../../services/dashboardService';
import { reportsService } from '../../services/reportsService';
import { formatLocalYmd } from '../../utils/localDate';
import '../DashboardPage/Dashboard.css';
import './Reports.css';
import { HiSparkles } from 'react-icons/hi2';

interface DashboardStatistics {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  expensesByCategory?: Array<{ categoryName: string; totalAmount: number }>;
}

export default function Reports() {
  const { user } = useAuth();
  const baseCurrency = (user?.baseCurrency || 'RON').toUpperCase();

  const now = new Date();
  const [from, setFrom] = useState(() =>
    formatLocalYmd(new Date(now.getFullYear(), now.getMonth(), 1))
  );
  const [to, setTo] = useState(() =>
    formatLocalYmd(new Date(now.getFullYear(), now.getMonth() + 1, 0))
  );

  const [statistics, setStatistics] = useState<DashboardStatistics | null>(null);
  const [statsLoading, setStatsLoading] = useState(true);
  const [statsError, setStatsError] = useState('');

  const [adviceText, setAdviceText] = useState<string | null>(null);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState('');

  useEffect(() => {
    setStatsLoading(true);
    setStatsError('');
    dashboardService
      .getStatistics(from, to)
      .then((stats) => {
        setStatistics({
          totalIncome: Number(stats?.totalIncome ?? 0),
          totalExpense: Number(stats?.totalExpense ?? 0),
          balance: Number(stats?.balance ?? 0),
          expensesByCategory: stats?.expensesByCategory ?? [],
        });
      })
      .catch(() => setStatsError('Nu s-au putut încărca statisticile.'))
      .finally(() => setStatsLoading(false));
  }, [from, to]);

  const formatMoney = (v: number | null | undefined) =>
    Number(v ?? 0).toFixed(2);

  const dateOrderInvalid =
    from &&
    to &&
    from > to;

  const handleAiAdvice = async () => {
    if (dateOrderInvalid) return;
    setAiError('');
    setAdviceText(null);
    setAiLoading(true);
    try {
      const data = await reportsService.getAiAdvice(from, to);
      setAdviceText(data?.adviceText ?? '');
    } catch (err: unknown) {
      const msg =
        err && typeof err === 'object' && 'response' in err
          ? (err as { response?: { data?: { message?: string } } }).response
              ?.data?.message
          : undefined;
      setAiError(msg || 'Nu s-a putut genera analiza AI. Verifică cheia Gemini.');
    } finally {
      setAiLoading(false);
    }
  };

  return (
    <div className="dashboard-page reports-page">
      <Navbar />
      <main className="dashboard-main reports-main">
        <h1 className="reports-title">Reports &amp; AI insights</h1>
        <p className="reports-subtitle">
          Alege perioada, vezi sumarul financiar și generează sfaturi cu Gemini
          (moneda rapoartelor: {baseCurrency}).
        </p>

        <div className="reports-period-row">
          <div className="reports-date-field">
            <label htmlFor="reports-from">De la</label>
            <input
              id="reports-from"
              type="date"
              className="reports-date-input"
              value={from}
              onChange={(e) => setFrom(e.target.value)}
            />
          </div>
          <div className="reports-date-field">
            <label htmlFor="reports-to">Până la</label>
            <input
              id="reports-to"
              type="date"
              className="reports-date-input"
              value={to}
              onChange={(e) => setTo(e.target.value)}
            />
          </div>
        </div>

        {dateOrderInvalid && (
          <p className="reports-error">Data „Până la” trebuie să fie după „De la”.</p>
        )}

        {statsLoading && (
          <p className="reports-loading-stats">Se încarcă sumarul…</p>
        )}
        {statsError && <p className="reports-error">{statsError}</p>}

        {!statsLoading && !statsError && statistics && (
          <div className="reports-stats-grid">
            <div className="reports-stat-card">
              <p className="reports-stat-label">Venituri</p>
              <p className="reports-stat-value">
                {formatMoney(statistics.totalIncome)} {baseCurrency}
              </p>
            </div>
            <div className="reports-stat-card">
              <p className="reports-stat-label">Cheltuieli</p>
              <p className="reports-stat-value">
                {formatMoney(statistics.totalExpense)} {baseCurrency}
              </p>
            </div>
            <div className="reports-stat-card">
              <p className="reports-stat-label">Sold</p>
              <p className="reports-stat-value">
                {formatMoney(statistics.balance)} {baseCurrency}
              </p>
            </div>
          </div>
        )}

        <div className="reports-ai-actions">
          <button
            type="button"
            className="reports-ai-btn"
            onClick={handleAiAdvice}
            disabled={aiLoading || dateOrderInvalid || statsLoading}
          >
            {aiLoading ? (
              <span className="reports-ai-spinner" aria-hidden />
            ) : (
              <HiSparkles className="text-lg" aria-hidden />
            )}
            Generează Analiză AI
          </button>
          {aiError && <p className="reports-error">{aiError}</p>}
        </div>

        {adviceText != null && (
          <div className="reports-ai-card">
            <div className="reports-ai-card-header">
              <span className="reports-ai-icon" aria-hidden>
                ✨
              </span>
              Analiză AI
            </div>
            <p className="reports-ai-body">{adviceText}</p>
          </div>
        )}
      </main>
    </div>
  );
}
