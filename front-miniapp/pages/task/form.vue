<template>
  <view class="page-container" style="padding-bottom: 160rpx;">
    <view class="customer-info-bar" v-if="customerName">
      <text class="ci-label">关联客户</text>
      <text class="ci-name">{{ customerName }}</text>
    </view>

    <view class="form-group" style="margin-top: 24rpx;">
      <text class="form-group-title">任务信息</text>
      <view class="form-item" @tap="showPicker('taskType')">
        <text class="form-label required">任务类型</text>
        <text class="form-value">{{ TASK_TYPE[form.taskType] || '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
      <view class="form-item">
        <text class="form-label required">任务标题</text>
        <input class="form-input" v-model="form.title" placeholder="请输入任务标题" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item" @tap="showPicker('priority')">
        <text class="form-label">优先级</text>
        <text class="form-value">{{ TASK_PRIORITY[form.priority] ? TASK_PRIORITY[form.priority].label : '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">时间设置</text>
      <view class="form-item">
        <text class="form-label required">任务日期</text>
        <picker mode="date" @change="e => form.date = e.detail.value">
          <text class="form-value" :class="{ placeholder: !form.date }">{{ form.date || '选择日期' }}</text>
        </picker>
        <text class="form-arrow">›</text>
      </view>
      <view class="form-item">
        <text class="form-label">任务时间</text>
        <picker mode="time" @change="e => form.time = e.detail.value">
          <text class="form-value" :class="{ placeholder: !form.time }">{{ form.time || '选择时间' }}</text>
        </picker>
        <text class="form-arrow">›</text>
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">任务说明</text>
      <view class="form-item" style="align-items: flex-start; padding-top: 20rpx;">
        <textarea
          class="form-textarea"
          v-model="form.content"
          placeholder="添加任务详情..."
          placeholder-style="color:#9ca3af"
          maxlength="500"
          auto-height
        />
      </view>
    </view>

    <view style="height: 32rpx;" />

    <view class="bottom-bar">
      <button class="btn btn-primary btn-block" :disabled="saving" @tap="save">
        {{ saving ? '保存中...' : '创建任务' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { TASK_TYPE, TASK_PRIORITY } from '../../constants/dictionary'

const form = ref({ taskType: '', title: '', priority: 'MEDIUM', date: '', time: '', content: '' })
const saving = ref(false)
const customerName = ref('')
let customerId = null

onLoad((options) => {
  customerId = options?.customerId
  customerName.value = options?.customerName || ''
  if (options?.customerName) {
    form.value.title = `跟进${options.customerName}`
  }
})

const PICKER_CFG = {
  taskType: { keys: Object.keys(TASK_TYPE), labels: Object.values(TASK_TYPE) },
  priority: { keys: Object.keys(TASK_PRIORITY), labels: Object.values(TASK_PRIORITY).map(v => v.label) }
}

function showPicker(field) {
  const cfg = PICKER_CFG[field]
  uni.showActionSheet({
    itemList: cfg.labels,
    success(res) { form.value[field] = cfg.keys[res.tapIndex] }
  })
}

async function save() {
  if (!form.value.taskType) { uni.showToast({ title: '请选择任务类型', icon: 'none' }); return }
  if (!form.value.title.trim()) { uni.showToast({ title: '请输入任务标题', icon: 'none' }); return }
  if (!form.value.date) { uni.showToast({ title: '请选择任务日期', icon: 'none' }); return }
  saving.value = true
  await new Promise(r => setTimeout(r, 600))
  saving.value = false
  uni.showToast({ title: '任务已创建', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 1200)
}
</script>

<style lang="scss" scoped>
.customer-info-bar {
  background: #e8effd;
  padding: 20rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;

  .ci-label { font-size: 24rpx; color: #1a56db; font-weight: 600; }
  .ci-name { font-size: 28rpx; color: #111827; font-weight: 600; }
}

.form-group { margin: 0 24rpx 24rpx; }
.placeholder { color: #9ca3af !important; }
</style>
