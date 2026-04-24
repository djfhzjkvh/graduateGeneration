<template>
  <view class="login-page">
    <view class="login-header">
      <view class="logo-wrap">
        <text class="logo-icon">房</text>
      </view>
      <text class="app-name">置业跟进助手</text>
      <text class="app-desc">客户购房意向跟进提醒系统</text>
    </view>

    <view class="login-form">
      <view class="form-card">
        <view class="input-group">
          <view class="input-wrap">
            <text class="input-prefix">账号</text>
            <input
              class="input"
              v-model="form.username"
              placeholder="请输入用户名"
              placeholder-style="color: #9ca3af"
              :disabled="loading"
            />
          </view>
          <view class="input-divider" />
          <view class="input-wrap">
            <text class="input-prefix">密码</text>
            <input
              class="input"
              v-model="form.password"
              password
              placeholder="请输入密码"
              placeholder-style="color: #9ca3af"
              :disabled="loading"
              @confirm="handleLogin"
            />
          </view>
        </view>

        <text v-if="errorMsg" class="error-msg">{{ errorMsg }}</text>

        <button class="login-btn" :class="{ loading }" :disabled="loading" @tap="handleLogin">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </view>

      <text class="hint">演示账号：admin / 123456，advisor_a / 123456</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { authApi } from '../../api/auth'
import { useUserStore } from '../../stores/user'
import { mapUser } from '../../utils/adapters'

const form = ref({ username: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')
const { setUser, setToken } = useUserStore()

async function handleLogin() {
  if (!form.value.username.trim()) {
    errorMsg.value = '请输入用户名'
    return
  }
  if (!form.value.password.trim()) {
    errorMsg.value = '请输入密码'
    return
  }

  errorMsg.value = ''
  loading.value = true
  try {
    const data = await authApi.login(form.value)
    setToken(data?.token || '')
    setUser(mapUser(data?.userInfo || {}))
    uni.switchTab({ url: '/pages/home/index' })
  } catch (error) {
    errorMsg.value = error?.message || '登录失败，请检查账号密码'
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  padding: 0 48rpx;
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 160rpx;
  padding-bottom: 80rpx;

  .logo-wrap {
    width: 120rpx;
    height: 120rpx;
    background: #e8effd;
    border-radius: 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 32rpx;

    .logo-icon {
      font-size: 44rpx;
      color: #1a56db;
      font-weight: 800;
    }
  }

  .app-name {
    font-size: 44rpx;
    font-weight: 700;
    color: #111827;
    margin-bottom: 12rpx;
  }

  .app-desc {
    font-size: 24rpx;
    color: #9ca3af;
  }
}

.login-form {
  flex: 1;

  .form-card {
    background: #ffffff;
    border: 1rpx solid #e5e7eb;
    border-radius: 16rpx;
    padding: 8rpx 0;
    margin-bottom: 24rpx;

    .input-wrap {
      display: flex;
      align-items: center;
      padding: 32rpx;

      .input-prefix {
        font-size: 28rpx;
        color: #374151;
        font-weight: 600;
        width: 80rpx;
        flex-shrink: 0;
      }

      .input {
        flex: 1;
        font-size: 28rpx;
        color: #111827;
        margin-left: 16rpx;
      }
    }

    .input-divider {
      height: 1rpx;
      background: #f3f4f6;
      margin: 0 32rpx;
    }

    .error-msg {
      display: block;
      font-size: 24rpx;
      color: #c81e1e;
      padding: 8rpx 32rpx 16rpx;
    }

    .login-btn {
      margin: 16rpx 32rpx 32rpx;
      background: #1a56db;
      color: #ffffff;
      font-size: 32rpx;
      font-weight: 600;
      border-radius: 10rpx;
      height: 88rpx;
      border: none;

      &::after { border: none; }
      &.loading { opacity: 0.7; }
    }
  }

  .hint {
    display: block;
    text-align: center;
    font-size: 22rpx;
    color: #9ca3af;
  }
}
</style>
