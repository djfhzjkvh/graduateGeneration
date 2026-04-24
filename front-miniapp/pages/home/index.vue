<template>
  <view class="page-container">
    <view class="user-banner">
      <view class="user-info">
        <text class="greeting">{{ greeting }}，{{ displayName }}</text>
        <text class="dept">{{ displayDept }} · {{ today }}</text>
      </view>
      <view class="avatar">
        <text class="avatar-text">{{ avatarChar }}</text>
      </view>
    </view>

    <view class="admin-entry" v-if="isAdmin">
      <text class="admin-title">管理员请使用后台管理端</text>
      <text class="admin-desc">小程序端保留系统总览入口提示，系统配置、账号权限和全局数据管理请在后台管理端完成。</text>
    </view>

    <view class="stats-grid" v-else>
      <view class="stat-card" v-for="item in stats" :key="item.label">
        <text class="stat-num" :style="{ color: item.color }">{{ item.value }}</text>
        <text class="stat-label">{{ item.label }}</text>
      </view>
    </view>

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

    <view class="section role-dashboard" v-if="roleDashboardItems.length">
      <view class="section-header">
        <text class="section-title">{{ dashboardTitle }}</text>
        <text class="section-more" v-if="isManager">团队</text>
      </view>
      <view class="mini-stats">
        <view class="mini-stat" v-for="item in roleDashboardItems" :key="item.label">
          <text class="mini-num">{{ item.value || 0 }}</text>
          <text class="mini-label">{{ item.label }}</text>
        </view>
      </view>
      <view class="rank-list" v-if="advisorRankList.length">
        <view class="rank-item" v-for="item in advisorRankList" :key="item.advisorId">
          <text class="rank-name">{{ item.advisorName || '顾问' }}</text>
          <text class="rank-meta">客户 {{ item.customerCount || 0 }} · 高意向 {{ item.highIntentCustomerCount || 0 }} · 待办 {{ item.pendingTaskCount || 0 }}</text>
        </view>
      </view>
    </view>

    <view class="section" v-if="!isAdmin">
      <view class="section-header">
        <text class="section-title">{{ taskSectionTitle }}</text>
        <text class="section-more" @tap="goTaskList">全部</text>
      </view>
      <view v-if="todayTasks.length">
        <TaskCard
          v-for="task in todayTasks"
          :key="task.id"
          :task="task"
          @select="goTaskDetail"
          @complete="completeTask"
        />
      </view>
      <EmptyState v-else icon="✓" :text="taskEmptyText" />
    </view>

    <view class="section" v-if="!isAdmin">
      <view class="section-header">
        <text class="section-title">{{ customerSectionTitle }}</text>
        <text class="section-more" @tap="goCustomerList">全部</text>
      </view>
      <view v-if="customerPreviewList.length">
        <CustomerCard
          v-for="c in customerPreviewList"
          :key="c.id"
          :customer="c"
          :show-actions="false"
          @select="goCustomerDetail"
        />
      </view>
      <EmptyState v-else icon="客" :text="customerEmptyText" />
    </view>

    <view style="height: 32rpx;" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { dashboardApi } from '../../api/dashboard'
import { customerApi } from '../../api/customer'
import { taskApi } from '../../api/task'
import { formatDate } from '../../utils/format'
import { mapCustomer, mapTask } from '../../utils/adapters'
import TaskCard from '../../components/TaskCard.vue'
import CustomerCard from '../../components/CustomerCard.vue'
import EmptyState from '../../components/EmptyState.vue'

const { userInfo, loadUser } = useUserStore()
const todayTasks = ref([])
const customerPreviewList = ref([])
const roleDashboard = ref(null)
const overview = ref({
  todayTaskCount: 0,
  overdueTaskCount: 0,
  highIntentCustomerCount: 0,
  newCustomerCount: 0
})

