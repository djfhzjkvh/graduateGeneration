import request from '@/utils/request'

export const taskApi = {
  /** GET /api/admin/tasks — 已实现
   *  params: customerId, ownerId, status, date (yyyy-MM-dd), pageNum, pageSize
   */
  page: (params) => request.get('/admin/tasks', { params }),

  // TODO: 以下接口后端待补充
  // complete: (id) => request.put(`/admin/tasks/${id}/complete`),
  // transfer: (id, ownerId) => request.put(`/admin/tasks/${id}/transfer`, { ownerId }),
}
