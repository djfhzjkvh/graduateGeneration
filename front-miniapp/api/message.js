import { http } from '../utils/request'

export const messageApi = {
  page: (params) => http.get('/api/app/messages', params),
  read: (id) => http.put(`/api/app/messages/${id}/read`),
  readAll: (userId) => http.put(`/api/app/messages/read-all?userId=${userId}`)
}
