import { http } from '../utils/request'

export const authApi = {
  login: (data) => http.post('/api/app/auth/login', data),
  profile: () => http.get('/api/app/auth/profile'),
  logout: () => http.post('/api/app/auth/logout')
}
