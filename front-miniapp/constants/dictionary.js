// 客户状态
export const CUSTOMER_STATUS = {
  NEW: { label: '新客户', color: '#1a56db', bg: '#e8effd' },
  FOLLOWING: { label: '跟进中', color: '#5b21b6', bg: '#ede9fe' },
  VISITED: { label: '已到访', color: '#c27803', bg: '#fef3c7' },
  DEAL: { label: '已成交', color: '#057a55', bg: '#d1fae5' },
  LOST: { label: '已流失', color: '#6b7280', bg: '#f3f4f6' }
}

// 意向等级
export const INTENT_LEVEL = {
  HIGH: { label: '高', color: '#c81e1e', bg: '#fee2e2' },
  MEDIUM: { label: '中', color: '#c27803', bg: '#fef3c7' },
  LOW: { label: '低', color: '#6b7280', bg: '#f3f4f6' }
}

// 客户来源
export const CUSTOMER_SOURCE = {
  WECHAT: '微信',
  PHONE: '电话',
  VISIT: '到访',
  REFERRAL: '转介绍',
  IMPORT: '导入'
}

// 跟进方式
export const FOLLOW_METHOD = {
  WECHAT: '微信',
  PHONE: '电话',
  SMS: '短信',
  VISIT: '到访',
  OTHER: '其他'
}

// 跟进结果
export const FOLLOW_RESULT = {
  CONNECTED: { label: '已接通', color: '#057a55' },
  NO_ANSWER: { label: '未接通', color: '#c81e1e' },
  WAITING: { label: '待考虑', color: '#c27803' },
  VISITED: { label: '已到访', color: '#1a56db' },
  DEAL: { label: '已成交', color: '#057a55' },
  LOST: { label: '已流失', color: '#6b7280' }
}

// 任务类型
export const TASK_TYPE = {
  FOLLOW: '跟进',
  VISIT: '到访',
  CALL: '电话',
  SYSTEM: '系统任务'
}

// 任务状态
export const TASK_STATUS = {
  PENDING: { label: '待处理', color: '#1a56db', bg: '#e8effd' },
  DONE: { label: '已完成', color: '#057a55', bg: '#d1fae5' },
  OVERDUE: { label: '已逾期', color: '#c81e1e', bg: '#fee2e2' },
  DELAYED: { label: '已延期', color: '#c27803', bg: '#fef3c7' }
}

// 任务优先级
export const TASK_PRIORITY = {
  URGENT: { label: '紧急', color: '#c81e1e', bg: '#fee2e2' },
  HIGH: { label: '高', color: '#c27803', bg: '#fef3c7' },
  MEDIUM: { label: '中', color: '#1a56db', bg: '#e8effd' },
  LOW: { label: '低', color: '#6b7280', bg: '#f3f4f6' }
}

// 性别
export const GENDER = {
  MALE: '男',
  FEMALE: '女',
  UNKNOWN: '未知'
}

// 购房目的
export const PURCHASE_PURPOSE = {
  SELF_USE: '自住',
  INVEST: '投资',
  IMPROVE: '改善',
  OTHER: '其他'
}
