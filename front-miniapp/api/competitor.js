import { http } from '../utils/request'

export const competitorApi = {
  compare: (data) => http.post('/api/app/competitors/compare', data),
  aiResponse: (data) => http.post('/api/app/competitors/ai-response', data),
  history: (customerId) => http.get(`/api/app/customers/${customerId}/competitor-history`)
}
