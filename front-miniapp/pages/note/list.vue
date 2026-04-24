<template>
  <view class="page-container" style="padding-bottom: 150rpx;">
    <view class="note-toolbar">
      <view>
        <text class="toolbar-title">客户云笔记</text>
        <text class="toolbar-sub">{{ notes.length }} 条复盘记录</text>
      </view>
      <button class="mini-btn" @tap="openEditor()">新增</button>
    </view>

    <view class="note-list" v-if="notes.length">
      <view class="note-card" v-for="item in notes" :key="item.id" @tap="openEditor(item)">
        <view class="note-head">
          <text class="note-type">{{ item.noteType === 'AUTO' ? 'AI复盘' : '手动笔记' }}</text>
          <text class="note-time">{{ formatDate(item.createdAt) }}</text>
        </view>
        <text class="note-title">{{ item.title || '未命名笔记' }}</text>
        <text class="note-summary">{{ item.summary || '--' }}</text>
        <view class="note-extra" v-if="item.objectionTop3 || item.nextTopic">
          <text v-if="item.objectionTop3">异议：{{ item.objectionTop3 }}</text>
          <text v-if="item.nextTopic">下次主题：{{ item.nextTopic }}</text>
        </view>
      </view>
    </view>
    <EmptyState v-else icon="📝" text="暂无客户笔记" btn-text="新增笔记" @action="openEditor()" />

    <view class="bottom-bar">
      <button class="btn btn-secondary" style="flex:1" @tap="fetchNotes">刷新</button>
      <button class="btn btn-primary" style="flex:2" @tap="openEditor()">新增笔记</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { noteApi } from '../../api/note'
import { formatDate } from '../../utils/format'
import EmptyState from '../../components/EmptyState.vue'

const customerId = ref(null)
const notes = ref([])

onLoad((options) => {
  customerId.value = options?.customerId || null
})

onShow(() => {
  if (customerId.value) fetchNotes()
})

async function fetchNotes() {
  if (!customerId.value) return
  notes.value = await noteApi.listByCustomer(customerId.value) || []
}

function openEditor(item) {
  const base = `/pages/note/form?customerId=${customerId.value}`
  uni.navigateTo({ url: item?.id ? `${base}&id=${item.id}` : base })
}
</script>

<style lang="scss" scoped>
.note-toolbar {
  background: #ffffff;
  padding: 24rpx;
  border-bottom: 1rpx solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-title {
  display: block;
  font-size: 32rpx;
  color: #111827;
  font-weight: 800;
}

.toolbar-sub {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  color: #9ca3af;
}

.mini-btn {
  height: 64rpx;
  line-height: 64rpx;
  padding: 0 26rpx;
  background: #1a56db;
  color: #ffffff;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.mini-btn::after { border: none; }

.note-list {
  padding: 20rpx 24rpx;
}

.note-card {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.note-head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.note-type {
  font-size: 22rpx;
  color: #1a56db;
  background: #e8effd;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  font-weight: 700;
}

.note-time {
  font-size: 22rpx;
  color: #9ca3af;
}

.note-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #111827;
  margin-bottom: 8rpx;
}

.note-summary {
  display: block;
  font-size: 26rpx;
  color: #374151;
  line-height: 1.6;
}

.note-extra {
  margin-top: 14rpx;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  font-size: 24rpx;
  color: #6b7280;
}
</style>
