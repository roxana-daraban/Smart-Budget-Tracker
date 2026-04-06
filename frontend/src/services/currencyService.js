import api from './api';

/**
 * Apelează POST /api/currency/convert.
 * Body: { fromCurrency, toCurrency, amount } (amount e number).
 * Returnează: { originalAmount, fromCurrency, convertedAmount, toCurrency, rate, rateDate }.
 */
export const currencyService = {
  async convert({ fromCurrency, toCurrency, amount }) {
    const { data } = await api.post('/currency/convert', {
      fromCurrency: fromCurrency.trim().toUpperCase(),
      toCurrency: toCurrency.trim().toUpperCase(),
      amount: Number(amount),
    });
    return data;
  },
};