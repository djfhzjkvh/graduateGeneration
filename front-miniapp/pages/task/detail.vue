<template>
  <view class="page-container" style="padding-bottom: 160rpx;">
    <view v-if="task">
      <view class="task-header">
        <view class="header-top">
          <text class="task-title">{{ task.title }}</text>
          <StatusTag type="task-status" :value="task.status" />
        </view>
        <view class="header-meta">
          <StatusTag type="priority" :value="task.priority" />
          <text class="task-type">{{ TASK_TYPE[task.taskType] || task.taskType }}</text>
        </view>
      </view>

      <view class="form-group" style="margin-top:24rpx;">
        <text class="form-group-title">任务信息</text>
        <view class="form-item"><text class="form-label">计划时间</text><text class="form-value">{{ formatDate(task.planTime, 'YYYY-MM-DD HH:mm') }}</text></view>
        <view class="form-item"><text class="form-label">关联客户</text><text class="form-value link" @tap="goCustomer">{{ task.customerName || '--' }}</text></view>
        <view class="form-item" v-if="task.content"><text class="form-label">任务说明</text><text class="form-value text-left">{{ task.content }}</text></view>
        <view class="form-item" v-if="task.doneTime"><text class="form-label">完成时间</text><text class="form-value">{{ formatDate(task.doneTime, 'YYYY-MM-DD HH:mm') }}</text></view>
      </view>
    </view>
    <EmptyState v-else icon="任" text="任务不存在或已被删除" />

    <view class="bottom-bar" v-if="task">
      <button v-if="task.status === 'PENDING'" class="btn btn-primary" style="flex:2" @tap="completeTask">完成任务</button>
      <button class="btn btn-secondary" style="flex:1" @tap="goCustomer">查看客户</button>
      <button class="btn btn-secondary" style="flex:1" @tap="goFollow">记录跟进</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { taskApi } from '../../api/task'
import { formatDate } from '../../utils/format'
import { mapTask } from '../../utils/adapters'
import { TASK_TYPE } from '../../constants/dictionary'
import StatusTag from '../../components/StatusTag.vue'
import EmptyState from '../../components/EmptyState.vue'

const task = ref(null)
let taskId = null

onLoad((options) => {
  taskId = options?.id
  fetchTask()
})

async function fetchTask() {
  const data = await taskApi.list({ pageNum: 1, pageSize: 100 })
  const found = (data?.list || []).find(item => String(item.id) === String(taskId))
  task.value = found ? mapTask(found) : null
}

function completeTask() {
  uni.showModal({
    title: '完成任务',
    content: '确认完成该任务？',
    success: async (res) => {
      if (!res.confirm) return
      await taskApi.complete(task.value.id)
      uni.showToast({ title: '已完成', icon: 'success' })
      fetchTask()
    }
  })
}

function goCustomer() {
  if (!task.value?.customerId) return
  uni.navigateTo({ url: `/pages/customer/detail?id=${task.value.customerId}` })
}

function goFollow() {
  if (!task.value?.customerId) return
  uni.navigateTo({ url: `/pages/follow/form?customerId=${task.value.customerId}&customerName=${encodeURIComponent(task.value.customerName || '')}` })
}
</script>

<style lang="scss" scoped>
.task-header {
  background: #ffffff;
  border-bottom: 1rpx solid #e5e7eb;
  padding: 32rpx 24rpx;

  .header-top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16rpx;
    margin-bottom: 16rpx;

    .task-title {
      font-size: 34rpx;
      font-weight: 700;
      color: #111827;
      flex: 1;
      line-height: 1.4;
    }
  }

  .header-meta {
    display: flex;
    align-items: center;
    gap: 12rpx;

    .task-type {
      font-size: 22rpx;
      color: #6b7280;
    }
  }
}

.form-group {
  margin: 0 24rpx 24rpx;
}

.link {
  color: #1a56db !important;
}

.text-left {
  text-align: left !important;
  flex: 1;
}
</style>
