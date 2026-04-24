/**
 * 格式化日期时间
 * @param {string|number} val - 时间戳或日期字符串
 * @param {string} fmt - 格式，默认 'MM-DD HH:mm'
 */
export function formatDate(val, fmt = 'MM-DD HH:mm') {
  if (!val) return '--'
  const d = new Date(val)
  const map = {
    'YYYY': d.getFullYear(),
    'MM': String(d.getMonth() + 1).padStart(2, '0'),
    'DD': String(d.getDate()).padStart(2, '0'),
    'HH': String(d.getHours()).padStart(2, '0'),
    'mm': String(d.getMinutes()).padStart(2, '0')
  }
  return fmt.replace(/YYYY|MM|DD|HH|mm/g, k => map[k])
}

/**
 * 相对时间描述
 */
export function relativeTime(val) {
  if (!val) return '--'
  const now = Date.now()
  const t = new Date(val).getTime()
  const diff = now - t
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  return formatDate(val, 'MM-DD')
}

/**
 * 手机号脱敏
 */
export function maskPhone(phone) {
  if (!phone) return '--'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 预算范围格式化（万元）
 */
export function formatBudget(min, max) {
  if (!min && !max) return '--'
  if (!max) return `${min}万以上`
  if (!min) return `${max}万以下`
  return `${min}-${max}万`
}

/**
 * 判断任务是否已逾期
 */
export function isOverdue(dateStr) {
  if (!dateStr) return false
  return new Date(dateStr).getTime() < Date.now()
}
