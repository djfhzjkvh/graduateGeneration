<template>
  <view class="page-container">
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">搜</text>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索客户姓名、手机号"
          placeholder-style="color: #9ca3af"
          @confirm="fetchCustomers"
        />
        <text v-if="keyword" class="search-clear" @tap="clearSearch">×</text>
      </view>
    </view>

    <scroll-view class="filter-bar" scroll-x>
      <view class="filter-list">
        <view class="filter-item" :class="{ active: activeStatus === '' }" @tap="setStatus('')">全部</view>
        <view
          class="filter-item"
          v-for="(cfg, key) in CUSTOMER_STATUS"
          :key="key"
          :class="{ active: activeStatus === key }"
          @tap="setStatus(key)"
        >{{ cfg.label }}</view>
      </view>
    </scroll-view>

    <view class="list-wrap">
      <text class="list-count">共 {{ total }} 位客户</text>
      <CustomerCard
        v-for="c in customers"
        :key="c.id"
        :customer="c"
        @select="goDetail"
        @call="callCustomer"
        @follow="goFollow"
      />
      <EmptyState v-if="!customers.length" icon="客" text="暂无客户" btn-text="新增客户" @action="goCreate" />
    </view>

    <view class="fab" @tap="goCreate">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { CUSTOMER_STATUS } from '../../constants/dictionary'
import { customerApi } from '../../api/customer'
import { mapCustomer } from '../../utils/adapters'
import { callPhone } from '../../utils/phone'
import CustomerCard from '../../components/CustomerCard.vue'
import EmptyState from '../../components/EmptyState.vue'

const keyword = ref('')
const activeStatus = ref('')
const customers = ref([])
const total = ref(0)

async function fetchCustomers() {
  try {
    const data = await customerApi.list({
      keyword: keyword.value,
      status: activeStatus.value,
      pageNum: 1,
      pageSize: 50
    })
    customers.value = (data?.list || []).map(mapCustomer)
    total.value = data?.total || customers.value.length
  } finally {
    uni.stopPullDownRefresh()
  }
}

function setStatus(val) {
  activeStatus.value = val
  fetchCustomers()
}

function clearSearch() {
  keyword.value = ''
  fetchCustomers()
}

function goDetail(c) {
  if (!c?.id) {
    if (c?.type || c?.target || c?.currentTarget) {
      console.warn('[customer-list] native tap event ignored', c)
      return
    }
    console.warn('[customer-list] navigate detail blocked, missing customer id', c)
    uni.showToast({ title: '客户ID缺失，无法查看详情', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages/customer/detail?id=${c.id}` })
}
function goCreate() { uni.navigateTo({ url: '/pages/customer/form' }) }
function goFollow(c) { uni.navigateTo({ url: `/pages/follow/form?customerId=${c.id}&customerName=${encodeURIComponent(c.name)}` }) }
function callCustomer(c) { callPhone(c.phone) }

onMounted(fetchCustomers)
onPullDownRefresh(fetchCustomers)
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
      font-size: 24rpx;
      color: #6b7280;
      margin-right: 12rpx;
    }

    .search-input {
      flex: 1;
      font-size: 28rpx;
      color: #111827;
    }

    .search-clear {
      font-size: 32rpx;
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
