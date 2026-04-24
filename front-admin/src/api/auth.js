import request from '@/utils/request'

export const authApi = {
  login: (data) => request.post('/admin/auth/login', data),
  profile: () => request.get('/admin/auth/profile'),
  logout: () => request.post('/admin/auth/logout'),
}
