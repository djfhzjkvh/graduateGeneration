import { http } from '../utils/request'

export const taskApi = {
  list: (params) => http.get('/api/app/tasks', params),
  detail: (id) => http.get(`/api/app/tasks/${id}`),
  create: (data) => http.post('/api/app/tasks', data),
  complete: (id) => http.put(`/api/app/tasks/${id}/complete`),
  delay: (id, data) => http.put(`/api/app/tasks/${id}/delay`, data),
  remindLogs: (id, params) => http.get(`/api/app/tasks/${id}/remind-logs`, params)
}
