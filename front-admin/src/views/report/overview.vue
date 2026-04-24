<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">经营报表</h1>
      <p class="page-desc">查看销售日报、客户转化和来源质量，辅助判断团队经营状态</p>
    </div>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item label="日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.deptId" placeholder="部门" clearable style="width: 150px">
            <el-option v-for="dept in flatDepts" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchReports">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="stat-grid" v-loading="loading">
      <div class="stat-card gold">
        <div class="stat-icon">新</div>
        <div class="stat-value">{{ totals.newCustomerCount }}</div>
        <div class="stat-label">新增客户</div>
        <div class="stat-change up">高意向 {{ totals.highIntentCount }}</div>
      </div>
      <div class="stat-card blue">
        <div class="stat-icon">转</div>
        <div class="stat-value">{{ percent(conversion.visitRate) }}</div>
        <div class="stat-label">到访率</div>
        <div class="stat-change">到访 {{ conversion.visitedCustomerCount || 0 }} / {{ conversion.totalCustomerCount || 0 }}</div>
      </div>
      <div class="stat-card green">
        <div class="stat-icon">成</div>
        <div class="stat-value">{{ percent(conversion.dealRate) }}</div>
        <div class="stat-label">成交率</div>
        <div class="stat-change up">成交 {{ conversion.dealCustomerCount || 0 }}</div>
      </div>
      <div class="stat-card red">
        <div class="stat-icon">失</div>
        <div class="stat-value">{{ percent(conversion.lostRate) }}</div>
        <div class="stat-label">流失率</div>
        <div class="stat-change down">流失 {{ conversion.lostCustomerCount || 0 }}</div>
      </div>
    </div>

    <div class="report-grid">
      <div class="table-card">
        <div class="table-toolbar">
          <span class="table-title">销售日报</span>
          <span class="table-count">{{ dailyReports.length }} 天</span>
        </div>
        <el-table :data="dailyReports" v-loading="loading" row-key="statDate">
          <el-table-column prop="statDate" label="日期" width="120" />
          <el-table-column prop="newCustomerCount" label="新增客户" width="105" />
          <el-table-column prop="highIntentCount" label="高意向" width="95" />
          <el-table-column prop="pendingTaskCount" label="待处理任务" width="115" />
          <el-table-column label="预计金额" min-width="130">
            <template #default="{ row }">{{ money(row.expectedAmount) }}</template>
          </el-table-column>
          <el-table-column prop="forgetTaskCount" label="遗忘任务" width="100" />
        </el-table>
      </div>

      <div class="table-card">
        <div class="table-toolbar">
          <span class="table-title">客户来源分析</span>
          <span class="table-count">{{ sourceReports.length }} 个来源</span>
        </div>
        <el-table :data="sourceReports" v-loading="loading" row-key="source">
          <el-table-column label="来源" min-width="120">
            <template #default="{ row }">
              <span class="cell-primary">{{ sourceLabel(row.source) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="customerCount" label="客户数" width="90" />
          <el-table-column prop="dealCustomerCount" label="成交数" width="90" />
          <el-table-column label="成交率" width="120">
            <template #default="{ row }">
              <span :class="['badge', sourceBadge(row.dealRate)]">{{ percent(row.dealRate) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <div class="table-card conversion-card">
      <div class="table-toolbar">
        <span class="table-title">转化漏斗</span>
        <span class="table-count">客户总数 {{ conversion.totalCustomerCount || 0 }}</span>
      </div>
      <div class="funnel-body" v-loading="loading">
        <div class="funnel-row">
          <span class="funnel-label">总客户</span>
          <div class="funnel-bar"><div class="funnel-fill gold" :style="{ width: '100%' }"></div></div>
          <span class="funnel-value">{{ conversion.totalCustomerCount || 0 }}</span>
        </div>
        <div class="funnel-row">
          <span class="funnel-label">已到访</span>
          <div class="funnel-bar"><div class="funnel-fill blue" :style="{ width: funnelWidth(conversion.visitedCustomerCount) }"></div></div>
          <span class="funnel-value">{{ conversion.visitedCustomerCount || 0 }}</span>
        </div>
        <div class="funnel-row">
          <span class="funnel-label">已成交</span>
          <div class="funnel-bar"><div class="funnel-fill green" :style="{ width: funnelWidth(conversion.dealCustomerCount) }"></div></div>
          <span class="funnel-value">{{ conversion.dealCustomerCount || 0 }}</span>
        </div>
        <div class="funnel-row">
          <span class="funnel-label">已流失</span>
          <div class="funnel-bar"><div class="funnel-fill red" :style="{ width: funnelWidth(conversion.lostCustomerCount) }"></div></div>
          <span class="funnel-value">{{ conversion.lostCustomerCount || 0 }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import dayjs from 'dayjs'
import { reportApi } from '@/api/report'
import { deptApi } from '@/api/dept'
import { formatAmount, formatPercent } from '@/utils/format'
import { logBusiness, warnBusiness } from '@/utils/logger'

const loading = ref(false)
const dailyReports = ref([])
const sourceReports = ref([])
const conversion = ref({})
const flatDepts = ref([])
const dateRange = ref([dayjs().subtract(6, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')])
const filters = reactive({ dateStart: dateRange.value[0], dateEnd: dateRange.value[1], deptId: null })

const totals = computed(() => dailyReports.value.reduce((sum, row) => ({
  newCustomerCount: sum.newCustomerCount + (row.newCustomerCount || 0),
  highIntentCount: sum.highIntentCount + (row.highIntentCount || 0),
}), { newCustomerCount: 0, highIntentCount: 0 }))

watch(dateRange, (range) => {
  filters.dateStart = range?.[0] || ''
  filters.dateEnd = range?.[1] || ''
})

function logStep(action, payload = {}) {
  logBusiness('reports', action, payload)
}

function flattenTree(nodes) {
  return nodes.reduce((acc, node) => {
    acc.push(node)
    if (node.children?.length) acc.push(...flattenTree(node.children))
    return acc
  }, [])
}

function percent(value) {
  return formatPercent(value)
}

function money(value) {
  return formatAmount(value)
}

function sourceLabel(source) {
  const map = {
    WECHAT: '微信',
    PHONE: '电话',
    VISIT: '到访',
    REFERRAL: '转介绍',
    IMPORT: '导入',
  }
  return map[source] || source || '未知'
}

function sourceBadge(rate) {
  const value = Number(rate || 0)
  if (value >= 0.3) return 'badge-success'
  if (value >= 0.1) return 'badge-gold'
  return 'badge-gray'
}

function funnelWidth(count) {
  const total = conversion.value.totalCustomerCount || 0
  if (!total) return '0%'
  return `${Math.min(100, Math.round((Number(count || 0) / total) * 100))}%`
}

async function fetchDepts() {
  try {
    const tree = await deptApi.tree()
    flatDepts.value = flattenTree(tree || [])
  } catch (error) {
    warnBusiness('reports', 'dept:failed', error)
  }
}

async function fetchReports() {
  loading.value = true
  const params = { ...filters }
  try {
    const [daily, conversionData, source] = await Promise.all([
      reportApi.daily(params),
      reportApi.conversion(params),
      reportApi.source(params),
    ])
    dailyReports.value = daily || []
    conversion.value = conversionData || {}
    sourceReports.value = source || []
  } finally {
    loading.value = false
  }
}

function handleReset() {
  dateRange.value = [dayjs().subtract(6, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
  Object.assign(filters, { dateStart: dateRange.value[0], dateEnd: dateRange.value[1], deptId: null })
  fetchReports()
}

onMounted(() => {
  fetchDepts()
  fetchReports()
})
</script>

<style scoped>
.report-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(360px, .65fr);
  gap: 14px;
  margin-bottom: 14px;
}

.conversion-card {
  margin-bottom: 14px;
}

.funnel-body {
  padding: 18px;
  display: grid;
  gap: 14px;
}

.funnel-row {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr) 70px;
  align-items: center;
  gap: 12px;
}

.funnel-label {
  color: var(--text-500);
  font-size: 12px;
}

.funnel-value {
  color: var(--text-900);
  font-size: 13px;
  font-weight: 600;
  text-align: right;
}

.funnel-bar {
  height: 8px;
  background: var(--surface-100);
  border-radius: 99px;
  overflow: hidden;
}

.funnel-fill {
  height: 100%;
  min-width: 4px;
  border-radius: 99px;
}

.funnel-fill.gold { background: linear-gradient(90deg, var(--gold-500), var(--gold-300)); }
.funnel-fill.blue { background: linear-gradient(90deg, #3b82f6, #60a5fa); }
.funnel-fill.green { background: linear-gradient(90deg, #10b981, #34d399); }
.funnel-fill.red { background: linear-gradient(90deg, #ef4444, #f87171); }

@media (max-width: 1200px) {
  .report-grid {
    grid-template-columns: 1fr;
  }
}
</style>
