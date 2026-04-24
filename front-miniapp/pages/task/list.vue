<template>
  <view class="page-container">
    <!-- 状态 Tab -->
    <scroll-view class="status-tabs" scroll-x>
      <view class="tab-list">
        <view
          class="tab-item"
          v-for="tab in tabs"
          :key="tab.key"
          :class="{ active: activeTab === tab.key }"
          @tap="activeTab = tab.key"
        >
          {{ tab.label }}
          <text v-if="tab.count" class="tab-badge">{{ tab.count }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 任务列表 -->
    <view class="list-wrap">
      <TaskCard
        v-for="task in filteredTasks"
        :key="task.id"
        :task="task"
        @tap="goDetail"
        @complete="completeTask"
      />
      <EmptyState v-if="!filteredTasks.length" icon="✅" :text="emptyText" />
      <view style="height: 32rpx;" />
    </view>

    <!-- 新增悬浮按钮 -->
    <view class="fab" @tap="goCreate">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import TaskCard from '../../components/TaskCard.vue'
import EmptyState from '../../components/EmptyState.vue'

const activeTab = ref('TODAY')

const allTasks = ref([
  { id: 1, title: '回访张先生购房意向', customerName: '张先生', taskType: 'CALL', status: 'PENDING', priority: 'HIGH', planTime: new Date().setHours(10, 0) },
  { id: 2, title: '安排李女士看房', customerName: '李女士', taskType: 'VISIT', status: 'PENDING', priority: 'URGENT', planTime: new Date().setHours(14, 30) },
  { id: 3, title: '跟进王先生贷款事宜', customerName: '王先生', taskType: 'FOLLOW', status: 'OVERDUE', priority: 'MEDIUM', planTime: new Date(Date.now() - 86400000) },
  { id: 4, title: '陈女士签约跟进', customerName: '陈女士', taskType: 'FOLLOW', status: 'PENDING', priority: 'HIGH', planTime: new Date().setHours(16, 0) },
  { id: 5, title: '刘先生基础跟进', customerName: '刘先生', taskType: 'FOLLOW', status: 'PENDING', priority: 'LOW', planTime: new Date(Date.now() + 86400000) },
  { id: 6, title: '新客户初次跟进', customerName: '赵女士', taskType: 'CALL', status: 'DONE', priority: 'MEDIUM', planTime: new Date(Date.now() - 172800000) }
])

const todayStart = new Date().setHours(0, 0, 0, 0)
const todayEnd = new Date().setHours(23, 59, 59, 999)

const tabs = computed(() => [
  { key: 'TODAY', label: '今日', count: allTasks.value.filter(t => t.planTime >= todayStart && t.planTime <= todayEnd && t.status !== 'DONE').length },
  { key: 'PENDING', label: '待处理', count: allTasks.value.filter(t => t.status === 'PENDING').length },
  { key: 'OVERDUE', label: '逾期', count: allTasks.value.filter(t => t.status === 'OVERDUE').length },
  { key: 'DONE', label: '已完成', count: 0 },
  { key: 'ALL', label: '全部', count: 0 }
])

const filteredTasks = computed(() => {
  switch (activeTab.value) {
    case 'TODAY': return allTasks.value.filter(t => t.planTime >= todayStart && t.planTime <= todayEnd)
    case 'PENDING': return allTasks.value.filter(t => t.status === 'PENDING')
    case 'OVERDUE': return allTasks.value.filter(t => t.status === 'OVERDUE')
    case 'DONE': return allTasks.value.filter(t => t.status === 'DONE')
    default: return allTasks.value
  }
})

const emptyText = computed(() => {
  switch (activeTab.value) {
    case 'TODAY': return '今日暂无任务'
    case 'PENDING': return '暂无待处理任务'
    case 'OVERDUE': return '没有逾期任务'
    case 'DONE': return '暂无已完成任务'
    default: return '暂无任务'
  }
})

function goDetail(task) { uni.navigateTo({ url: `/pages/task/detail?id=${task.id}` }) }
function goCreate() { uni.navigateTo({ url: '/pages/task/form' }) }

function completeTask(task) {
  uni.showModal({
    title: '完成任务',
    content: `确认完成「${task.title}」？`,
    success(res) {
      if (res.confirm) {
        task.status = 'DONE'
        uni.showToast({ title: '已完成', icon: 'success' })
      }
    }
  })
}

onPullDownRefresh(() => {
  uni.showToast({ title: '已刷新', icon: 'success' })
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.status-tabs {
  background: #ffffff;
  border-bottom: 1rpx solid #e5e7eb;
  white-space: nowrap;

  .tab-list {
    display: flex;
    padding: 0 8rpx;
  }

  .tab-item {
    display: inline-flex;
    align-items: center;
    gap: 8rpx;
    padding: 24rpx 24rpx;
    font-size: 26rpx;
    color: #6b7280;
    flex-shrink: 0;
    border-bottom: 4rpx solid transparent;
    position: relative;

    &.active {
      color: #1a56db;
      font-weight: 600;
      border-bottom-color: #1a56db;
    }

    .tab-badge {
      background: #c81e1e;
      color: #ffffff;
      font-size: 18rpx;
      padding: 2rpx 8rpx;
      border-radius: 9999rpx;
      font-weight: 700;
    }
  }
}

.list-wrap {
  padding: 16rpx 24rpx 0;
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
