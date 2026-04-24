<template>
  <view class="page-container">
    <view class="msg-toolbar">
      <view class="segmented">
        <view
          v-for="item in filters"
          :key="item.value"
          class="seg-item"
          :class="{ active: readStatus === item.value }"
          @tap="switchFilter(item.value)"
        >
          {{ item.label }}
        </view>
      </view>
      <text class="read-all" @tap="markAllRead">全部已读</text>
    </view>

    <view class="msg-list" v-if="messages.length">
      <view
        v-for="item in messages"
        :key="item.id"
        class="msg-card"
        :class="{ unread: item.readStatus === 0 }"
        @tap="openMessage(item)"
      >
        <view class="msg-head">
          <text class="msg-type">{{ typeLabel(item.msgType) }}</text>
          <text class="msg-time">{{ formatDate(item.createdAt) }}</text>
        </view>
        <text class="msg-title">{{ item.title }}</text>
        <text class="msg-content">{{ item.content }}</text>
      </view>
    </view>
    <EmptyState v-else icon="🔔" text="暂无消息通知" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { messageApi } from '../../api/message'
import { useUserStore } from '../../stores/user'
import { formatDate } from '../../utils/format'
import EmptyState from '../../components/EmptyState.vue'

const { userInfo, loadUser } = useUserStore()
const messages = ref([])
const readStatus = ref('')
const loading = ref(false)

const filters = [
  { label: '全部', value: '' },
  { label: '未读', value: 0 },
  { label: '已读', value: 1 }
]

function currentUserId() {
  loadUser()
  return userInfo.value?.id
}

function typeLabel(type) {
  const map = { TASK: '任务', HIGH_INTENT: '高意向', SYSTEM: '系统', FOLLOW: '跟进' }
  return map[type] || type || '通知'
}

async function fetchMessages() {
  const userId = currentUserId()
  if (!userId) return
  loading.value = true
  try {
    const data = await messageApi.page({
      userId,
      readStatus: readStatus.value,
      pageNum: 1,
      pageSize: 50
    })
    messages.value = data?.list || []
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

function switchFilter(value) {
  readStatus.value = value
  fetchMessages()
}

async function openMessage(item) {
  if (item.readStatus === 0) {
    await messageApi.read(item.id)
    item.readStatus = 1
  }
  uni.showModal({
    title: item.title || '消息详情',
    content: item.content || '',
    showCancel: false
  })
}

async function markAllRead() {
  const userId = currentUserId()
  if (!userId) return
  await messageApi.readAll(userId)
  uni.showToast({ title: '已全部标记', icon: 'success' })
  fetchMessages()
}

onMounted(fetchMessages)
onPullDownRefresh(fetchMessages)
</script>

<style lang="scss" scoped>
.msg-toolbar {
  background: #ffffff;
  padding: 20rpx 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid #e5e7eb;
}

.segmented {
  display: flex;
  background: #f3f4f6;
  border-radius: 10rpx;
  padding: 4rpx;
}

.seg-item {
  padding: 10rpx 22rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #6b7280;

  &.active {
    background: #ffffff;
    color: #1a56db;
    font-weight: 700;
  }
}

.read-all {
  font-size: 24rpx;
  color: #1a56db;
  font-weight: 600;
}

.msg-list {
  padding: 20rpx 24rpx;
}

.msg-card {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  position: relative;

  &.unread {
    border-left: 6rpx solid #1a56db;
  }
}

.msg-head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.msg-type {
  font-size: 22rpx;
  color: #1a56db;
  background: #e8effd;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  font-weight: 700;
}

.msg-time {
  font-size: 22rpx;
  color: #9ca3af;
}

.msg-title {
  display: block;
  font-size: 30rpx;
  color: #111827;
  font-weight: 700;
  margin-bottom: 8rpx;
}

.msg-content {
  display: block;
  font-size: 26rpx;
  color: #4b5563;
  line-height: 1.6;
}
</style>
