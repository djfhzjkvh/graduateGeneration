import { fileApi } from '@/api/file'

export function isImageFile(file) {
  return file?.fileType?.startsWith('image/')
}

export async function uploadBusinessFile({ file, bizType, bizId, createdBy }) {
  return fileApi.upload({ file, bizType, bizId, createdBy })
}
