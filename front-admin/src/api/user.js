import request from '@/utils/request'

export const userApi = {
  /** 已实现: GET /api/admin/users
   *  params: keyword, roleId, deptId, status (1/0), pageNum, pageSize
   */
  page: (params) => request.get('/admin/users', { params }),
  detail: (id) => request.get(`/admin/users/${id}`),
  create: (data) => request.post('/admin/users', data),
  update: (id, data) => request.put(`/admin/users/${id}`, data),
  remove: (id) => request.delete(`/admin/users/${id}`),

  // TODO: 重置密码接口后端待补充
  // resetPassword: (id) => request.put(`/admin/users/${id}/password/reset`),
}
