import request from '@/utils/request'

export const fileApi = {
  /** POST /api/admin/files/upload */
  upload: ({ file, bizType, bizId, createdBy }) => {
    const formData = new FormData()
    formData.append('file', file)
    if (bizType) formData.append('bizType', bizType)
    if (bizId !== null && bizId !== undefined && bizId !== '') formData.append('bizId', bizId)
    if (createdBy !== null && createdBy !== undefined && createdBy !== '') formData.append('createdBy', createdBy)
    return request.post('/admin/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 60000,
    })
  },

  /** GET /api/files/{id} */
  detail: (id) => request.get(`/files/${id}`),
}
