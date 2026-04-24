import request from '@/utils/request'

export const deptApi = {
  /** GET /api/admin/depts/tree — 已实现 */
  tree: () => request.get('/admin/depts/tree'),
}
