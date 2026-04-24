<template>
  <view class="page-container" style="padding-bottom: 260rpx;">
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

      <view class="form-group">
        <text class="form-group-title">提醒记录</text>
        <view v-if="remindLogs.length">
          <view class="log-item" v-for="log in remindLogs" :key="log.id">
            <view class="log-head">
              <text class="log-type">{{ REMIND_TYPE[log.remindType] || log.remindType || '提醒' }}</text>
              <text class="log-status">{{ REMIND_STATUS[log.status] || log.status || '--' }}</text>
            </view>
            <text class="log-time">{{ formatDate(log.remindTime || log.createdAt, 'YYYY-MM-DD HH:mm') }}</text>
            <text class="log-msg" v-if="log.resultMsg">{{ log.resultMsg }}</text>
          </view>
        </view>
        <view v-else class="empty-log">暂无提醒记录</view>
      </view>
    </view>
    <EmptyState v-else icon="任" text="任务不存在或已被删除" />

    <view class="bottom-bar task-bottom-bar" v-if="task">
      <button v-if="canOperate" class="btn btn-secondary task-action" @tap="showDelayPopup">延期</button>
      <button v-if="canOperate" class="btn btn-primary task-action" @tap="completeTask">完成</button>
      <button class="btn btn-secondary task-action" @tap="goCustomer">查看客户</button>
      <button class="btn btn-secondary task-action" @tap="goFollow">记录跟进</button>
    </view>

    <view v-if="delayVisible" class="modal-mask" @tap="closeDelayPopup">
      <view class="delay-panel" @tap.stop>
        <view class="delay-title">延期任务</view>
        <view class="delay-field">
          <text class="delay-label">新日期</text>
          <picker mode="date" @change="onDelayDateChange">
            <text class="delay-value" :class="{ placeholder: !delayForm.newTaskDate }">{{ delayForm.newTaskDate || '请选择日期' }}</text>
          </picker>
        </view>
        <view class="delay-field">
          <text class="delay-label">新时间</text>
          <picker mode="time" @change="onDelayTimeChange">
            <text class="delay-value" :class="{ placeholder: !delayForm.newTaskTime }">{{ delayForm.newTaskTime || '请选择时间' }}</text>
          </picker>
        </view>
        <textarea
          class="delay-reason"
          v-model="delayForm.reason"
          placeholder="填写延期原因，便于后续跟进"
          placeholder-style="color:#9ca3af"
          maxlength="200"
          auto-height
        />
        <view class="delay-actions">
          <button class="btn btn-secondary" style="flex:1" @tap="closeDelayPopup">取消</button>
          <button class="btn btn-primary" style="flex:1" :disabled="delaying" @tap="delayTask">{{ delaying ? '提交中...' : '确认延期' }}</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { taskApi } from '../../api/task'
import { formatDate } from '../../utils/format'
import { mapTask } from '../../utils/adapters'
import { TASK_TYPE } from '../../constants/dictionary'
import StatusTag from '../../components/StatusTag.vue'
import EmptyState from '../../components/EmptyState.vue'

const task = ref(null)
const remindLogs = ref([])
const delayVisible = ref(false)
const delaying = ref(false)
const delayForm = ref({ newTaskDate: '', newTaskTime: '', reason: '' })
let taskId = null

const REMIND_TYPE = { BEFORE: '提前提醒', DUE: '到期提醒', OVERDUE: '逾期提醒' }
const REMIND_STATUS = { SUCCESS: '成功', FAILED: '失败', SENT: '已发送', PENDING: '待发送' }
const canOperate = computed(() => ['PENDING', 'OVERDUE', 'DELAYED'].includes(task.value?.status))

onLoad((options) => {
  taskId = options?.id
  if (!taskId || taskId === 'undefined' || !/^\d+$/.test(String(taskId))) {
    console.warn('[task-detail] invalid route id', options)
    uni.showToast({ title: '任务ID缺失，无法查看详情', icon: 'none' })
    taskId = null
    return
  }
  fetchTask()
})

async function fetchTask() {
  if (!taskId) return
  try {
    const data = await taskApi.detail(taskId)
    task.value = mapTask(data?.task || data || {})
    remindLogs.value = data?.remindLogs || []
    console.info('[task-detail] task detail loaded', taskId)
  } catch (error) {
    console.warn('[task-detail] task detail load failed', error)
    task.value = null
    remindLogs.value = []
  }
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

function showDelayPopup() {
  if (!task.value) return
  delayForm.value = {
    newTaskDate: task.value.taskDate || '',
    newTaskTime: task.value.taskTime || '',
    reason: ''
  }
  delayVisible.value = true
}

function closeDelayPopup() {
  if (delaying.value) return
  delayVisible.value = false
}

function onDelayDateChange(e) {
  delayForm.value.newTaskDate = e.detail.value
}

function onDelayTimeChange(e) {
  delayForm.value.newTaskTime = e.detail.value
}

async function delayTask() {
  if (!delayForm.value.newTaskDate) {
    uni.showToast({ title: '请选择新的任务日期', icon: 'none' })
    return
  }

  delaying.value = true
  try {
    await taskApi.delay(task.value.id, delayForm.value)
    console.info('[task-detail] task delayed', task.value.id)
    uni.showToast({ title: '已延期', icon: 'success' })
    delayVisible.value = false
    fetchTask()
  } finally {
    delaying.value = false
  }
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

.task-bottom-bar {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
}

.task-action {
  width: 100%;
  min-width: 0;
  height: 76rpx;
  padding: 0 12rpx;
  font-size: 26rpx;
  line-height: 76rpx;
  white-space: nowrap;
}

.log-item {
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f3f4f6;

  &:last-child {
    border-bottom: none;
  }
}

.log-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.log-type {
  font-size: 26rpx;
  font-weight: 700;
  color: #111827;
}

.log-status {
  font-size: 22rpx;
  color: #1a56db;
  background: #e8effd;
  border-radius: 6rpx;
  padding: 4rpx 12rpx;
}

.log-time,
.log-msg,
.empty-log {
  display: block;
  font-size: 24rpx;
  color: #6b7280;
  line-height: 1.6;
}

.empty-log {
  padding: 28rpx 0;
  text-align: center;
  color: #9ca3af;
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 39, 0.45);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.delay-panel {
  width: 100%;
  background: #ffffff;
  border-radius: 16rpx 16rpx 0 0;
  padding: 32rpx 24rpx calc(32rpx + env(safe-area-inset-bottom));
}

.delay-title {
  font-size: 32rpx;
  font-weight: 800;
  color: #111827;
  margin-bottom: 24rpx;
}

.delay-field {
  min-height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid #f3f4f6;
}

.delay-label {
  font-size: 28rpx;
  color: #374151;
}

.delay-value {
  font-size: 28rpx;
  color: #111827;
}

.placeholder {
  color: #9ca3af;
}

.delay-reason {
  width: 100%;
  box-sizing: border-box;
  min-height: 132rpx;
  margin-top: 24rpx;
  padding: 20rpx;
  border: 1rpx solid #e5e7eb;
  border-radius: 10rpx;
  font-size: 28rpx;
  color: #111827;
}

.delay-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}
</style>
