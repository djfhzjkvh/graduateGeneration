<template>
  <view class="login-page">
    <view class="login-header">
      <view class="logo-wrap">
        <text class="logo-icon">🏡</text>
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

        <button
          class="login-btn"
          :class="{ loading }"
          :disabled="loading"
          @tap="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </view>

      <text class="hint">演示账号：advisor_a / 123456</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '../../stores/user'

// 原型阶段直接用 mock 数据，不请求后端
const form = ref({ username: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')
const { setUser, setToken } = useUserStore()

// mock 账号数据
const MOCK_ACCOUNTS = {
  'advisor_a': { id: 1, name: '张顾问', username: 'advisor_a', role: 'ADVISOR', deptName: '华西销售一组', phone: '13800138001' },
  'advisor_b': { id: 2, name: '李顾问', username: 'advisor_b', role: 'ADVISOR', deptName: '华西销售一组', phone: '13800138002' },
  'manager_hx': { id: 3, name: '王经理', username: 'manager_hx', role: 'MANAGER', deptName: '华西销售部', phone: '13800138003' },
  'admin': { id: 4, name: '管理员', username: 'admin', role: 'ADMIN', deptName: '总部', phone: '13800138000' }
}

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

  // 模拟网络延迟
  await new Promise(r => setTimeout(r, 800))

  const user = MOCK_ACCOUNTS[form.value.username]
  if (user && form.value.password === '123456') {
    setToken('mock-token-' + user.username)
    setUser(user)
    uni.switchTab({ url: '/pages/home/index' })
  } else {
    errorMsg.value = '用户名或密码错误'
  }
  loading.value = false
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
      font-size: 64rpx;
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

    .input-group {
      padding: 0;
    }

    .input-wrap {
      display: flex;
      align-items: center;
      padding: 32rpx 32rpx;

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

      &::after {
        border: none;
      }

      &.loading {
        opacity: 0.7;
      }
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
