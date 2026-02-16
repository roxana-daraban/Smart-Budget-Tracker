import api from './api';

/**
 * Serviciu pentru tranzacții și categorii.
 * api are deja token și X-User-Id în header-uri.
 */

export const transactionService = {
  /** Lista de tranzacții (ordonată după dată descrescător). */
  async getTransactions() {
    const { data } = await api.get('/transactions');
    return data;
  },

  /** Categorii opțional filtrate după tip: INCOME sau EXPENSE. */
  async getCategories(type) {
    const params = type ? { type } : {};
    const { data } = await api.get('/categories', { params });
    return data;
  },

  /** Creare tranzacție. Body: { description, amount, currency, date, categoryId } */
  async createTransaction(body) {
    const { data } = await api.post('/transactions', body);
    return data;
  },

  /** Actualizare tranzacție. */
  async updateTransaction(id, body) {
    const { data } = await api.put(`/transactions/${id}`, body);
    return data;
  },

  /** Ștergere tranzacție. */
  async deleteTransaction(id) {
    await api.delete(`/transactions/${id}`);
  },
};