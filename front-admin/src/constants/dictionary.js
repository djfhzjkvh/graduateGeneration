export const CUSTOMER_STATUS = [
  { value: 'NEW',       label: '新客户',  badge: 'badge-gray'    },
  { value: 'FOLLOWING', label: '跟进中',  badge: 'badge-info'    },
  { value: 'VISITED',   label: '已到访',  badge: 'badge-warning' },
  { value: 'DEAL',      label: '已成交',  badge: 'badge-success' },
  { value: 'LOST',      label: '已流失',  badge: 'badge-danger'  },
]

export const INTENT_LEVEL = [
  { value: 'LOW',    label: '低意向', badge: 'badge-gray'    },
  { value: 'MEDIUM', label: '中意向', badge: 'badge-warning' },
  { value: 'HIGH',   label: '高意向', badge: 'badge-gold'    },
]

export const TASK_STATUS = [
  { value: 'PENDING', label: '待处理', badge: 'badge-warning' },
  { value: 'DONE',    label: '已完成', badge: 'badge-success' },
  { value: 'OVERDUE', label: '已逾期', badge: 'badge-danger'  },
  { value: 'DELAYED', label: '已延期', badge: 'badge-gray'    },
]

export const TASK_PRIORITY = [
  { value: 'LOW',    label: '低',   badge: 'badge-gray'   },
  { value: 'MEDIUM', label: '中',   badge: 'badge-info'   },
  { value: 'HIGH',   label: '高',   badge: 'badge-warning'},
  { value: 'URGENT', label: '紧急', badge: 'badge-danger' },
]

export const TASK_TYPE = [
  { value: 'FOLLOW', label: '跟进', badge: 'badge-success' },
  { value: 'VISIT',  label: '到访', badge: 'badge-purple'  },
  { value: 'CALL',   label: '电话', badge: 'badge-info'    },
  { value: 'SYSTEM', label: '系统', badge: 'badge-gray'    },
]

export const USER_STATUS = [
  { value: 1, label: '启用', badge: 'badge-success' },
  { value: 0, label: '禁用', badge: 'badge-danger'  },
]

/** 根据枚举值返回对应 badge class */
export function getBadgeClass(map, value) {
  return map.find(i => i.value === value)?.badge ?? 'badge-gray'
}

/** 根据枚举值返回中文标签 */
export function getLabel(map, value) {
  return map.find(i => i.value === value)?.label ?? value
}
