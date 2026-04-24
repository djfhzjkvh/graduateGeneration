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
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { customerApi } from '../../api/customer'
import { formatDate, formatBudget } from '../../utils/format'
import { mapCustomer, mapFollow } from '../../utils/adapters'
import { GENDER, CUSTOMER_SOURCE, PURCHASE_PURPOSE, FOLLOW_METHOD, FOLLOW_RESULT } from '../../constants/dictionary'
import StatusTag from '../../components/StatusTag.vue'
import EmptyState from '../../components/EmptyState.vue'

const customer = ref(null)
const follows = ref([])
let customerId = null

onLoad((options) => {
  customerId = options?.id
})

onShow(() => {
  if (customerId) fetchData()
})

async function fetchData() {
  const [detail, followData] = await Promise.all([
    customerApi.detail(customerId),
    customerApi.follows(customerId, { pageNum: 1, pageSize: 50 }).catch(() => [])
  ])
  customer.value = mapCustomer(detail || {})
  follows.value = (Array.isArray(followData) ? followData : (followData?.list || [])).map(mapFollow)
}

function callCustomer() {
  uni.makePhoneCall({ phoneNumber: customer.value.phone, fail() {} })
}

function goFollow() {
  uni.navigateTo({ url: `/pages/follow/form?customerId=${customer.value.id}&customerName=${encodeURIComponent(customer.value.name)}` })
}

function goEdit() {
  uni.navigateTo({ url: `/pages/customer/form?id=${customer.value.id}` })
}

function showMoreActions() {
  const actions = ['编辑客户', 'AI跟进话术', '客户云笔记', '竞品对比']
  uni.showActionSheet({
    itemList: actions,
    success(res) {
      const id = customer.value.id
      const name = encodeURIComponent(customer.value.name)
      if (res.tapIndex === 0) goEdit()
      if (res.tapIndex === 1) uni.navigateTo({ url: `/pages/ai/script?customerId=${id}&customerName=${name}` })
      if (res.tapIndex === 2) uni.navigateTo({ url: `/pages/note/list?customerId=${id}` })
      if (res.tapIndex === 3) uni.navigateTo({ url: `/pages/competitor/compare?customerId=${id}&customerName=${name}` })
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
</style>
