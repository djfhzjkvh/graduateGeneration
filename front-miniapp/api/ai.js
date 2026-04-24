import { http } from '../utils/request'

export const aiApi = {
  generateScript: (data) => http.post('/api/app/ai/scripts/generate', data),
  chat: (data) => http.post('/api/app/ai/chat', data)
}
