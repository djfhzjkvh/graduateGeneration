import request from '@/utils/request'

export const projectApi = {
  /** GET /api/admin/projects */
  page: (params) => request.get('/admin/projects', { params }),
  detail: (id) => request.get(`/admin/projects/${id}`),
  create: (data) => request.post('/admin/projects', data),
  update: (id, data) => request.put(`/admin/projects/${id}`, data),
  remove: (id) => request.delete(`/admin/projects/${id}`),

  /** House type APIs under /api/admin/projects */
  houseTypes: (projectId) => request.get(`/admin/projects/${projectId}/house-types`),
  createHouseType: (data) => request.post('/admin/projects/house-types', data),
  updateHouseType: (id, data) => request.put(`/admin/projects/house-types/${id}`, data),
  removeHouseType: (id) => request.delete(`/admin/projects/house-types/${id}`),
}
