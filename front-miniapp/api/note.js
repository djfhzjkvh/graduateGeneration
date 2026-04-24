import { http } from '../utils/request'

export const noteApi = {
  listByCustomer: (customerId) => http.get(`/api/app/customers/${customerId}/notes`),
  detail: (id) => http.get(`/api/app/notes/${id}`),
  create: (data) => http.post('/api/app/notes', data),
  update: (id, data) => http.put(`/api/app/notes/${id}`, data),
  remove: (id) => http.del(`/api/app/notes/${id}`),
  autoGenerate: (data) => http.post('/api/app/notes/auto-generate', data)
}
