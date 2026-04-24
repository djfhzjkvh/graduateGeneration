import { http } from '../utils/request'

export const dashboardApi = {
  workbench: (params) => http.get('/api/app/dashboard/workbench', params),
  advisor: (params) => http.get('/api/app/dashboard/advisor', params),
  manager: (params) => http.get('/api/app/dashboard/manager', params)
}
