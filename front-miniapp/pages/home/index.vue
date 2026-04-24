<template>
  <view class="page-container">
    <!-- 顶部用户信息 -->
    <view class="user-banner">
      <view class="user-info">
        <text class="greeting">{{ greeting }}，{{ displayName }}</text>
        <text class="dept">{{ displayDept }} · {{ today }}</text>
      </view>
      <view class="avatar">
        <text class="avatar-text">{{ avatarChar }}</text>
      </view>
    </view>

    <!-- 数据概览卡片 -->
    <view class="stats-grid">
      <view class="stat-card" v-for="item in stats" :key="item.label">
        <text class="stat-num" :style="{ color: item.color }">{{ item.value }}</text>
        <text class="stat-label">{{ item.label }}</text>
      </view>
    </view>

    <!-- 快捷操作 -->
    <view class="section">
      <text class="section-title">快捷操作</text>
      <view class="quick-grid">
        <view class="quick-item" v-for="item in quickActions" :key="item.label" @tap="item.action">
          <view class="quick-icon-wrap" :style="{ background: item.bg }">
            <text class="quick-icon">{{ item.icon }}</text>
          </view>
          <text class="quick-label">{{ item.label }}</text>
        </view>
      </view>
    </view>

    <!-- 今日待办 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">今日待办</text>
        <text class="section-more" @tap="goTaskList">全部</text>
      </view>
      <view v-if="todayTasks.length">
        <TaskCard
          v-for="task in todayTasks"
          :key="task.id"
          :task="task"
          @tap="goTaskDetail"
          @complete="completeTask"
        />
      </view>
      <EmptyState v-else icon="✅" text="今日暂无待办任务" />
    </view>

    <!-- 高意向客户 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">高意向客户</text>
        <text class="section-more" @tap="goCustomerList">全部</text>
      </view>
      <view v-if="highIntentCustomers.length">
        <CustomerCard
          v-for="c in highIntentCustomers"
          :key="c.id"
          :customer="c"
          :show-actions="false"
          @tap="goCustomerDetail"
        />
      </view>
      <EmptyState v-else icon="👥" text="暂无高意向客户" />
    </view>

    <view style="height: 32rpx;" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { formatDate } from '../../utils/format'
import TaskCard from '../../components/TaskCard.vue'
import CustomerCard from '../../components/CustomerCard.vue'
import EmptyState from '../../components/EmptyState.vue'

const { userInfo, loadUser } = useUserStore()

const displayName = computed(() => (userInfo.value && userInfo.value.name) || '')
const displayDept = computed(() => (userInfo.value && userInfo.value.deptName) || '')
const avatarChar = computed(() => (userInfo.value && userInfo.value.name && userInfo.value.name.charAt(0)) || '?')

const today = formatDate(Date.now(), 'MM月DD日')

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '上午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

// mock 数据
const stats = ref([
  { label: '今日待跟进', value: 5, color: '#1a56db' },
  { label: '逾期任务', value: 2, color: '#c81e1e' },
  { label: '高意向客户', value: 8, color: '#c27803' },
  { label: '今日新增', value: 1, color: '#057a55' }
])

const todayTasks = ref([
  {
    id: 1, title: '回访张先生购房意向', customerName: '张先生',
    taskType: 'CALL', status: 'PENDING', priority: 'HIGH',
    planTime: new Date().setHours(10, 0)
  },
  {
    id: 2, title: '安排李女士看房', customerName: '李女士',
    taskType: 'VISIT', status: 'PENDING', priority: 'URGENT',
    planTime: new Date().setHours(14, 30)
  },
  {
    id: 3, title: '跟进王先生贷款事宜', customerName: '王先生',
    taskType: 'FOLLOW', status: 'OVERDUE', priority: 'MEDIUM',
    planTime: new Date(Date.now() - 86400000)
  }
])

const highIntentCustomers = ref([
  {
    id: 1, name: '张先生', phone: '13812345678',
    status: 'FOLLOWING', intentLevel: 'HIGH',
    budgetMin: 150, budgetMax: 200,
    focusArea: '天府新区', nextFollowTime: new Date().setHours(10, 0)
  },
  {
    id: 2, name: '李女士', phone: '13987654321',
    status: 'VISITED', intentLevel: 'HIGH',
    budgetMin: 200, budgetMax: 300,
    focusArea: '高新区', nextFollowTime: new Date().setHours(14, 30)
  }
])

const quickActions = [
  { label: '新增客户', icon: '➕', bg: '#e8effd', action: () => uni.navigateTo({ url: '/pages/customer/form' }) },
  { label: '记录跟进', icon: '📝', bg: '#d1fae5', action: () => uni.navigateTo({ url: '/pages/follow/form' }) },
  { label: '创建任务', icon: '📋', bg: '#fef3c7', action: () => uni.navigateTo({ url: '/pages/task/form' }) },
  { label: '客户搜索', icon: '🔍', bg: '#f3f4f6', action: goCustomerList }
]

function goCustomerList() { uni.switchTab({ url: '/pages/customer/list' }) }
function goTaskList() { uni.switchTab({ url: '/pages/task/list' }) }
function goCustomerDetail(c) { uni.navigateTo({ url: `/pages/customer/detail?id=${c.id}` }) }
function goTaskDetail(t) { uni.navigateTo({ url: `/pages/task/detail?id=${t.id}` }) }

function completeTask(task) {
  uni.showModal({
    title: '完成任务',
    content: `确认完成任务「${task.title}」？`,
    success(res) {
      if (res.confirm) {
        task.status = 'DONE'
        uni.showToast({ title: '已完成', icon: 'success' })
      }
    }
  })
}

onMounted(() => {
  loadUser()
})

onPullDownRefresh(() => {
  uni.showToast({ title: '已刷新', icon: 'success' })
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.user-banner {
  background: #1a56db;
  padding: 48rpx 32rpx 40rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .user-info {
    flex: 1;

    .greeting {
      display: block;
      font-size: 36rpx;
      font-weight: 700;
      color: #ffffff;
      margin-bottom: 8rpx;
    }

    .dept {
      font-size: 24rpx;
      color: rgba(255, 255, 255, 0.75);
    }
  }

  .avatar {
    width: 80rpx;
    height: 80rpx;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 9999rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    .avatar-text {
      font-size: 32rpx;
      font-weight: 700;
      color: #ffffff;
    }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  padding: 24rpx 24rpx 0;

  .stat-card {
    background: #ffffff;
    border: 1rpx solid #e5e7eb;
    border-radius: 12rpx;
    padding: 24rpx;
    display: flex;
    flex-direction: column;
    align-items: center;

    .stat-num {
      font-size: 52rpx;
      font-weight: 700;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 22rpx;
      color: #6b7280;
      margin-top: 8rpx;
    }
  }
}

.section {
  padding: 32rpx 24rpx 0;

  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16rpx;

    .section-more {
      font-size: 24rpx;
      color: #1a56db;
    }
  }

  .section-title {
    display: block;
    font-size: 30rpx;
    font-weight: 700;
    color: #111827;
    margin-bottom: 16rpx;
  }
}

.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  gap: 16rpx;

  .quick-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12rpx;

    .quick-icon-wrap {
      width: 96rpx;
      height: 96rpx;
      border-radius: 24rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      .quick-icon {
        font-size: 44rpx;
      }
    }

    .quick-label {
      font-size: 22rpx;
      color: #374151;
    }
  }
}
</style>
