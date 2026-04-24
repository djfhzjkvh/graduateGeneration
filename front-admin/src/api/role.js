import request from '@/utils/request'

export const roleApi = {
  /** GET /api/admin/roles — 已实现 */
  list: () => request.get('/admin/roles'),
}
