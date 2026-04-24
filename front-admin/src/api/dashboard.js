import request from '@/utils/request'

export const dashboardApi = {
  /** GET /api/admin/dashboard/overview — 已实现 */
  overview: () => request.get('/admin/dashboard/overview'),

  // TODO: 以下接口后端待补充
  // customerTrend: () => request.get('/admin/dashboard/customer-trend'),
  // taskSummary: () => request.get('/admin/dashboard/task-summary'),
}
