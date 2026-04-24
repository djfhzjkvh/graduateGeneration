<template>
  <view class="page-container" style="padding-bottom: 150rpx;">
    <view class="customer-bar" v-if="customerName">
      <text class="bar-label">当前客户</text>
      <text class="bar-name">{{ customerName }}</text>
    </view>

    <view class="form-group" style="margin-top: 24rpx;">
      <text class="form-group-title">对比条件</text>
      <view class="form-item">
        <text class="form-label required">本楼盘ID</text>
        <input class="form-input" v-model="form.projectId" type="number" placeholder="请输入楼盘ID" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item">
        <text class="form-label required">竞品ID</text>
        <input class="form-input" v-model="form.competitorId" type="number" placeholder="请输入竞品楼盘ID" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item textarea-item">
        <text class="form-label">客户关注</text>
        <textarea class="form-textarea" v-model="form.customerConcern" placeholder="例如：客户觉得竞品价格低、交付更早" placeholder-style="color:#9ca3af" maxlength="300" auto-height />
      </view>
    </view>

    <view class="action-row">
      <button class="btn btn-secondary" style="flex:1" :disabled="loading" @tap="compare">生成对比</button>
      <button class="btn btn-primary" style="flex:1" :disabled="loading" @tap="generateResponse">AI应对话术</button>
    </view>

    <view class="compare-card" v-if="compareResult">
      <text class="card-title">对比摘要</text>
      <view class="compare-grid">
        <view class="compare-col">
          <text class="col-label">本楼盘</text>
          <text class="col-name">{{ compareResult.projectName }}</text>
          <text class="col-line">均价：{{ compareResult.projectAvgPrice || '--' }}</text>
          <text class="col-line">卖点：{{ compareResult.projectHighlights || '--' }}</text>
          <text class="col-line">优惠：{{ compareResult.projectDiscountInfo || '--' }}</text>
        </view>
        <view class="compare-col">
          <text class="col-label">竞品</text>
          <text class="col-name">{{ compareResult.competitorName }}</text>
          <text class="col-line">均价：{{ compareResult.competitorAvgPrice || '--' }}</text>
          <text class="col-line">卖点：{{ compareResult.competitorHighlights || '--' }}</text>
          <text class="col-line">弱点：{{ compareResult.competitorWeakness || '--' }}</text>
        </view>
      </view>
      <text class="summary">{{ compareResult.compareSummary || '暂无摘要' }}</text>
    </view>

    <view class="compare-card" v-if="responseText">
      <view class="card-head">
        <text class="card-title">AI 应对话术</text>
        <text class="copy-btn" @tap="copyResponse">复制</text>
      </view>
      <text class="response">{{ responseText }}</text>
    </view>

    <view class="history-section">
      <text class="section-title">历史关注</text>
      <view v-if="historyList.length">
        <view class="history-card" v-for="item in historyList" :key="item.id">
          <text class="history-title">{{ item.competitorName || '竞品楼盘' }}</text>
          <text class="history-content">{{ item.focusContent || '--' }}</text>
          <text class="history-time">{{ formatDate(item.createdAt) }}</text>
        </view>
      </view>
      <EmptyState v-else icon="🏙️" text="暂无竞品关注历史" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { competitorApi } from '../../api/competitor'
import { customerApi } from '../../api/customer'
import { useUserStore } from '../../stores/user'
import { formatDate } from '../../utils/format'
import { mapCustomer } from '../../utils/adapters'
import EmptyState from '../../components/EmptyState.vue'

const { userInfo, loadUser } = useUserStore()
const customerId = ref(null)
const customerName = ref('')
const loading = ref(false)
const compareResult = ref(null)
const responseText = ref('')
const historyList = ref([])
const form = ref({
  projectId: '',
  competitorId: '',
  customerConcern: ''
})

onLoad(async (options) => {
  customerId.value = options?.customerId || null
  customerName.value = options?.customerName || ''
  loadUser()
  await fetchCustomer()
  fetchHistory()
})

async function fetchCustomer() {
  if (!customerId.value) return

  try {
    const data = mapCustomer(await customerApi.detail(customerId.value))
    customerName.value = data.name || customerName.value
    console.info('[competitor-compare] customer detail loaded', customerId.value)
  } catch (error) {
    console.warn('[competitor-compare] customer detail load failed', error)
  }
}

function payload() {
  return {
    customerId: Number(customerId.value),
    projectId: Number(form.value.projectId),
    competitorId: Number(form.value.competitorId),
    customerConcern: form.value.customerConcern,
    createdBy: userInfo.value?.id
  }
}

function validate() {
  if (!customerId.value) {
    uni.showToast({ title: '缺少客户信息', icon: 'none' })
    return false
  }
  if (!form.value.projectId || !form.value.competitorId) {
    uni.showToast({ title: '请填写楼盘ID和竞品ID', icon: 'none' })
    return false
  }
  return true
}

async function compare() {
  if (!validate()) return
  loading.value = true
  try {
    compareResult.value = await competitorApi.compare(payload())
  } finally {
    loading.value = false
  }
}

async function generateResponse() {
  if (!validate()) return
  loading.value = true
  try {
    const data = await competitorApi.aiResponse(payload())
    responseText.value = data?.responseText || ''
    await fetchHistory()
  } finally {
    loading.value = false
  }
}

async function fetchHistory() {
  if (!customerId.value) return
  historyList.value = await competitorApi.history(customerId.value) || []
}

function copyResponse() {
  uni.setClipboardData({ data: responseText.value })
}
</script>

<style lang="scss" scoped>
.customer-bar {
  background: #e8effd;
  padding: 20rpx 32rpx;
  display: flex;
  gap: 16rpx;
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

.action-row {
  display: flex;
  gap: 16rpx;
  padding: 0 24rpx 24rpx;
}

.compare-card {
  margin: 0 24rpx 24rpx;
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 24rpx;
}

.card-head {
  display: flex;
  justify-content: space-between;
}

.card-title {
  display: block;
  font-size: 30rpx;
  color: #111827;
  font-weight: 800;
  margin-bottom: 16rpx;
}

.copy-btn {
  font-size: 24rpx;
  color: #1a56db;
  font-weight: 700;
}

.compare-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.compare-col {
  background: #f9fafb;
  border-radius: 10rpx;
  padding: 18rpx;
}

.col-label {
  display: block;
  font-size: 22rpx;
  color: #9ca3af;
  margin-bottom: 6rpx;
}

.col-name {
  display: block;
  font-size: 28rpx;
  color: #111827;
  font-weight: 700;
  margin-bottom: 10rpx;
}

.col-line {
  display: block;
  font-size: 23rpx;
  color: #4b5563;
  line-height: 1.5;
}

.summary,
.response {
  display: block;
  font-size: 26rpx;
  color: #374151;
  line-height: 1.7;
  white-space: pre-wrap;
}

.history-section {
  padding: 0 24rpx 24rpx;
}

.section-title {
  display: block;
  font-size: 30rpx;
  color: #111827;
  font-weight: 800;
  margin-bottom: 16rpx;
}

.history-card {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 22rpx;
  margin-bottom: 14rpx;
}

.history-title {
  display: block;
  font-size: 28rpx;
  color: #111827;
  font-weight: 700;
}

.history-content {
  display: block;
  font-size: 25rpx;
  color: #4b5563;
  line-height: 1.6;
  margin-top: 8rpx;
}

.history-time {
  display: block;
  font-size: 22rpx;
  color: #9ca3af;
  margin-top: 8rpx;
}
</style>
