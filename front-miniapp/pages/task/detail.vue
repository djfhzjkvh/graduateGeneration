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
          <text class="task-type">{{ TASK_TYPE[task.taskType] }}</text>
        </view>
      </view>

      <view class="form-group" style="margin-top:24rpx;">
        <text class="form-group-title">任务信息</text>
        <view class="form-item">
          <text class="form-label">计划时间</text>
          <text class="form-value">{{ formatDate(task.planTime, 'YYYY-MM-DD HH:mm') }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">关联客户</text>
          <text class="form-value" :style="{ color: '#1a56db' }" @tap="goCustomer">{{ task.customerName }}</text>
        </view>
        <view class="form-item" v-if="task.content">
          <text class="form-label">任务说明</text>
          <text class="form-value" style="text-align:left; flex:1;">{{ task.content }}</text>
        </view>
        <view class="form-item" v-if="task.doneTime">
          <text class="form-label">完成时间</text>
          <text class="form-value">{{ formatDate(task.doneTime, 'YYYY-MM-DD HH:mm') }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">创建时间</text>
          <text class="form-value">{{ formatDate(task.createdAt, 'YYYY-MM-DD HH:mm') }}</text>
        </view>
      </view>
    </view>

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
import { formatDate } from '../../utils/format'
import { TASK_TYPE } from '../../constants/dictionary'
import StatusTag from '../../components/StatusTag.vue'

const task = ref(null)

const MOCK_TASKS = {
  1: { id: 1, title: '回访张先生购房意向', customerName: '张先生', customerId: 1, taskType: 'CALL', status: 'PENDING', priority: 'HIGH', planTime: new Date().setHours(10, 0), content: '上次通话中，客户表示资金快到位，本次主要确认看房时间', createdAt: Date.now() - 86400000, doneTime: null },
  2: { id: 2, title: '安排李女士看房', customerName: '李女士', customerId: 2, taskType: 'VISIT', status: 'PENDING', priority: 'URGENT', planTime: new Date().setHours(14, 30), content: '客户指定要看高新区B地块3-4楼户型', createdAt: Date.now() - 43200000, doneTime: null }
}

onLoad((options) => {
  task.value = MOCK_TASKS[options?.id] || MOCK_TASKS[1]
})

function completeTask() {
  uni.showModal({
    title: '完成任务',
    content: '确认完成该任务？',
    success(res) {
      if (res.confirm) {
        task.value.status = 'DONE'
        task.value.doneTime = Date.now()
        uni.showToast({ title: '已完成', icon: 'success' })
      }
    }
  })
}

function goCustomer() {
  uni.navigateTo({ url: `/pages/customer/detail?id=${task.value.customerId}` })
}

function goFollow() {
  uni.navigateTo({ url: `/pages/follow/form?customerId=${task.value.customerId}&customerName=${task.value.customerName}` })
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
</style>
