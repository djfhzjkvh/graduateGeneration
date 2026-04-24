import request from '@/utils/request'

export const customerApi = {
  /** GET /api/admin/customers — 已实现
   *  params: keyword, status, advisorId, managerId, deptId,
   *          minHeatScore, maxHeatScore, pageNum, pageSize
   */
  page: (params) => request.get('/admin/customers', { params }),

  /** GET /api/app/customers/:id — 已实现（详情用 app 端接口，admin 端暂无） */
  detail: (id) => request.get(`/app/customers/${id}`),

  // TODO: 以下接口后端待补充（admin 端）
  // update: (id, data) => request.put(`/admin/customers/${id}`, data),
  // assign: (id, advisorId) => request.post(`/admin/customers/${id}/assign`, { advisorId }),
  // export: (params) => request.get('/admin/customers/export', { params, responseType: 'blob' }),

  /** GET /api/admin/customer-tags — 已实现 */
  tagList: () => request.get('/admin/customer-tags'),
  createTag: (data) => request.post('/admin/customer-tags', data),
  updateTag: (id, data) => request.put(`/admin/customer-tags/${id}`, data),
  deleteTag: (id) => request.delete(`/admin/customer-tags/${id}`),
}
