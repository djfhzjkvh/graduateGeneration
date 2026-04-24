<template>
  <view class="customer-card" @tap="$emit('tap', customer)">
    <view class="card-header">
      <view class="name-row">
        <text class="name">{{ customer.name }}</text>
        <StatusTag type="intent" :value="customer.intentLevel" />
        <StatusTag type="customer-status" :value="customer.status" />
      </view>
      <text class="phone">{{ maskPhone(customer.phone) }}</text>
    </view>

    <view class="card-body">
      <view class="info-row" v-if="customer.budgetMin || customer.budgetMax">
        <text class="info-label">预算</text>
        <text class="info-value">{{ formatBudget(customer.budgetMin, customer.budgetMax) }}</text>
      </view>
      <view class="info-row" v-if="customer.focusArea">
        <text class="info-label">关注区域</text>
        <text class="info-value">{{ customer.focusArea }}</text>
      </view>
      <view class="info-row" v-if="customer.nextFollowTime">
        <text class="info-label">下次跟进</text>
        <text class="info-value">{{ formatDate(customer.nextFollowTime) }}</text>
      </view>
    </view>

    <view class="card-footer" v-if="showActions">
      <view class="action-btn" @tap.stop="$emit('call', customer)">
        <text class="action-icon">📞</text>
        <text class="action-text">电话</text>
      </view>
      <view class="action-divider" />
      <view class="action-btn" @tap.stop="$emit('follow', customer)">
        <text class="action-icon">📝</text>
        <text class="action-text">跟进</text>
      </view>
      <view class="action-divider" />
      <view class="action-btn" @tap.stop="$emit('tap', customer)">
        <text class="action-icon">👁</text>
        <text class="action-text">详情</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { maskPhone, formatBudget, formatDate } from '../utils/format'
import StatusTag from './StatusTag.vue'

defineProps({
  customer: { type: Object, required: true },
  showActions: { type: Boolean, default: true }
})
defineEmits(['tap', 'call', 'follow'])
</script>

<style lang="scss" scoped>
.customer-card {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 16rpx;
}

.card-header {
  padding: 24rpx 28rpx 16rpx;

  .name-row {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 8rpx;
  }

  .name {
    font-size: 32rpx;
    font-weight: 600;
    color: #111827;
  }

  .phone {
    font-size: 24rpx;
    color: #6b7280;
  }
}

.card-body {
  padding: 0 28rpx 16rpx;

  .info-row {
    display: flex;
    align-items: center;
    padding: 4rpx 0;

    .info-label {
      font-size: 24rpx;
      color: #9ca3af;
      width: 128rpx;
      flex-shrink: 0;
    }

    .info-value {
      font-size: 24rpx;
      color: #374151;
    }
  }
}

.card-footer {
  display: flex;
  border-top: 1rpx solid #f3f4f6;

  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    padding: 20rpx 0;

    .action-icon {
      font-size: 28rpx;
    }

    .action-text {
      font-size: 24rpx;
      color: #374151;
    }
  }

  .action-divider {
    width: 1rpx;
    background: #f3f4f6;
    margin: 12rpx 0;
  }
}
</style>
