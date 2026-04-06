/**
 * Valoare pentru afișare / totaluri: folosește amountInBaseCurrency dacă e nenul și nenul numeric;
 * altfel (null, 0) revine la amount (tranzacții vechi fără conversie populatesă).
 */
export function effectiveAmountInBase(tx: {
  amount?: number | null;
  amountInBaseCurrency?: number | null;
}): number {
  const raw = tx.amountInBaseCurrency;
  const n =
    raw != null && raw !== '' && !Number.isNaN(Number(raw))
      ? Number(raw)
      : NaN;
  if (!Number.isNaN(n) && n !== 0) {
    return Math.abs(n);
  }
  return Math.abs(Number(tx.amount ?? 0));
}
