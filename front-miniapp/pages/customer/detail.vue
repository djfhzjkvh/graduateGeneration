<template>
  <view class="page-container" style="padding-bottom: 160rpx;">
    <view v-if="customer">
      <view class="info-section">
        <view class="customer-head">
          <view class="avatar">
            <text class="avatar-text">{{ customer.name.charAt(0) }}</text>
          </view>
          <view class="head-info">
            <view class="name-row">
              <text class="cus-name">{{ customer.name }}</text>
              <StatusTag type="intent" :value="customer.intentLevel" />
            </view>
            <text class="phone">{{ customer.phone }}</text>
          </view>
          <StatusTag type="customer-status" :value="customer.status" />
        </view>
      </view>

      <view class="form-group">
        <text class="form-group-title">基础信息</text>
        <view class="form-item"><text class="form-label">性别</text><text class="form-value">{{ GENDER[customer.gender] || '--' }}</text></view>
        <view class="form-item"><text class="form-label">年龄</text><text class="form-value">{{ customer.age ? customer.age + '岁' : '--' }}</text></view>
        <view class="form-item"><text class="form-label">客户来源</text><text class="form-value">{{ CUSTOMER_SOURCE[customer.source] || '--' }}</text></view>
        <view class="form-item"><text class="form-label">备注</text><text class="form-value">{{ customer.remark || '--' }}</text></view>
      </view>

      <view class="form-group">
        <text class="form-group-title">购房意向</text>
        <view class="form-item"><text class="form-label">预算范围</text><text class="form-value">{{ formatBudget(customer.budgetMin, customer.budgetMax) }}</text></view>
        <view class="form-item"><text class="form-label">关注区域</text><text class="form-value">{{ customer.focusArea || '--' }}</text></view>
        <view class="form-item"><text class="form-label">户型需求</text><text class="form-value">{{ customer.houseType || '--' }}</text></view>
        <view class="form-item"><text class="form-label">购房目的</text><text class="form-value">{{ PURCHASE_PURPOSE[customer.purpose] || '--' }}</text></view>
        <view class="form-item"><text class="form-label">热度分</text><text class="form-value heat">{{ customer.heatScore ?? '--' }}</text></view>
      </view>

      <view class="form-group">
        <view class="tag-title-row">
          <text class="form-group-title">客户标签</text>
          <text class="tag-edit" @tap="openTagPanel">编辑</text>
        </view>
        <view class="tag-list" v-if="displayTagNames.length">
          <text class="tag-pill" v-for="tag in displayTagNames" :key="tag">{{ tag }}</text>
        </view>
        <view v-else class="empty-tags" @tap="openTagPanel">暂无标签，点击添加</view>
      </view>

      <view class="form-group">
        <text class="form-group-title">跟进时间</text>
        <view class="form-item"><text class="form-label">最近跟进</text><text class="form-value">{{ formatDate(customer.lastFollowTime) }}</text></view>
        <view class="form-item"><text class="form-label">下次跟进</text><text class="form-value">{{ formatDate(customer.nextFollowTime) }}</text></view>
      </view>

      <view class="section-title-bar">
        <text class="section-title">跟进记录</text>
        <text class="section-count">{{ follows.length }}条</text>
      </view>

      <view class="follow-list" v-if="follows.length">
        <view class="follow-item" v-for="f in follows" :key="f.id">
          <view class="follow-head">
            <view class="follow-method-tag">{{ FOLLOW_METHOD[f.method] || f.method }}</view>
            <view class="follow-result-tag" :style="{ color: FOLLOW_RESULT[f.result] ? FOLLOW_RESULT[f.result].color : '' }">
              {{ FOLLOW_RESULT[f.result] ? FOLLOW_RESULT[f.result].label : f.result }}
            </view>
            <text class="follow-time">{{ formatDate(f.createdAt) }}</text>
          </view>
          <text class="follow-content" v-if="f.summary || f.content">{{ f.summary || f.content }}</text>
          <text class="follow-next" v-if="f.nextFollowTime">下次跟进：{{ formatDate(f.nextFollowTime, 'MM-DD HH:mm') }}</text>
        </view>
      </view>
      <EmptyState v-else icon="记" text="暂无跟进记录" />

      <view style="height: 32rpx;" />
    </view>

    <view class="bottom-bar" v-if="customer">
      <button class="btn btn-secondary" style="flex:1" @tap="callCustomer">电话</button>
      <button class="btn btn-primary" style="flex:2" @tap="goFollow">记录跟进</button>
      <button class="btn btn-secondary" style="flex:1" @tap="showMoreActions">更多</button>
    </view>

    <view v-if="tagPanelVisible" class="modal-mask" @tap="closeTagPanel">
      <view class="tag-panel" @tap.stop>
        <view class="tag-panel-head">
          <text class="tag-panel-title">编辑客户标签</text>
          <text class="tag-panel-close" @tap="closeTagPanel">关闭</text>
        </view>
        <view class="tag-options" v-if="availableTags.length">
          <view
            class="tag-option"
            v-for="tag in availableTags"
            :key="tag.id"
            :class="{ active: selectedTagIds.includes(tag.id) }"
            :style="tagOptionStyle(tag)"
            @tap="toggleTag(tag.id)"
          >
            {{ tag.tagName }}
          </view>
        </view>
        <view v-else class="empty-tags">暂无可用标签</view>
        <button class="btn btn-primary btn-block tag-save" :disabled="savingTags" @tap="saveTags">
          {{ savingTags ? '保存中...' : '保存标签' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { customerApi } from '../../api/customer'
import { formatDate, formatBudget } from '../../utils/format'
import { callPhone } from '../../utils/phone'
import { mapCustomer, mapFollow } from '../../utils/adapters'
import { GENDER, CUSTOMER_SOURCE, PURCHASE_PURPOSE, FOLLOW_METHOD, FOLLOW_RESULT } from '../../constants/dictionary'
import StatusTag from '../../components/StatusTag.vue'
import EmptyState from '../../components/EmptyState.vue'

const customer = ref(null)
const follows = ref([])
const availableTags = ref([])
const selectedTagIds = ref([])
const tagPanelVisible = ref(false)
const savingTags = ref(false)
let customerId = null

const displayTagNames = computed(() => customer.value?.tagNames || [])

onLoad((options) => {
  customerId = options?.id
  if (!customerId || customerId === 'undefined') {
    console.warn('[customer-detail] missing route id', options)
    uni.showToast({ title: '客户ID缺失，无法查看详情', icon: 'none' })
    customerId = null
  }
})

onShow(() => {
  if (customerId) fetchData()
})

async function fetchData() {
  const [detail, followData, tagData] = await Promise.all([
    customerApi.detail(customerId),
    customerApi.follows(customerId, { pageNum: 1, pageSize: 50 }).catch(() => []),
    customerApi.tags().catch(() => [])
  ])
  customer.value = mapCustomer(detail || {})
  follows.value = (Array.isArray(followData) ? followData : (followData?.list || [])).map(mapFollow)
  availableTags.value = tagData || []
  selectedTagIds.value = availableTags.value
    .filter(tag => (customer.value.tagNames || []).includes(tag.tagName))
    .map(tag => tag.id)
}

function callCustomer() {
  callPhone(customer.value.phone)
}

function goFollow() {
  uni.navigateTo({ url: `/pages/follow/form?customerId=${customer.value.id}&customerName=${encodeURIComponent(customer.value.name)}` })
}

function goEdit() {
  uni.navigateTo({ url: `/pages/customer/form?id=${customer.value.id}` })
}

function openTagPanel() {
  tagPanelVisible.value = true
}

function closeTagPanel() {
  if (savingTags.value) return
  tagPanelVisible.value = false
}

function toggleTag(tagId) {
  const index = selectedTagIds.value.indexOf(tagId)
  if (index >= 0) {
    selectedTagIds.value.splice(index, 1)
  } else {
    selectedTagIds.value.push(tagId)
  }
}

function tagOptionStyle(tag) {
  const active = selectedTagIds.value.includes(tag.id)
  return active && tag.color ? { borderColor: tag.color, color: tag.color, background: `${tag.color}14` } : {}
}

async function saveTags() {
  savingTags.value = true
  try {
    await customerApi.bindTags(customer.value.id, selectedTagIds.value)
    console.info('[customer-detail] customer tags saved', customer.value.id, selectedTagIds.value)
    uni.showToast({ title: '标签已保存', icon: 'success' })
    tagPanelVisible.value = false
    fetchData()
  } finally {
    savingTags.value = false
  }
}

function showMoreActions() {
  const actions = ['编辑客户', '编辑标签', 'AI跟进话术', '客户云笔记', '竞品对比']
  uni.showActionSheet({
    itemList: actions,
    success(res) {
      const id = customer.value.id
      const name = encodeURIComponent(customer.value.name)
      if (res.tapIndex === 0) goEdit()
      if (res.tapIndex === 1) openTagPanel()
      if (res.tapIndex === 2) uni.navigateTo({ url: `/pages/ai/script?customerId=${id}&customerName=${name}` })
      if (res.tapIndex === 3) uni.navigateTo({ url: `/pages/note/list?customerId=${id}` })
      if (res.tapIndex === 4) uni.navigateTo({ url: `/pages/competitor/compare?customerId=${id}&customerName=${name}` })
    }
  })
}
</script>

<style lang="scss" scoped>
.info-section {
  background: #ffffff;
  border-bottom: 1rpx solid #e5e7eb;
  padding: 32rpx 24rpx;
  margin-bottom: 24rpx;

  .customer-head {
    display: flex;
    align-items: center;
    gap: 24rpx;
  }

  .avatar {
    width: 96rpx;
    height: 96rpx;
    background: #e8effd;
    border-radius: 9999rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .avatar-text {
      font-size: 40rpx;
      font-weight: 700;
      color: #1a56db;
    }
  }

  .head-info {
    flex: 1;
    min-width: 0;

    .name-row {
      display: flex;
      align-items: center;
      gap: 12rpx;
      margin-bottom: 8rpx;
    }

    .cus-name {
      font-size: 36rpx;
      font-weight: 700;
      color: #111827;
    }

    .phone {
      font-size: 26rpx;
      color: #6b7280;
    }
  }
}

.form-group {
  margin: 0 24rpx 24rpx;
}

.heat {
  color: #c81e1e;
  font-weight: 700;
}

.tag-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #f9fafb;
  border-bottom: 1rpx solid #f3f4f6;

  .form-group-title {
    border-bottom: none;
  }
}

.tag-edit {
  padding: 24rpx 32rpx 16rpx;
  font-size: 24rpx;
  font-weight: 700;
  color: #1a56db;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding: 24rpx 32rpx 28rpx;
}

.tag-pill,
.tag-option {
  display: inline-flex;
  align-items: center;
  border-radius: 9999rpx;
  font-size: 24rpx;
  line-height: 1;
}

.tag-pill {
  background: #e8effd;
  color: #1a56db;
  padding: 10rpx 18rpx;
  font-weight: 700;
}

.empty-tags {
  padding: 28rpx 32rpx;
  color: #9ca3af;
  font-size: 26rpx;
}

.section-title-bar {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 0 24rpx 16rpx;

  .section-title {
    font-size: 30rpx;
    font-weight: 700;
    color: #111827;
  }

  .section-count {
    font-size: 22rpx;
    color: #9ca3af;
  }
}

.follow-list {
  padding: 0 24rpx;
}

.follow-item {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;

  .follow-head {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 12rpx;

    .follow-method-tag {
      font-size: 20rpx;
      padding: 4rpx 12rpx;
      background: #f3f4f6;
      border-radius: 6rpx;
      color: #6b7280;
      font-weight: 600;
    }

    .follow-result-tag {
      font-size: 20rpx;
      font-weight: 600;
    }

    .follow-time {
      font-size: 20rpx;
      color: #9ca3af;
      margin-left: auto;
    }
  }

  .follow-content {
    font-size: 26rpx;
    color: #374151;
    line-height: 1.6;
    margin-bottom: 8rpx;
  }

  .follow-next {
    font-size: 22rpx;
    color: #9ca3af;
  }
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 39, 0.45);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.tag-panel {
  width: 100%;
  max-height: 72vh;
  background: #ffffff;
  border-radius: 16rpx 16rpx 0 0;
  padding: 32rpx 24rpx calc(32rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
}

.tag-panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.tag-panel-title {
  font-size: 32rpx;
  font-weight: 800;
  color: #111827;
}

.tag-panel-close {
  font-size: 26rpx;
  color: #6b7280;
}

.tag-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  max-height: 46vh;
  overflow-y: auto;
}

.tag-option {
  border: 1rpx solid #d1d5db;
  color: #374151;
  background: #ffffff;
  padding: 16rpx 22rpx;

  &.active {
    font-weight: 800;
  }
}

.tag-save {
  margin-top: 28rpx;
}
</style>
