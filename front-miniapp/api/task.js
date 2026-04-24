import { http } from '../utils/request'

export const taskApi = {
  list: (params) => http.get('/api/app/tasks', params),
  create: (data) => http.post('/api/app/tasks', data),
  complete: (id) => http.put(`/api/app/tasks/${id}/complete`)
}
