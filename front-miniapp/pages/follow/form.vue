<template>
  <view class="page-container" style="padding-bottom: 160rpx;">
    <!-- 关联客户 -->
    <view class="customer-info-bar" v-if="customerName">
      <text class="ci-label">跟进客户</text>
      <text class="ci-name">{{ customerName }}</text>
      <text class="script-link" @tap="goAiScript">AI话术</text>
    </view>

    <view class="form-group" style="margin-top: 24rpx;">
      <text class="form-group-title">跟进信息</text>
      <view class="form-item" @tap="showPicker('method')">
        <text class="form-label required">跟进方式</text>
        <text class="form-value">{{ FOLLOW_METHOD[form.method] || '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
      <view class="form-item" @tap="showPicker('result')">
        <text class="form-label required">跟进结果</text>
        <text class="form-value">{{ followResultLabel || '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">跟进内容</text>
      <view class="form-item" style="align-items: flex-start; padding-top: 20rpx;">
        <textarea
          class="form-textarea"
          v-model="form.content"
          placeholder="详细记录此次沟通内容..."
          placeholder-style="color:#9ca3af"
          maxlength="1000"
          auto-height
          style="min-height: 200rpx;"
        />
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">跟进摘要</text>
      <view class="form-item" style="align-items: flex-start; padding-top: 20rpx;">
        <textarea
          class="form-textarea"
          v-model="form.summary"
          placeholder="简要总结（将显示在客户详情）..."
          placeholder-style="color:#9ca3af"
          maxlength="200"
          auto-height
        />
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">下次跟进</text>
      <view class="form-item">
        <text class="form-label">下次时间</text>
        <picker mode="date" @change="onDateChange">
          <text class="form-value" :class="{ placeholder: !form.nextFollowDate }">
            {{ form.nextFollowDate || '选择日期' }}
          </text>
        </picker>
        <text class="form-arrow">›</text>
      </view>
      <view class="form-item" v-if="form.method === 'PHONE'">
        <text class="form-label">通话时长</text>
        <input class="form-input" v-model="form.duration" type="number" placeholder="分钟" placeholder-style="color:#9ca3af" />
      </view>
    </view>

    <view style="height: 32rpx;" />

    <view class="bottom-bar">
      <button class="btn btn-primary btn-block" :disabled="saving" @tap="save">
        {{ saving ? '保存中...' : '保存跟进记录' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { FOLLOW_METHOD, FOLLOW_RESULT } from '../../constants/dictionary'
import { followApi } from '../../api/follow'
import { customerApi } from '../../api/customer'
import { useUserStore } from '../../stores/user'
import { mapCustomer } from '../../utils/adapters'

const form = ref({ method: '', result: '', content: '', summary: '', nextFollowDate: '', duration: '' })
const saving = ref(false)
const customerName = ref('')
let customerId = null
const { userInfo, loadUser } = useUserStore()

const followResultLabel = computed(() => {
  const cfg = FOLLOW_RESULT[form.value.result]
  return cfg ? cfg.label : ''
})

onLoad(async (options) => {
  customerId = options?.customerId
  customerName.value = options?.customerName || ''
  loadUser()
  await fetchCustomer()
})

async function fetchCustomer() {
  if (!customerId) return

  try {
    const data = mapCustomer(await customerApi.detail(customerId))
    customerName.value = data.name || customerName.value
    console.info('[follow-form] customer detail loaded', customerId)
  } catch (error) {
    console.warn('[follow-form] customer detail load failed', error)
  }
}

const PICKER_CFG = {
  method: { keys: Object.keys(FOLLOW_METHOD), labels: Object.values(FOLLOW_METHOD) },
  result: { keys: Object.keys(FOLLOW_RESULT), labels: Object.values(FOLLOW_RESULT).map(v => v.label) }
}

function showPicker(field) {
  const cfg = PICKER_CFG[field]
  uni.showActionSheet({
    itemList: cfg.labels,
    success(res) { form.value[field] = cfg.keys[res.tapIndex] }
  })
}

function onDateChange(e) { form.value.nextFollowDate = e.detail.value }

function goAiScript() {
  if (!customerId) {
    uni.showToast({ title: '请先选择客户', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages/ai/script?customerId=${customerId}&customerName=${encodeURIComponent(customerName.value)}` })
}

async function save() {
  if (!form.value.method) { uni.showToast({ title: '请选择跟进方式', icon: 'none' }); return }
  if (!form.value.result) { uni.showToast({ title: '请选择跟进结果', icon: 'none' }); return }
  if (!form.value.content.trim()) { uni.showToast({ title: '请填写跟进内容', icon: 'none' }); return }

  saving.value = true
  try {
    await followApi.create({
      customerId: Number(customerId),
      followType: form.value.method,
      followResult: form.value.result,
      content: form.value.content,
      summary: form.value.summary,
      nextFollowTime: form.value.nextFollowDate,
      userId: userInfo.value?.id
    })
  } finally {
    saving.value = false
  }

  // 如果填写了下次跟进时间，提示是否创建任务
  if (form.value.nextFollowDate) {
    uni.showModal({
      title: '创建跟进任务',
      content: `是否为 ${form.value.nextFollowDate} 创建一个跟进任务？`,
      confirmText: '创建',
      cancelText: '不了',
      success(res) {
        if (res.confirm) {
          uni.navigateTo({ url: `/pages/task/form?customerId=${customerId}&customerName=${encodeURIComponent(customerName.value)}` })
        } else {
          uni.showToast({ title: '记录成功', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 1200)
        }
      }
    })
  } else {
    uni.showToast({ title: '记录成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1200)
  }
}
</script>

<style lang="scss" scoped>
.customer-info-bar {
  background: #e8effd;
  padding: 20rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;

  .ci-label {
    font-size: 24rpx;
    color: #1a56db;
    font-weight: 600;
  }

  .ci-name {
    font-size: 28rpx;
    color: #111827;
    font-weight: 600;
  }

  .script-link {
    margin-left: auto;
    font-size: 24rpx;
    color: #1a56db;
    font-weight: 700;
  }
}

.form-group {
  margin: 0 24rpx 24rpx;
}

.placeholder {
  color: #9ca3af !important;
}
</style>
