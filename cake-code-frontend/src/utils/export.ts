/**
 * 导出 CSV 文件
 * @param filename 文件名（不含扩展名）
 * @param headers 表头
 * @param rows 数据行
 */
export function exportCsv(filename: string, headers: string[], rows: (string | number)[][]): void {
  const content = [headers, ...rows].map((row) => row.join(',')).join('\n')
  // 加 BOM 让 Excel 正确识别 UTF-8 中文
  const blob = new Blob(['\ufeff' + content], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${filename}.csv`
  link.click()
  URL.revokeObjectURL(url)
}
