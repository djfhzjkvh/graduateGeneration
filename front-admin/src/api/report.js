import request from '@/utils/request'

export const reportApi = {
  /** GET /api/admin/reports/daily */
  daily: (params) => request.get('/admin/reports/daily', { params }),
  /** GET /api/admin/reports/conversion */
  conversion: (params) => request.get('/admin/reports/conversion', { params }),
  /** GET /api/admin/reports/source */
  source: (params) => request.get('/admin/reports/source', { params }),
}
