import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatTrend(rate: number | null | undefined): { text: string; isUp: boolean } {
  if (rate === null || rate === undefined) {
    return { text: '-', isUp: true };
  }

  const isUp = rate >= 0;
  const text = `${isUp ? '+' : ''}${rate.toFixed(1)}%`;
  return { text, isUp };
}
