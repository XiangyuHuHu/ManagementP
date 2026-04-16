const escapeCsvCell = (value) => {
  const text = value == null ? '' : String(value)
  if (/[",\r\n]/.test(text)) {
    return `"${text.replace(/"/g, '""')}"`
  }
  return text
}

const toCsv = (rows) => rows.map(row => row.map(escapeCsvCell).join(',')).join('\r\n')

const downloadBlob = (content, filename, type) => {
  const blob = new Blob([content], { type })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

export const exportToExcel = (data, filename) => {
  const headers = Object.keys(data?.[0] || {})
  const rows = [headers, ...data.map(item => headers.map(key => item[key]))]
  const csv = '\uFEFF' + toCsv(rows)
  downloadBlob(csv, `${filename}.csv`, 'text/csv;charset=utf-8;')
}

export const exportTableToExcel = (tableRef, filename) => {
  if (!tableRef) return

  const el = tableRef.$el || tableRef
  const headers = el.querySelectorAll('.el-table__header-wrapper th')
  const rows = el.querySelectorAll('.el-table__body-wrapper tr')

  if (headers.length === 0 || rows.length === 0) return

  const headerData = Array.from(headers).map(th => th.textContent.trim()).filter(Boolean)
  const bodyData = Array.from(rows).map(row => {
    return Array.from(row.querySelectorAll('td')).slice(0, headerData.length).map(td => {
      const cell = td.querySelector('.cell')
      return cell ? cell.textContent.trim() : td.textContent.trim()
    })
  })

  const csv = '\uFEFF' + toCsv([headerData, ...bodyData])
  downloadBlob(csv, `${filename}.csv`, 'text/csv;charset=utf-8;')
}

export const exportJsonToExcel = (jsonData, columns, filename) => {
  const data = jsonData.map(item => {
    const row = {}
    columns.forEach(col => {
      row[col.label] = col.prop ? getNestedValue(item, col.prop) : ''
    })
    return row
  })
  exportToExcel(data, filename)
}

const getNestedValue = (obj, path) => {
  return path.split('.').reduce((acc, part) => acc && acc[part], obj)
}

export const formatExcelDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
