import { parseLocalYmd } from './localDate';

/**
 * Sortare descrescătoare după dată (locală), apoi după id desc (aceeași zi).
 */
export function compareTransactionsNewestFirst(
  a: { id: number | string; date: string },
  b: { id: number | string; date: string }
): number {
  const ta = parseLocalYmd(a.date).getTime();
  const tb = parseLocalYmd(b.date).getTime();
  const aNaN = Number.isNaN(ta);
  const bNaN = Number.isNaN(tb);
  const idDiff = Number(b.id) - Number(a.id);
  if (aNaN && bNaN) return idDiff;
  if (aNaN) return 1;
  if (bNaN) return -1;
  if (tb !== ta) return tb - ta;
  return idDiff;
}
