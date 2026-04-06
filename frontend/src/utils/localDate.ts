/**
 * Formatare YYYY-MM-DD în timezone-ul local (fără conversie UTC ca la toISOString).
 */
export function formatLocalYmd(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

/**
 * Parsește YYYY-MM-DD ca zi calendaristică locală.
 */
export function parseLocalYmd(s: string): Date {
  const parts = s.split('-').map(Number);
  const y = parts[0];
  const mo = parts[1];
  const day = parts[2];
  if (!y || !mo || !day) return new Date(NaN);
  return new Date(y, mo - 1, day);
}
