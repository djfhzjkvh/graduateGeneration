import request from '@/utils/request'

export const taskApi = {
  /** GET /api/admin/tasks — 已实现
   *  params: customerId, ownerId, status, date (yyyy-MM-dd), pageNum, pageSize
   */
  page: (params) => request.get('/admin/tasks', { params }),
  detail: (id) => request.get(`/admin/tasks/${id}`),
  stat: () => request.get('/admin/tasks/stat'),
  remindLogs: (params) => request.get('/admin/tasks/remind-logs', { params }),
  transferLogs: (params) => request.get('/admin/tasks/transfer-logs', { params }),
  taskTransferLogs: (taskId, params) => request.get(`/admin/tasks/${taskId}/transfer-logs`, { params }),
  transfer: (id, data) => request.put(`/admin/tasks/${id}/transfer`, data),

  // TODO: 以下接口后端待补充
  // complete: (id) => request.put(`/admin/tasks/${id}/complete`),
}
