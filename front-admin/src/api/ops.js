import request from '@/utils/request'

export const opsApi = {
  /** System config APIs */
  configs: (params) => request.get('/admin/configs', { params }),
  updateConfig: (key, data) => request.put(`/admin/configs/${key}`, data),

  /** AI model config APIs */
  aiConfig: () => request.get('/admin/ai/config'),
  updateAiConfig: (data) => request.put('/admin/ai/config', data),

  /** Admin log APIs */
  aiLogs: (params) => request.get('/admin/logs/ai', { params }),
  operationLogs: (params) => request.get('/admin/logs/operation', { params }),

  /** System job APIs */
  refreshOverdueTasks: () => request.post('/admin/jobs/refresh-overdue-tasks'),
  generateFollowTasks: () => request.post('/admin/jobs/generate-follow-tasks'),
  remindDueSoonTasks: () => request.post('/admin/jobs/remind-due-soon-tasks'),
  generateDailyReport: () => request.post('/admin/jobs/generate-daily-report'),
}
