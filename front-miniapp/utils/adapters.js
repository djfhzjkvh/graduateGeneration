function combineDateTime(date, time) {
  if (!date) return null
  return time ? `${date} ${time}` : `${date} 00:00`
}

export function mapUser(user = {}) {
  return {
    ...user,
    name: user.nickname || user.username || '',
    role: user.roleCode || user.role || '',
    phone: user.mobile || user.phone || ''
  }
}

export function mapCustomer(customer = {}) {
  return {
    ...customer,
    name: customer.name || customer.customerName || '',
    phone: customer.phone || customer.mobile || '',
    focusArea: customer.focusArea || customer.region || '',
    lastFollowTime: customer.lastFollowTime || customer.latestFollowTime || null
  }
}

export function mapTask(task = {}) {
  const planTime = task.planTime || combineDateTime(task.taskDate, task.taskTime)
  return {
    ...task,
    customerName: task.customerName || '',
    customerId: task.customerId,
    taskType: task.taskType || 'FOLLOW',
    status: task.status || 'PENDING',
    priority: task.priority || 'MEDIUM',
    planTime,
    doneTime: task.doneTime || task.completeTime || null,
    createdAt: task.createdAt || planTime
  }
}

export function mapFollow(follow = {}) {
  return {
    ...follow,
    method: follow.method || follow.followType,
    result: follow.result || follow.followResult
  }
}
