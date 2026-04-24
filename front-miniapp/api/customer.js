import { http } from '../utils/request'
import { downloadAuthorizedFile } from './file'

export const customerApi = {
  list: (params) => http.get('/api/app/customers', params),
  detail: (id) => http.get(`/api/app/customers/${id}`),
  create: (data) => http.post('/api/app/customers', data),
  update: (id, data) => http.put(`/api/app/customers/${id}`, data),
  updateStatus: (id, data) => http.put(`/api/app/customers/${id}/status`, data),
  remove: (id) => http.del(`/api/app/customers/${id}`),
  follows: (customerId, params) => http.get(`/api/app/customers/${customerId}/follows`, params),
  tags: () => http.get('/api/app/customer-tags'),
  bindTags: (customerId, tagIds) => http.put(`/api/app/customers/${customerId}/tags`, { tagIds }),
  downloadExcelTemplate: () => downloadAuthorizedFile('/api/admin/customers/import/excel/template', '客户导入模板.xlsx')
}
