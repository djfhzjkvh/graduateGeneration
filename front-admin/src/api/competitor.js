import request from '@/utils/request'

export const competitorApi = {
  /** GET /api/admin/competitors */
  page: (params) => request.get('/admin/competitors', { params }),
  detail: (id) => request.get(`/admin/competitors/${id}`),
  create: (data) => request.post('/admin/competitors', data),
  update: (id, data) => request.put(`/admin/competitors/${id}`, data),
  remove: (id) => request.delete(`/admin/competitors/${id}`),
}
