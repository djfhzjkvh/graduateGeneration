import request from '@/utils/request'

export const customerApi = {
  /** GET /api/admin/customers — 已实现
   *  params: keyword, status, advisorId, managerId, deptId,
   *          minHeatScore, maxHeatScore, pageNum, pageSize
  */
  page: (params) => request.get('/admin/customers', { params }),
  profile: (id) => request.get(`/admin/customers/${id}/profile`),

  /** GET /api/app/customers/:id — 已实现（详情用 app 端接口，admin 端暂无） */
  detail: (id) => request.get(`/app/customers/${id}`),
  follows: (customerId) => request.get(`/app/customers/${customerId}/follows`),
  createFollow: (data) => request.post('/app/follows', data),
  updateTags: (customerId, tagIds) => request.put(`/app/customers/${customerId}/tags`, { tagIds }),
  notes: (customerId) => request.get(`/app/customers/${customerId}/notes`),
  createNote: (data) => request.post('/app/notes', data),
  updateNote: (id, data) => request.put(`/app/notes/${id}`, data),
  deleteNote: (id) => request.delete(`/app/notes/${id}`),
  autoGenerateNote: (data) => request.post('/app/notes/auto-generate', data),
  competitorHistory: (customerId) => request.get(`/app/customers/${customerId}/competitor-history`),
  compareCompetitor: (data) => request.post('/app/competitors/compare', data),
  competitorAiResponse: (data) => request.post('/app/competitors/ai-response', data),
  generateScript: (data) => request.post('/app/ai/scripts/generate', data),
  extractLead: (data) => request.post('/app/ai/leads/extract', data),
  confirmLead: (data) => request.post('/app/ai/leads/confirm', data),

  /** 管理端客户分配与状态流转 */
  assignLogs: (params) => request.get('/admin/customers/assign-logs', { params }),
  assignLogsByCustomer: (customerId, params) => request.get(`/admin/customers/${customerId}/assign-logs`, { params }),
  assign: (id, data) => request.put(`/admin/customers/${id}/assign`, data),
  updateStatus: (id, data) => request.put(`/admin/customers/${id}/status`, data),
  previewExcelImport: (data) => request.post('/admin/customers/import/excel/preview', data),
  confirmExcelImport: (data) => request.post('/admin/customers/import/excel/confirm', data),
  downloadExcelTemplate: () => request.get('/admin/customers/import/excel/template', { responseType: 'blob' }),
  leadExtractRecords: (params) => request.get('/admin/ai/leads/extract-records', { params }),

  // TODO: 以下接口后端待补充（admin 端）
  // update: (id, data) => request.put(`/admin/customers/${id}`, data),
  // export: (params) => request.get('/admin/customers/export', { params, responseType: 'blob' }),

  /** GET /api/admin/customer-tags — 已实现 */
  tagList: () => request.get('/admin/customer-tags'),
  createTag: (data) => request.post('/admin/customer-tags', data),
  updateTag: (id, data) => request.put(`/admin/customer-tags/${id}`, data),
  deleteTag: (id) => request.delete(`/admin/customer-tags/${id}`),
}
