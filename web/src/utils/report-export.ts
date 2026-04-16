type ExportColumn<T> = {
  key: keyof T
  label: string
}

type PrintMetaItem = {
  label: string
  value: string
}

type PrintOptions = {
  subtitle?: string
  meta?: PrintMetaItem[]
  preparedBy?: string
  reviewedBy?: string
  approvedBy?: string
}

const escapeCsvCell = (value: unknown) => {
  const text = value == null ? '' : String(value)
  if (/[",\r\n]/.test(text)) {
    return `"${text.replace(/"/g, '""')}"`
  }
  return text
}

export const exportRowsToCsv = <T extends Record<string, unknown>>(
  rows: T[],
  columns: ExportColumn<T>[],
  filename: string,
) => {
  const header = columns.map((item) => item.label).join(',')
  const body = rows.map((row) => columns.map((col) => escapeCsvCell(row[col.key])).join(',')).join('\r\n')
  const csv = `\uFEFF${header}\r\n${body}`
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${filename}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

export const printRowsAsTable = <T extends Record<string, unknown>>(
  title: string,
  rows: T[],
  columns: ExportColumn<T>[],
  options: PrintOptions = {},
) => {
  const popup = window.open('', '_blank', 'width=1200,height=800')
  if (!popup) return false

  const metaRows = (options.meta || [])
    .map((item) => `<span><b>${item.label}：</b>${item.value || '-'}</span>`)
    .join('')
  const printTime = new Date().toLocaleString('zh-CN', { hour12: false })
  const head = columns.map((item) => `<th>${item.label}</th>`).join('')
  const body = rows
    .map((row) => `<tr>${columns.map((col) => `<td>${row[col.key] ?? ''}</td>`).join('')}</tr>`)
    .join('')

  popup.document.write(`
<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <title>${title}</title>
  <style>
    @page { size: A4 landscape; margin: 12mm; }
    body { font-family: "Microsoft YaHei", sans-serif; padding: 0; color: #111; }
    .sheet { width: 100%; }
    h1 { margin: 0; font-size: 22px; text-align: center; }
    .subtitle { margin: 6px 0 14px; text-align: center; font-size: 13px; color: #4d5968; }
    .meta { margin-bottom: 10px; display: flex; gap: 14px; flex-wrap: wrap; font-size: 12px; color: #2f3742; }
    .meta span { background: #f4f7fb; border: 1px solid #dbe3ee; padding: 3px 8px; border-radius: 4px; }
    table { width: 100%; border-collapse: collapse; }
    th, td { border: 1px solid #cfd6df; padding: 8px 10px; font-size: 13px; text-align: left; }
    th { background: #f5f7fb; }
    .footer { margin-top: 14px; display: flex; justify-content: space-between; align-items: center; color: #2f3742; font-size: 12px; }
    .signatures { display: flex; gap: 20px; }
  </style>
</head>
<body>
  <div class="sheet">
    <h1>${title}</h1>
    ${options.subtitle ? `<div class="subtitle">${options.subtitle}</div>` : ''}
    ${metaRows ? `<div class="meta">${metaRows}</div>` : ''}
    <table>
      <thead><tr>${head}</tr></thead>
      <tbody>${body}</tbody>
    </table>
    <div class="footer">
      <div class="signatures">
        <span>制表：${options.preparedBy || '-'}</span>
        <span>复核：${options.reviewedBy || '-'}</span>
        <span>审批：${options.approvedBy || '-'}</span>
      </div>
      <span>打印时间：${printTime}</span>
    </div>
  </div>
</body>
</html>`)
  popup.document.close()
  popup.focus()
  popup.print()
  return true
}
