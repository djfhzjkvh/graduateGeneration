<template>
  <view class="page-container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索客户姓名、手机号"
          placeholder-style="color: #9ca3af"
          @input="onSearch"
        />
        <text v-if="keyword" class="search-clear" @tap="clearSearch">✕</text>
      </view>
    </view>

    <!-- 筛选标签 -->
    <scroll-view class="filter-bar" scroll-x>
      <view class="filter-list">
        <view
          class="filter-item"
          :class="{ active: activeStatus === '' }"
          @tap="setStatus('')"
        >全部</view>
        <view
          class="filter-item"
          v-for="(cfg, key) in CUSTOMER_STATUS"
          :key="key"
          :class="{ active: activeStatus === key }"
          @tap="setStatus(key)"
        >{{ cfg.label }}</view>
      </view>
    </scroll-view>

    <!-- 客户列表 -->
    <view class="list-wrap">
      <text class="list-count">共 {{ filteredList.length }} 位客户</text>
      <CustomerCard
        v-for="c in filteredList"
        :key="c.id"
        :customer="c"
        @tap="goDetail"
        @call="callCustomer"
        @follow="goFollow"
      />
      <EmptyState v-if="!filteredList.length" icon="👥" text="暂无客户" btn-text="新增客户" @action="goCreate" />
    </view>

    <!-- 新增悬浮按钮 -->
    <view class="fab" @tap="goCreate">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { CUSTOMER_STATUS } from '../../constants/dictionary'
import CustomerCard from '../../components/CustomerCard.vue'
import EmptyState from '../../components/EmptyState.vue'

const keyword = ref('')
const activeStatus = ref('')

// mock 数据
const customers = ref([
  { id: 1, name: '张先生', phone: '13812345678', status: 'FOLLOWING', intentLevel: 'HIGH', budgetMin: 150, budgetMax: 200, focusArea: '天府新区', nextFollowTime: Date.now() + 3600000 },
  { id: 2, name: '李女士', phone: '13987654321', status: 'VISITED', intentLevel: 'HIGH', budgetMin: 200, budgetMax: 300, focusArea: '高新区', nextFollowTime: Date.now() + 7200000 },
  { id: 3, name: '王先生', phone: '13711111111', status: 'NEW', intentLevel: 'MEDIUM', budgetMin: 80, budgetMax: 120, focusArea: '双流区', nextFollowTime: Date.now() + 86400000 },
  { id: 4, name: '赵女士', phone: '13622222222', status: 'FOLLOWING', intentLevel: 'LOW', budgetMin: 60, budgetMax: 80, focusArea: '龙泉驿', nextFollowTime: null },
  { id: 5, name: '刘先生', phone: '13533333333', status: 'DEAL', intentLevel: 'HIGH', budgetMin: 300, budgetMax: 400, focusArea: '锦江区', nextFollowTime: null },
  { id: 6, name: '陈女士', phone: '13444444444', status: 'LOST', intentLevel: 'LOW', budgetMin: 50, budgetMax: 70, focusArea: '青羊区', nextFollowTime: null }
])

const filteredList = computed(() => {
  return customers.value.filter(c => {
    const matchStatus = !activeStatus.value || c.status === activeStatus.value
    const matchKeyword = !keyword.value || c.name.includes(keyword.value) || c.phone.includes(keyword.value)
    return matchStatus && matchKeyword
  })
})

function setStatus(val) { activeStatus.value = val }
function onSearch() {}
function clearSearch() { keyword.value = '' }

function goDetail(c) { uni.navigateTo({ url: `/pages/customer/detail?id=${c.id}` }) }
function goCreate() { uni.navigateTo({ url: '/pages/customer/form' }) }
function goFollow(c) { uni.navigateTo({ url: `/pages/follow/form?customerId=${c.id}&customerName=${c.name}` }) }

function callCustomer(c) {
  uni.makePhoneCall({ phoneNumber: c.phone, fail() {} })
}

onPullDownRefresh(() => {
  uni.showToast({ title: '已刷新', icon: 'success' })
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.search-bar {
  background: #ffffff;
  padding: 16rpx 24rpx;
  border-bottom: 1rpx solid #f3f4f6;

  .search-input-wrap {
    background: #f3f4f6;
    border-radius: 10rpx;
    display: flex;
    align-items: center;
    padding: 0 24rpx;
    height: 72rpx;

    .search-icon {
      font-size: 28rpx;
      margin-right: 12rpx;
    }

    .search-input {
      flex: 1;
      font-size: 28rpx;
      color: #111827;
    }

    .search-clear {
      font-size: 24rpx;
      color: #9ca3af;
      padding: 8rpx;
    }
  }
}

.filter-bar {
  background: #ffffff;
  border-bottom: 1rpx solid #f3f4f6;
  white-space: nowrap;

  .filter-list {
    display: flex;
    padding: 0 16rpx;
    gap: 8rpx;
  }

  .filter-item {
    display: inline-flex;
    align-items: center;
    padding: 16rpx 24rpx;
    font-size: 26rpx;
    color: #6b7280;
    flex-shrink: 0;
    border-bottom: 4rpx solid transparent;

    &.active {
      color: #1a56db;
      font-weight: 600;
      border-bottom-color: #1a56db;
    }
  }
}

.list-wrap {
  padding: 16rpx 24rpx;

  .list-count {
    display: block;
    font-size: 22rpx;
    color: #9ca3af;
    margin-bottom: 16rpx;
  }
}

.fab {
  position: fixed;
  right: 48rpx;
  bottom: calc(120rpx + env(safe-area-inset-bottom));
  width: 96rpx;
  height: 96rpx;
  background: #1a56db;
  border-radius: 9999rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(26, 86, 219, 0.4);
  z-index: 50;

  .fab-icon {
    font-size: 56rpx;
    color: #ffffff;
    line-height: 1;
    margin-top: -4rpx;
  }
}
</style>
