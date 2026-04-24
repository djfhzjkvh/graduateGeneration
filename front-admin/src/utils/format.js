import dayjs from 'dayjs'

export function formatDate(value, pattern = 'YYYY-MM-DD HH:mm') {
  return value ? dayjs(value).format(pattern) : '-'
}

export function formatPrice(value, unit = '元/㎡') {
  if (value === null || value === undefined || value === '') return '-'
  return `${Number(value).toLocaleString()} ${unit}`
}

export function formatAmount(value, unit = '万') {
  if (value === null || value === undefined || value === '') return '-'
  return `${Number(value).toLocaleString()} ${unit}`
}

export function formatPercent(value, precision = 1) {
  if (value === null || value === undefined || value === '') return '0%'
  return `${(Number(value) * 100).toFixed(precision)}%`
}

export function formatFileSize(size) {
  const value = Number(size || 0)
  if (!value) return '-'
  if (value < 1024) return `${value} B`
  if (value < 1024 * 1024) return `${(value / 1024).toFixed(1)} KB`
  return `${(value / 1024 / 1024).toFixed(2)} MB`
}
