<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">数据概览</h1>
      <p class="page-desc">今日概况 · {{ today }}</p>
    </div>

    <!-- Stat cards -->
    <div class="stat-grid" v-loading="loading">
      <div class="stat-card gold">
        <div class="stat-icon">👥</div>
        <div class="stat-value">{{ overview.totalCustomerCount ?? '—' }}</div>
        <div class="stat-label">客户总数</div>
        <div class="stat-change up">↑ 今日新增 {{ overview.newCustomerCount ?? 0 }}</div>
      </div>
      <div class="stat-card blue">
        <div class="stat-icon">⭐</div>
        <div class="stat-value">{{ overview.highIntentCustomerCount ?? '—' }}</div>
        <div class="stat-label">高意向客户</div>
        <div class="stat-change up">↑ 成交 {{ overview.dealCustomerCount ?? 0 }}</div>
      </div>
      <div class="stat-card green">
        <div class="stat-icon">☑</div>
        <div class="stat-value">{{ overview.pendingTaskCount ?? '—' }}</div>
        <div class="stat-label">待处理任务</div>
        <div class="stat-change">来自今日安排</div>
      </div>
      <div class="stat-card red">
        <div class="stat-icon">⚠</div>
        <div class="stat-value">{{ overview.overdueTaskCount ?? '—' }}</div>
        <div class="stat-label">逾期任务</div>
        <div class="stat-change down" v-if="overview.overdueTaskCount > 0">↓ 需立即处理</div>
        <div class="stat-change up" v-else>暂无逾期</div>
      </div>
    </div>

    <!-- Dashboard grid -->
    <div class="dashboard-grid">
      <!-- Recent customers -->
      <div class="d-card">
        <div class="d-card-header">
          <span class="d-card-title">最近跟进客户</span>
          <router-link to="/customer/list" class="d-card-action">查看全部 →</router-link>
        </div>
        <div class="d-card-body">
          <div v-if="!recentCustomers.length" class="empty-tip">暂无数据</div>
          <ul class="recent-list" v-else>
            <li class="recent-item" v-for="c in recentCustomers" :key="c.id">
              <div class="recent-avatar">{{ c.customerName?.[0] ?? '客' }}</div>
              <div>
                <div class="recent-name">
                  {{ c.customerName }}
                  <span :class="['badge', intentBadge(c.intentLevel)]">{{ intentLabel(c.intentLevel) }}</span>
                </div>
                <div class="recent-meta">{{ c.mobile }} · {{ c.advisorName }}</div>
              </div>
              <div class="recent-time">{{ formatTime(c.latestFollowTime) }}</div>
            </li>
          </ul>
        </div>
      </div>

      <!-- Task overview -->
      <div class="d-card">
        <div class="d-card-header">
          <span class="d-card-title">团队任务概览</span>
          <router-link to="/task/list" class="d-card-action">查看全部 →</router-link>
        </div>
        <div class="d-card-body">
          <div style="margin-bottom:14px; font-size:13px; color:var(--text-500)">
            待处理 <b style="color:var(--text-900)">{{ overview.pendingTaskCount ?? 0 }}</b> &nbsp;·&nbsp;
            逾期 <b style="color:var(--danger)">{{ overview.overdueTaskCount ?? 0 }}</b>
          </div>
          <!-- taskStats from backend -->
          <template v-if="taskStats.length">
            <div class="task-stat-row" v-for="s in taskStats" :key="s.status">
              <div style="width:52px; font-size:12.5px; color:var(--text-700)">{{ getLabel(TASK_STATUS, s.status) }}</div>
              <div class="task-stat-bar">
                <div class="task-stat-fill" :style="{ width: barWidth(s.count) + '%', background: barColor(s.status) }"></div>
              </div>
              <div style="width:28px; text-align:right; font-size:13px; font-weight:600; color:var(--text-700)">{{ s.count }}</div>
            </div>
          </template>
          <!-- TODO: dailyStats 趋势图 — 待后端补充 /api/admin/dashboard/customer-trend -->
          <div class="todo-tip" style="margin-top:16px">
            📊 客户增长趋势图 — 待接入 <code>/api/admin/dashboard/customer-trend</code>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import dayjs from 'dayjs'
import { dashboardApi } from '@/api/dashboard'
import { customerApi } from '@/api/customer'
import { TASK_STATUS, getLabel, getBadgeClass, INTENT_LEVEL } from '@/constants/dictionary'

const loading = ref(false)
const overview = ref({})
const recentCustomers = ref([])
const taskStats = ref([])

const today = dayjs().format('YYYY年M月D日 dddd')

const intentBadge = (v) => getBadgeClass(INTENT_LEVEL, v)
const intentLabel = (v) => getLabel(INTENT_LEVEL, v)

function barWidth(count) {
  const total = taskStats.value.reduce((s, i) => s + i.count, 0)
  return total ? Math.round((count / total) * 100) : 0
}
function barColor(status) {
  const map = { PENDING: '#f59e0b', DONE: '#10b981', OVERDUE: '#ef4444', DELAYED: '#94a3b8' }
  return map[status] || '#94a3b8'
}
function formatTime(t) {
  if (!t) return '—'
  return dayjs(t).fromNow ? dayjs(t).format('MM-DD HH:mm') : t
}

async function fetchDashboard() {
  loading.value = true
  try {
    const data = await dashboardApi.overview()
    overview.value = data
    taskStats.value = data.taskStats || []
  } finally {
    loading.value = false
  }
}

async function fetchRecentCustomers() {
  try {
    const data = await customerApi.page({ pageNum: 1, pageSize: 5 })
    recentCustomers.value = data?.list || []
  } catch { /* non-critical */ }
}

onMounted(() => {
  fetchDashboard()
  fetchRecentCustomers()
})
</script>

<style scoped>
.empty-tip { padding: 20px 0; text-align: center; color: var(--text-400); font-size: 13px; }
.todo-tip {
  padding: 10px 12px; background: var(--surface-100);
  border-radius: var(--radius-sm); font-size: 12px; color: var(--text-500);
}
.todo-tip code { font-size: 11px; background: var(--surface-200); padding: 1px 5px; border-radius: 3px; }
</style>
