<template>
  <view class="page-container" style="padding-bottom: 160rpx;">
    <view v-if="customer">
      <!-- 基础信息卡 -->
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

      <!-- 详细信息 -->
      <view class="form-group">
        <text class="form-group-title">基础信息</text>
        <view class="form-item">
          <text class="form-label">性别</text>
          <text class="form-value">{{ GENDER[customer.gender] || '--' }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">年龄</text>
          <text class="form-value">{{ customer.age ? customer.age + '岁' : '--' }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">客户来源</text>
          <text class="form-value">{{ CUSTOMER_SOURCE[customer.source] || '--' }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">备注</text>
          <text class="form-value">{{ customer.remark || '--' }}</text>
        </view>
      </view>

      <view class="form-group">
        <text class="form-group-title">购房意向</text>
        <view class="form-item">
          <text class="form-label">预算范围</text>
          <text class="form-value">{{ formatBudget(customer.budgetMin, customer.budgetMax) }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">关注区域</text>
          <text class="form-value">{{ customer.focusArea || '--' }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">户型需求</text>
          <text class="form-value">{{ customer.houseType || '--' }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">购房目的</text>
          <text class="form-value">{{ PURCHASE_PURPOSE[customer.purpose] || '--' }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">热度分</text>
          <text class="form-value heat">{{ customer.heatScore !== null && customer.heatScore !== undefined ? customer.heatScore : '--' }}</text>
        </view>
      </view>

      <view class="form-group">
        <text class="form-group-title">跟进时间</text>
        <view class="form-item">
          <text class="form-label">最近跟进</text>
          <text class="form-value">{{ formatDate(customer.lastFollowTime) }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">下次跟进</text>
          <text class="form-value">{{ formatDate(customer.nextFollowTime) }}</text>
        </view>
      </view>

      <!-- 跟进记录 -->
      <view class="section-title-bar">
        <text class="section-title">跟进记录</text>
        <text class="section-count">{{ follows.length }}条</text>
      </view>

      <view class="follow-list" v-if="follows.length">
        <view class="follow-item" v-for="f in follows" :key="f.id">
          <view class="follow-head">
            <view class="follow-method-tag">{{ FOLLOW_METHOD[f.method] }}</view>
            <view class="follow-result-tag" :style="{ color: FOLLOW_RESULT[f.result] ? FOLLOW_RESULT[f.result].color : '' }">
              {{ FOLLOW_RESULT[f.result] ? FOLLOW_RESULT[f.result].label : '' }}
            </view>
            <text class="follow-time">{{ formatDate(f.createdAt) }}</text>
          </view>
          <text class="follow-content" v-if="f.summary">{{ f.summary }}</text>
          <text class="follow-next" v-if="f.nextFollowTime">下次跟进：{{ formatDate(f.nextFollowTime, 'MM-DD HH:mm') }}</text>
        </view>
      </view>
      <EmptyState v-else icon="📝" text="暂无跟进记录" />

      <view style="height: 32rpx;" />
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <button class="btn btn-secondary" style="flex:1" @tap="callCustomer">📞 电话</button>
      <button class="btn btn-primary" style="flex:2" @tap="goFollow">记录跟进</button>
      <button class="btn btn-secondary" style="flex:1" @tap="goEdit">编辑</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { formatDate, formatBudget } from '../../utils/format'
import { CUSTOMER_STATUS, INTENT_LEVEL, GENDER, CUSTOMER_SOURCE, PURCHASE_PURPOSE, FOLLOW_METHOD, FOLLOW_RESULT } from '../../constants/dictionary'
import StatusTag from '../../components/StatusTag.vue'
import EmptyState from '../../components/EmptyState.vue'

const customer = ref(null)
const follows = ref([])
let customerId = null

// mock 数据
const MOCK_CUSTOMERS = {
  1: { id: 1, name: '张先生', phone: '13812345678', gender: 'MALE', age: 35, source: 'WECHAT', status: 'FOLLOWING', intentLevel: 'HIGH', budgetMin: 150, budgetMax: 200, focusArea: '天府新区', houseType: '三室两厅', purpose: 'SELF_USE', heatScore: 85, remark: '急需在6月前购房', lastFollowTime: Date.now() - 86400000, nextFollowTime: Date.now() + 3600000 },
  2: { id: 2, name: '李女士', phone: '13987654321', gender: 'FEMALE', age: 28, source: 'VISIT', status: 'VISITED', intentLevel: 'HIGH', budgetMin: 200, budgetMax: 300, focusArea: '高新区', houseType: '两室一厅', purpose: 'INVEST', heatScore: 92, remark: '看过两套，比较满意', lastFollowTime: Date.now() - 3600000, nextFollowTime: Date.now() + 7200000 }
}

const MOCK_FOLLOWS = {
  1: [
    { id: 1, method: 'PHONE', result: 'CONNECTED', summary: '客户表示近期资金到位，希望尽快看房，对天府新区三室户型感兴趣', nextFollowTime: Date.now() + 3600000, createdAt: Date.now() - 86400000 },
    { id: 2, method: 'WECHAT', result: 'WAITING', summary: '发送了天府新区新盘资料，客户表示需要再考虑', nextFollowTime: null, createdAt: Date.now() - 172800000 }
  ],
  2: [
    { id: 3, method: 'VISIT', result: 'VISITED', summary: '带客户参观了高新区两套房源，客户对B区2号楼较为满意，有意向签约', nextFollowTime: Date.now() + 7200000, createdAt: Date.now() - 3600000 }
  ]
}

onLoad((options) => {
  customerId = options && options.id
  customer.value = MOCK_CUSTOMERS[customerId] || MOCK_CUSTOMERS[1]
  follows.value = MOCK_FOLLOWS[customerId] || []
})

function callCustomer() {
  uni.makePhoneCall({ phoneNumber: customer.value.phone, fail() {} })
}

function goFollow() {
  uni.navigateTo({ url: `/pages/follow/form?customerId=${customer.value.id}&customerName=${customer.value.name}` })
}

function goEdit() {
  uni.navigateTo({ url: `/pages/customer/form?id=${customer.value.id}` })
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
