<template>
  <view class="page-container profile-page">
    <view class="profile-header">
      <view class="avatar">
        <text class="avatar-text">{{ avatarChar }}</text>
      </view>
      <view class="header-info">
        <text class="name">{{ displayName }}</text>
        <text class="username">@{{ displayUsername }}</text>
      </view>
      <view class="status-tag" :class="{ disabled: profile.status === 0 }">
        {{ statusText }}
      </view>
    </view>

    <view class="info-section">
      <view class="section-title">账号信息</view>
      <view class="info-list">
        <view class="info-item" v-for="item in accountItems" :key="item.label">
          <text class="info-label">{{ item.label }}</text>
          <text class="info-value">{{ item.value || '-' }}</text>
        </view>
      </view>
    </view>

    <view class="info-section">
      <view class="section-title">组织关系</view>
      <view class="info-list">
        <view class="info-item" v-for="item in orgItems" :key="item.label">
          <text class="info-label">{{ item.label }}</text>
          <text class="info-value">{{ item.value || '-' }}</text>
        </view>
      </view>
    </view>

    <view class="tip-card">
      <text class="tip-title">资料来源</text>
      <text class="tip-text">当前资料来自后端个人信息接口，如需修改请联系管理员在后台维护。</text>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app'
import { authApi } from '../../api/auth'
import { useUserStore } from '../../stores/user'
import { mapUser } from '../../utils/adapters'

const ROLE_LABEL = { ADMIN: '管理员', MANAGER: '销售经理', ADVISOR: '销售顾问' }
const { userInfo, loadUser, setUser } = useUserStore()
const profile = reactive({})

const displayName = computed(() => profile.name || profile.nickname || profile.username || '')
const displayUsername = computed(() => profile.username || '')
const avatarChar = computed(() => (displayName.value && displayName.value.charAt(0)) || '?')
const statusText = computed(() => (profile.status === 0 ? '已停用' : '正常'))

const accountItems = computed(() => [
  { label: '姓名', value: displayName.value },
  { label: '账号', value: profile.username },
  { label: '手机号', value: profile.phone || profile.mobile },
  { label: '角色', value: ROLE_LABEL[profile.role] || profile.roleName || profile.role }
])

const orgItems = computed(() => [
  { label: '部门', value: profile.deptName },
  { label: '直属经理', value: profile.managerName },
  { label: '用户ID', value: profile.id }
])

function applyProfile(nextProfile = {}) {
  Object.keys(profile).forEach((key) => delete profile[key])
  Object.assign(profile, nextProfile)
}

async function fetchProfile() {
  loadUser()
  applyProfile(userInfo.value || {})

  try {
    const data = await authApi.profile()
    const mappedProfile = mapUser(data?.userInfo || data || {})
    setUser(mappedProfile)
    applyProfile(mappedProfile)
    console.info('[profile] profile loaded')
  } catch (error) {
    console.warn('[profile] profile load failed', error)
    uni.showToast({ title: error?.message || '个人资料加载失败', icon: 'none' })
  } finally {
    uni.stopPullDownRefresh()
  }
}

onLoad(fetchProfile)
onPullDownRefresh(fetchProfile)
</script>

<style lang="scss" scoped>
.profile-page {
  padding: 24rpx;
}

.profile-header {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 32rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.avatar {
  width: 112rpx;
  height: 112rpx;
  border-radius: 9999rpx;
  background: #e8effd;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-text {
  font-size: 48rpx;
  font-weight: 700;
  color: #1a56db;
}

.header-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.name {
  font-size: 36rpx;
  line-height: 44rpx;
  font-weight: 700;
  color: #111827;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.username {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #9ca3af;
}

.status-tag {
  flex-shrink: 0;
  padding: 8rpx 18rpx;
  border-radius: 9999rpx;
  background: #ecfdf3;
  color: #047857;
  font-size: 22rpx;
  font-weight: 600;

  &.disabled {
    background: #f3f4f6;
    color: #6b7280;
  }
}

.info-section {
  margin-top: 24rpx;
}

.section-title {
  font-size: 24rpx;
  font-weight: 600;
  color: #6b7280;
  margin: 0 8rpx 12rpx;
}

.info-list {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  overflow: hidden;
}

.info-item {
  min-height: 92rpx;
  padding: 0 28rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24rpx;
  border-bottom: 1rpx solid #f3f4f6;

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  flex-shrink: 0;
  font-size: 28rpx;
  color: #374151;
}

.info-value {
  flex: 1;
  min-width: 0;
  text-align: right;
  font-size: 28rpx;
  color: #111827;
  word-break: break-all;
}

.tip-card {
  margin-top: 24rpx;
  background: #f9fafb;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 24rpx;
}

.tip-title {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #374151;
  margin-bottom: 8rpx;
}

.tip-text {
  font-size: 24rpx;
  color: #6b7280;
  line-height: 1.7;
}
</style>
