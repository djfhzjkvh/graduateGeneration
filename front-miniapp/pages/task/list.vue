<template>
  <view class="page-container">
    <scroll-view class="status-tabs" scroll-x>
      <view class="tab-list">
        <view
          class="tab-item"
          v-for="tab in tabs"
          :key="tab.key"
          :class="{ active: activeTab === tab.key }"
          @tap="setTab(tab.key)"
        >
          {{ tab.label }}
        </view>
      </view>
    </scroll-view>

    <view class="list-wrap">
      <TaskCard
        v-for="task in tasks"
        :key="task.id"
        :task="task"
        @tap="goDetail"
        @complete="completeTask"
      />
      <EmptyState v-if="!tasks.length" icon="✓" :text="emptyText" />
      <view style="height: 32rpx;" />
    </view>

    <view class="fab" @tap="goCreate">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { taskApi } from '../../api/task'
import { mapTask } from '../../utils/adapters'
import TaskCard from '../../components/TaskCard.vue'
import EmptyState from '../../components/EmptyState.vue'

const activeTab = ref('TODAY')
const tasks = ref([])
const today = new Date().toISOString().slice(0, 10)

const tabs = [
  { key: 'TODAY', label: '今日' },
  { key: 'PENDING', label: '待处理' },
  { key: 'OVERDUE', label: '逾期' },
  { key: 'DONE', label: '已完成' },
  { key: 'ALL', label: '全部' }
]

const emptyText = computed(() => {
  const map = {
    TODAY: '今日暂无任务',
    PENDING: '暂无待处理任务',
    OVERDUE: '没有逾期任务',
    DONE: '暂无已完成任务',
    ALL: '暂无任务'
  }
  return map[activeTab.value]
})

function queryParams() {
  const params = { pageNum: 1, pageSize: 50 }
  if (activeTab.value === 'TODAY') params.date = today
  if (['PENDING', 'OVERDUE', 'DONE'].includes(activeTab.value)) params.status = activeTab.value
  return params
}

async function fetchTasks() {
  try {
    const data = await taskApi.list(queryParams())
    tasks.value = (data?.list || []).map(mapTask)
  } finally {
    uni.stopPullDownRefresh()
  }
}

function setTab(key) {
  activeTab.value = key
  fetchTasks()
}

function goDetail(task) { uni.navigateTo({ url: `/pages/task/detail?id=${task.id}` }) }
function goCreate() { uni.navigateTo({ url: '/pages/task/form' }) }

function completeTask(task) {
  uni.showModal({
    title: '完成任务',
    content: `确认完成“${task.title}”？`,
    success: async (res) => {
      if (!res.confirm) return
      await taskApi.complete(task.id)
      uni.showToast({ title: '已完成', icon: 'success' })
      fetchTasks()
    }
  })
}

onMounted(fetchTasks)
onPullDownRefresh(fetchTasks)
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
    padding: 24rpx;
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
  padding: 16rpx 16rpx 0;
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
