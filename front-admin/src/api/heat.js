import request from '@/utils/request'

export const heatApi = {
  /** GET /api/admin/heat/high-intent — 已实现
   *  params: managerId (optional)
   */
  highIntent: (params) => request.get('/admin/heat/high-intent', { params }),

  /** POST /api/admin/heat/batch-calculate — 已实现 */
  batchCalculate: () => request.post('/admin/heat/batch-calculate'),
}
