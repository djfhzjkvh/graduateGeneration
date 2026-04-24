import { http } from '../utils/request'

export const customerApi = {
  list: (params) => http.get('/api/app/customers', params),
  detail: (id) => http.get(`/api/app/customers/${id}`),
  create: (data) => http.post('/api/app/customers', data),
  update: (id, data) => http.put(`/api/app/customers/${id}`, data),
  remove: (id) => http.del(`/api/app/customers/${id}`),
  follows: (customerId, params) => http.get(`/api/app/customers/${customerId}/follows`, params)
}
