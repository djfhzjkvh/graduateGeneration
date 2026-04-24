import { http } from '../utils/request'

export const followApi = {
  create: (data) => http.post('/api/app/follows', data)
}