const displayName = computed(() => userInfo.value?.name || userInfo.value?.username || '')
const displayDept = computed(() => userInfo.value?.deptName || '')
const userRole = computed(() => String(userInfo.value?.role || 'ADVISOR').toUpperCase())
const isAdmin = computed(() => userRole.value === 'ADMIN')
const isManager = computed(() => userRole.value === 'MANAGER')
const isAdvisor = computed(() => userRole.value === 'ADVISOR')
const avatarChar = computed(() => (displayName.value || '?').charAt(0))
const today = formatDate(Date.now(), 'MM月DD日')

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '上午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const stats = computed(() => {
  if (isManager.value) {
    return [
      { label: '团队待办', value: overview.value.todayTaskCount || 0, color: '#1a56db' },
      { label: '团队逾期', value: overview.value.overdueTaskCount || 0, color: '#c81e1e' },
      { label: '高意向客户', value: overview.value.highIntentCustomerCount || 0, color: '#c27803' },
      { label: '团队新增', value: overview.value.newCustomerCount || 0, color: '#057a55' }
    ]
  }
  return [
    { label: '我的待办', value: overview.value.todayTaskCount || 0, color: '#1a56db' },
    { label: '逾期任务', value: overview.value.overdueTaskCount || 0, color: '#c81e1e' },
    { label: '我的客户', value: roleDashboard.value?.customerCount || 0, color: '#c27803' },
    { label: '今日新增', value: overview.value.newCustomerCount || 0, color: '#057a55' }
  ]
})

const quickActions = computed(() => {
  if (isAdmin.value) {
    return [
      { label: '系统总览', icon: '览', bg: '#e8effd', action: showAdminTip },
      { label: '后台管理', icon: '管', bg: '#f3f4f6', action: showAdminTip }
    ]
  }
  if (isManager.value) {
    return [
      { label: '团队客户', icon: '客', bg: '#e8effd', action: goCustomerList },
      { label: '顾问排行', icon: '榜', bg: '#fef3c7', action: scrollToDashboard },
      { label: 'AI助手', icon: '问', bg: '#d1fae5', action: () => uni.navigateTo({ url: '/pages/ai/chat' }) },
      { label: '高意向池', icon: '热', bg: '#fee2e2', action: () => uni.navigateTo({ url: '/pages/customer/high-intent' }) }
    ]
  }
  return [
    { label: '我的客户', icon: '客', bg: '#e8effd', action: goCustomerList },
    { label: '我的任务', icon: '✓', bg: '#d1fae5', action: goTaskList },
    { label: '新增客户', icon: '+', bg: '#f3f4f6', action: () => uni.navigateTo({ url: '/pages/customer/form' }) },
    { label: 'AI线索', icon: 'AI', bg: '#fef3c7', action: () => uni.navigateTo({ url: '/pages/ai/lead' }) }
  ]
})

const dashboardTitle = computed(() => {
  if (isManager.value) return '团队看板'
  if (isAdvisor.value) return '个人工作台'
  return '系统概览'
})
const taskSectionTitle = computed(() => (isManager.value ? '团队待办' : '我的任务'))
const taskEmptyText = computed(() => (isManager.value ? '暂无团队待办任务' : '暂无我的任务'))
const customerSectionTitle = computed(() => (isManager.value ? '团队客户' : '我的客户'))
const customerEmptyText = computed(() => (isManager.value ? '暂无团队客户' : '暂无我的客户'))
const advisorRankList = computed(() => roleDashboard.value?.advisorRankList || [])
const roleDashboardItems = computed(() => {
  const data = roleDashboard.value
  if (!data) return []
  if (isAdmin.value) {
    return [
      { label: '总览入口', value: data.totalOverviewCount || 0 },
      { label: '待配置', value: data.pendingConfigCount || 0 }
    ]
  }
  if (isManager.value) {
    return [
      { label: '团队客户', value: data.teamCustomerCount },
      { label: '团队待办', value: data.teamPendingTaskCount },
      { label: '团队逾期', value: data.teamOverdueTaskCount },
      { label: '高意向', value: data.teamHighIntentCustomerCount }
    ]
  }
  return [
    { label: '我的客户', value: data.customerCount },
    { label: '跟进中', value: data.followingCount },
    { label: '已成交', value: data.dealCount },
    { label: '我的任务', value: data.pendingTaskCount }
  ]
})

function currentUserId() {
  loadUser()
  return userInfo.value?.id
}

