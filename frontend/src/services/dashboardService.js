import api from './api';

/**
 * Serviciu pentru datele Dashboard-ului.
 * Folosește instanța "api" care are deja token și X-User-Id în header-uri.
 */

export const dashboardService = {
  /**
   * Statistici pentru perioada dată (sau luna curentă dacă nu trimiți from/to).
   * Backend: GET /api/dashboard/statistics?from=...&to=...
   */
  async getStatistics(from, to) {
    const params = {};
    if (from) params.from = from;
    if (to) params.to = to;
    const { data } = await api.get('/dashboard/statistics', { params });
    return data;
  },

  /**
   * Lista de tranzacții ale utilizatorului, ordonată descrescător după dată.
   * Backend: GET /api/transactions
   */
  async getTransactions() {
    const { data } = await api.get('/transactions');
    return data;
  },
};