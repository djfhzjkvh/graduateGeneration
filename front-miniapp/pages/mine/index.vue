<template>
  <view class="page-container">
    <!-- 用户信息卡 -->
    <view class="user-card">
      <view class="avatar">
        <text class="avatar-text">{{ avatarChar }}</text>
      </view>
      <text class="user-name">{{ displayName }}</text>
      <text class="user-username">@{{ displayUsername }}</text>
      <view class="user-tags">
        <view class="role-tag">{{ displayRole }}</view>
        <view class="dept-tag">{{ displayDept }}</view>
      </view>
      <text class="user-phone">{{ displayPhone }}</text>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-list">
        <view class="menu-item" v-for="item in menuItems" :key="item.label" @tap="item.action">
          <view class="menu-left">
            <text class="menu-icon">{{ item.icon }}</text>
            <text class="menu-label">{{ item.label }}</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-wrap">
      <button class="logout-btn" @tap="handleLogout">退出登录</button>
    </view>

    <text class="version">v1.0.0 · 置业跟进助手</text>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { authApi } from '../../api/auth'
import { useUserStore } from '../../stores/user'
import { mapUser } from '../../utils/adapters'

const { userInfo, loadUser, setUser, logout } = useUserStore()
const refreshing = ref(false)

const ROLE_LABEL = { ADMIN: '管理员', MANAGER: '销售经理', ADVISOR: '销售顾问' }

const avatarChar = computed(() => (userInfo.value && userInfo.value.name && userInfo.value.name.charAt(0)) || '?')
const displayName = computed(() => (userInfo.value && userInfo.value.name) || '')
const displayUsername = computed(() => (userInfo.value && userInfo.value.username) || '')
const displayDept = computed(() => (userInfo.value && userInfo.value.deptName) || '')
const displayPhone = computed(() => (userInfo.value && userInfo.value.phone) || '')
const displayRole = computed(() => {
  const role = userInfo.value && userInfo.value.role
  return ROLE_LABEL[role] || role || ''
})

const menuItems = [
  { icon: '👤', label: '个人资料', action: () => uni.navigateTo({ url: '/pages/profile/index' }) },
  { icon: '👥', label: '我的客户', action: () => uni.switchTab({ url: '/pages/customer/list' }) },
  { icon: '📋', label: '我的任务', action: () => uni.switchTab({ url: '/pages/task/list' }) },
  { icon: 'AI', label: 'AI线索录入', action: () => uni.navigateTo({ url: '/pages/ai/lead' }) },
  { icon: '🔔', label: '消息通知', action: () => uni.navigateTo({ url: '/pages/message/list' }) }
]

async function refreshProfile() {
  if (!uni.getStorageSync('token') || refreshing.value) return

  refreshing.value = true
  try {
    const data = await authApi.profile()
    const profile = data?.userInfo || data
    setUser(mapUser(profile || {}))
    console.info('[mine] profile refreshed')
  } catch (error) {
    console.warn('[mine] profile refresh failed', error)
  } finally {
    refreshing.value = false
  }
}

function handleLogout() {
  uni.showModal({
    title: '退出登录',
    content: '确认退出当前账号？',
    confirmColor: '#c81e1e',
    confirmText: '退出',
    async success(res) {
      if (res.confirm) {
        try {
          await authApi.logout()
          console.info('[mine] backend logout success')
        } catch (error) {
          // 退出登录以清理本地登录态为准，接口失败时记录日志方便排查。
          console.warn('[mine] backend logout failed', error)
        }
        logout()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}

onMounted(() => {
  loadUser()
})

onShow(() => {
  loadUser()
  refreshProfile()
})
</script>

<style lang="scss" scoped>
.user-card {
  background: #ffffff;
  padding: 48rpx 32rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-bottom: 1rpx solid #e5e7eb;

  .avatar {
    width: 128rpx;
    height: 128rpx;
    background: #e8effd;
    border-radius: 9999rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20rpx;

    .avatar-text {
      font-size: 56rpx;
      font-weight: 700;
      color: #1a56db;
    }
  }

  .user-name {
    font-size: 36rpx;
    font-weight: 700;
    color: #111827;
    margin-bottom: 8rpx;
  }

  .user-username {
    font-size: 24rpx;
    color: #9ca3af;
    margin-bottom: 16rpx;
  }

  .user-tags {
    display: flex;
    gap: 12rpx;
    margin-bottom: 16rpx;

    .role-tag, .dept-tag {
      font-size: 20rpx;
      padding: 4rpx 16rpx;
      border-radius: 9999rpx;
      font-weight: 600;
    }

    .role-tag {
      background: #e8effd;
      color: #1a56db;
    }

    .dept-tag {
      background: #f3f4f6;
      color: #6b7280;
    }
  }

  .user-phone {
    font-size: 26rpx;
    color: #6b7280;
  }
}

.menu-section {
  margin: 24rpx 24rpx 0;

  .menu-list {
    background: #ffffff;
    border: 1rpx solid #e5e7eb;
    border-radius: 12rpx;
    overflow: hidden;
  }

  .menu-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 32rpx 28rpx;
    border-bottom: 1rpx solid #f3f4f6;

    &:last-child { border-bottom: none; }

    .menu-left {
      display: flex;
      align-items: center;
      gap: 20rpx;

      .menu-icon { font-size: 36rpx; }

      .menu-label {
        font-size: 28rpx;
        color: #111827;
      }
    }

    .menu-arrow {
      font-size: 28rpx;
      color: #9ca3af;
    }
  }
}

.logout-wrap {
  padding: 32rpx 24rpx 0;

  .logout-btn {
    background: #ffffff;
    color: #c81e1e;
    border: 1rpx solid #fca5a5;
    border-radius: 12rpx;
    font-size: 30rpx;
    font-weight: 600;
    height: 88rpx;
    width: 100%;

    &::after { border: none; }
  }
}

.version {
  display: block;
  text-align: center;
  font-size: 22rpx;
  color: #9ca3af;
  padding: 32rpx;
}
</style>
