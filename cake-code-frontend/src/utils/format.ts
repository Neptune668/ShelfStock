/**
 * 格式化数字，保留 2 位小数，空值显示占位符
 */
export function formatNumber(value: number | null | undefined): string {
  if (value == null) return '-'
  return value.toFixed(2)
}
