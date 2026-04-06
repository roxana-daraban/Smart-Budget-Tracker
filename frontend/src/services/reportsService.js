import api from './api';

/**
 * Rapoarte & sfaturi AI (Gemini prin backend).
 * POST /api/reports/ai-advice — body { from, to } ca YYYY-MM-DD.
 */
export const reportsService = {
  async getAiAdvice(from, to) {
    const { data } = await api.post('/reports/ai-advice', { from, to });
    return data;
  },
};
