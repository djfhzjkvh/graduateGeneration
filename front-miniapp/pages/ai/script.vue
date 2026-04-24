<template>
  <view class="page-container" style="padding-bottom: 150rpx;">
    <view class="customer-bar" v-if="customerName">
      <text class="bar-label">服务客户</text>
      <text class="bar-name">{{ customerName }}</text>
    </view>

    <view class="form-group" style="margin-top: 24rpx;">
      <text class="form-group-title">生成条件</text>
      <view class="form-item" @tap="pickScene">
        <text class="form-label required">跟进场景</text>
        <text class="form-value">{{ sceneLabel }}</text>
        <text class="form-arrow">›</text>
      </view>
      <view class="form-item" @tap="pickChannel">
        <text class="form-label required">沟通渠道</text>
        <text class="form-value">{{ channelLabel }}</text>
        <text class="form-arrow">›</text>
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">补充要求</text>
      <view class="form-item textarea-item">
        <textarea
          class="form-textarea"
          v-model="customPrompt"
          placeholder="例如：客户担心总价偏高，希望强调付款节奏和地铁配套"
          placeholder-style="color:#9ca3af"
          maxlength="300"
          auto-height
        />
      </view>
    </view>

    <view class="script-card" v-if="scriptText">
      <view class="script-head">
        <text class="script-title">AI 推荐话术</text>
        <text class="copy-btn" @tap="copyScript">复制</text>
      </view>
      <text class="script-content">{{ scriptText }}</text>
      <text class="script-meta" v-if="modelName">{{ modelName }} · {{ tokenUsage || 0 }} tokens</text>
    </view>

    <EmptyState v-else icon="✨" text="填写场景后生成一段可直接沟通的话术" />

    <view class="bottom-bar">
      <button class="btn btn-primary btn-block" :disabled="loading" @tap="generate">
        {{ loading ? '生成中...' : '生成跟进话术' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { aiApi } from '../../api/ai'
import { customerApi } from '../../api/customer'
import { mapCustomer } from '../../utils/adapters'
import EmptyState from '../../components/EmptyState.vue'

const customerId = ref(null)
const customerName = ref('')
const sceneType = ref('FOLLOW_UP')
const channelType = ref('WECHAT')
const customPrompt = ref('')
const loading = ref(false)
const scriptText = ref('')
const modelName = ref('')
const tokenUsage = ref(0)

const scenes = [
  { label: '首次跟进', value: 'FIRST_CONTACT' },
  { label: '持续跟进', value: 'FOLLOW_UP' },
  { label: '邀约看房', value: 'VISIT_INVITE' },
  { label: '异议处理', value: 'OBJECTION' },
  { label: '成交推进', value: 'CLOSING' }
]

const channels = [
  { label: '微信', value: 'WECHAT' },
  { label: '电话', value: 'PHONE' },
  { label: '短信', value: 'SMS' },
  { label: '到访面谈', value: 'VISIT' }
]

const sceneLabel = computed(() => scenes.find(i => i.value === sceneType.value)?.label || '请选择')
const channelLabel = computed(() => channels.find(i => i.value === channelType.value)?.label || '请选择')

onLoad(async (options) => {
  customerId.value = options?.customerId || null
  customerName.value = options?.customerName || ''
  await fetchCustomer()
})

async function fetchCustomer() {
  if (!customerId.value) return

  try {
    const data = mapCustomer(await customerApi.detail(customerId.value))
    customerName.value = data.name || customerName.value
    console.info('[ai-script] customer detail loaded', customerId.value)
  } catch (error) {
    console.warn('[ai-script] customer detail load failed', error)
  }
}

function pickScene() {
  uni.showActionSheet({
    itemList: scenes.map(i => i.label),
    success(res) { sceneType.value = scenes[res.tapIndex].value }
  })
}

function pickChannel() {
  uni.showActionSheet({
    itemList: channels.map(i => i.label),
    success(res) { channelType.value = channels[res.tapIndex].value }
  })
}

async function generate() {
  if (!customerId.value) {
    uni.showToast({ title: '缺少客户信息', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const data = await aiApi.generateScript({
      customerId: Number(customerId.value),
      sceneType: sceneType.value,
      channelType: channelType.value,
      customPrompt: customPrompt.value
    })
    scriptText.value = data?.scriptText || ''
    modelName.value = data?.modelName || ''
    tokenUsage.value = data?.tokenUsage || 0
  } finally {
    loading.value = false
  }
}

function copyScript() {
  uni.setClipboardData({ data: scriptText.value })
}
</script>

<style lang="scss" scoped>
.customer-bar {
  background: #e8effd;
  padding: 20rpx 32rpx;
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.bar-label {
  font-size: 24rpx;
  color: #1a56db;
  font-weight: 700;
}

.bar-name {
  font-size: 28rpx;
  color: #111827;
  font-weight: 700;
}

.textarea-item {
  align-items: flex-start;
  padding-top: 20rpx;
}

.script-card {
  margin: 0 24rpx 24rpx;
  background: #ffffff;
  border: 1rpx solid #dbeafe;
  border-radius: 12rpx;
  padding: 28rpx;
}

.script-head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 18rpx;
}

.script-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #111827;
}

.copy-btn {
  font-size: 24rpx;
  color: #1a56db;
  font-weight: 700;
}

.script-content {
  display: block;
  font-size: 28rpx;
  color: #1f2937;
  line-height: 1.7;
  white-space: pre-wrap;
}

.script-meta {
  display: block;
  margin-top: 18rpx;
  font-size: 22rpx;
  color: #9ca3af;
}
</style>
