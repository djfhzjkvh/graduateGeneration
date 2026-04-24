import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')
  const roleCode = computed(() => userInfo.value?.roleCode || '')

  function setAuth(loginVO) {
    token.value = loginVO.token
    userInfo.value = loginVO.userInfo
    localStorage.setItem('admin_token', loginVO.token)
    localStorage.setItem('admin_user', JSON.stringify(loginVO.userInfo))
  }

  function clearAuth() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
  }

  return { token, userInfo, isLoggedIn, username, roleCode, setAuth, clearAuth }
})
