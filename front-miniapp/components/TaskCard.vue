<template>
  <view class="task-card" @tap="$emit('tap', task)">
    <view class="card-left">
      <view class="priority-bar" :style="{ background: priorityCfg.color }" />
    </view>
    <view class="card-content">
      <view class="top-row">
        <text class="title">{{ task.title }}</text>
        <StatusTag type="task-status" :value="task.status" />
      </view>
      <view class="meta-row">
        <text class="customer" v-if="task.customerName">{{ task.customerName }}</text>
        <text class="dot" v-if="task.customerName"> · </text>
        <text class="type">{{ TASK_TYPE[task.taskType] || task.taskType }}</text>
      </view>
      <view class="time-row">
        <text class="time">{{ formatDate(task.planTime, 'MM-DD HH:mm') }}</text>
        <StatusTag type="priority" :value="task.priority" />
      </view>
    </view>
    <view class="card-actions" v-if="showActions">
      <view class="action-btn" @tap.stop="$emit('complete', task)" v-if="task.status === 'PENDING'">
        <text class="action-text complete">完成</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { TASK_TYPE, TASK_PRIORITY } from '../constants/dictionary'
import { formatDate } from '../utils/format'
import StatusTag from './StatusTag.vue'

const props = defineProps({
  task: { type: Object, required: true },
  showActions: { type: Boolean, default: true }
})
defineEmits(['tap', 'complete'])

const priorityCfg = computed(() => TASK_PRIORITY[props.task.priority] || { color: '#6b7280' })
</script>

<style lang="scss" scoped>
.task-card {
  display: flex;
  align-items: stretch;
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 16rpx;
}

.card-left {
  width: 8rpx;
  flex-shrink: 0;

  .priority-bar {
    width: 100%;
    height: 100%;
  }
}

.card-content {
  flex: 1;
  padding: 20rpx 24rpx;

  .top-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8rpx;

    .title {
      font-size: 28rpx;
      font-weight: 600;
      color: #111827;
      flex: 1;
      margin-right: 16rpx;
    }
  }

  .meta-row {
    margin-bottom: 8rpx;

    .customer {
      font-size: 24rpx;
      color: #374151;
    }

    .dot {
      font-size: 24rpx;
      color: #9ca3af;
    }

    .type {
      font-size: 24rpx;
      color: #6b7280;
    }
  }

  .time-row {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .time {
      font-size: 22rpx;
      color: #9ca3af;
    }
  }
}

.card-actions {
  display: flex;
  align-items: center;
  padding: 0 20rpx;

  .action-btn {
    padding: 12rpx 16rpx;

    .action-text {
      font-size: 24rpx;
      font-weight: 600;

      &.complete {
        color: #1a56db;
      }
    }
  }
}
</style>
