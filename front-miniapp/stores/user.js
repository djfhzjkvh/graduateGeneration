import { ref } from 'vue'

const userInfo = ref(null)

export function useUserStore() {
  function loadUser() {
    const saved = uni.getStorageSync('userInfo')
    if (saved) {
      userInfo.value = typeof saved === 'string' ? JSON.parse(saved) : saved
    }
  }

  function setUser(info) {
    userInfo.value = info
    uni.setStorageSync('userInfo', JSON.stringify(info))
  }

  function setToken(token) {
    uni.setStorageSync('token', token)
  }

  function logout() {
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }

  function getToken() {
    return uni.getStorageSync('token')
  }

  return { userInfo, loadUser, setUser, setToken, logout, getToken }
}