async function fetchData() {
  const userId = currentUserId()
  if (!userId) return
  try {
    console.info('[home] load role dashboard', { userId, role: userRole.value })
    if (isAdmin.value) {
      overview.value = {
        todayTaskCount: 0,
        overdueTaskCount: 0,
        highIntentCustomerCount: 0,
        newCustomerCount: 0
      }
      roleDashboard.value = null
      todayTasks.value = []
      customerPreviewList.value = []
      return
    }
    const [data, dashboard, customerData] = await Promise.all([
      dashboardApi.workbench({ userId }),
      isManager.value
        ? dashboardApi.manager({ managerId: userId }).catch(() => null)
        : dashboardApi.advisor({ userId }).catch(() => null),
      customerApi.list({ pageNum: 1, pageSize: 3 }).catch(() => ({ list: [] }))
    ])
    overview.value = data || overview.value
    roleDashboard.value = dashboard
    todayTasks.value = (data?.todayTasks || []).map(mapTask)
    customerPreviewList.value = (customerData?.list || []).map(mapCustomer)
  } finally {
    uni.stopPullDownRefresh()
  }
}

function goCustomerList() { uni.switchTab({ url: '/pages/customer/list' }) }
function goTaskList() { uni.switchTab({ url: '/pages/task/list' }) }
function showAdminTip() {
  uni.showToast({ title: '管理员请使用后台管理端', icon: 'none' })
}
function scrollToDashboard() {
  uni.pageScrollTo({ selector: '.role-dashboard', duration: 180 })
}
function goCustomerDetail(c) {
  if (!c?.id) {
    if (c?.type || c?.target || c?.currentTarget) {
      console.warn('[home] native tap event ignored', c)
      return
    }
    console.warn('[home] navigate customer detail blocked, missing customer id', c)
    uni.showToast({ title: '客户ID缺失，无法查看详情', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages/customer/detail?id=${c.id}` })
}
function goTaskDetail(t) {
  if (!t?.id) {
    if (t?.type || t?.target || t?.currentTarget) {
      console.warn('[home] native task tap event ignored', t)
      return
    }
    console.warn('[home] navigate task detail blocked, missing task id', t)
    uni.showToast({ title: '任务ID缺失，无法查看详情', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages/task/detail?id=${t.id}` })
}

function completeTask(task) {
  uni.showModal({
    title: '完成任务',
    content: `确认完成任务“${task.title}”？`,
    success: async (res) => {
      if (!res.confirm) return
      await taskApi.complete(task.id)
      uni.showToast({ title: '已完成', icon: 'success' })
      fetchData()
    }
  })
}

onMounted(fetchData)
onPullDownRefresh(fetchData)
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

.admin-entry {
  margin: 24rpx 24rpx 0;
  padding: 28rpx;
  border: 1rpx solid #dbe4f0;
  border-radius: 12rpx;
  background: #ffffff;
}

.admin-title {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
  color: #111827;
}

.admin-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #6b7280;
}

.section {
  padding: 32rpx 16rpx 0;

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
  grid-template-columns: repeat(4, 1fr);
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
        font-size: 32rpx;
        font-weight: 800;
        color: #1f2937;
      }
    }

    .quick-label {
      font-size: 22rpx;
      color: #374151;
    }
  }
}

.mini-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12rpx;
}

.mini-stat {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 10rpx;
  padding: 18rpx 8rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 0;
}

.mini-num {
  font-size: 34rpx;
  font-weight: 800;
  color: #111827;
  line-height: 1.1;
}

.mini-label {
  margin-top: 8rpx;
  font-size: 20rpx;
  color: #6b7280;
  white-space: nowrap;
}

.rank-list {
  margin-top: 14rpx;
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 10rpx;
  overflow: hidden;
}

.rank-item {
  padding: 20rpx 24rpx;
  border-bottom: 1rpx solid #f3f4f6;

  &:last-child {
    border-bottom: none;
  }
}

.rank-name {
  display: block;
  font-size: 26rpx;
  font-weight: 800;
  color: #111827;
}

.rank-meta {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  color: #6b7280;
}
</style>
