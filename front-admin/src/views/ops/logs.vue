<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">日志中心</h1>
      <p class="page-desc">追踪 AI 调用和关键业务操作，辅助排查异常与审计问题</p>
    </div>

    <el-tabs v-model="activeTab" class="ops-tabs">
      <el-tab-pane label="AI 调用日志" name="ai">
        <div class="filter-card">
          <el-form :model="aiFilters" inline>
            <el-form-item>
              <el-input v-model="aiFilters.bizType" placeholder="业务类型" clearable style="width: 130px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="aiFilters.status" placeholder="状态" clearable style="width: 110px">
                <el-option label="成功" value="SUCCESS" />
                <el-option label="失败" value="FAILED" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="aiDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 260px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="fetchAiLogs">查询</el-button>
              <el-button @click="resetAiFilters">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="table-card">
          <div class="table-toolbar">
            <span class="table-title">AI 调用日志</span>
            <span class="table-count">共 {{ aiTotal }} 条</span>
          </div>
          <el-table :data="aiLogs" v-loading="aiLoading" row-key="id">
            <el-table-column prop="bizType" label="业务类型" width="125" show-overflow-tooltip />
            <el-table-column prop="bizId" label="业务 ID" width="105" />
            <el-table-column prop="modelName" label="模型" min-width="150" show-overflow-tooltip />
            <el-table-column prop="tokenUsage" label="Token" width="95" />
            <el-table-column label="状态" width="95">
              <template #default="{ row }">
                <span :class="['badge', statusBadge(row.status)]">{{ row.status || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="错误信息" min-width="190" show-overflow-tooltip>
              <template #default="{ row }">{{ row.errorMsg || '-' }}</template>
            </el-table-column>
            <el-table-column label="调用时间" width="165">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-button size="small" link @click="openDetail('ai', row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="table-pagination">
            <el-pagination
              v-model:current-page="aiPage"
              v-model:page-size="aiPageSize"
              :page-sizes="[10, 20, 50]"
              :total="aiTotal"
              layout="total, sizes, prev, pager, next"
              @change="fetchAiLogs"
            />
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="操作日志" name="operation">
        <div class="filter-card">
          <el-form :model="operFilters" inline>
            <el-form-item>
              <el-input v-model="operFilters.bizType" placeholder="业务类型" clearable style="width: 130px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="operFilters.action" placeholder="操作动作" clearable style="width: 130px" />
            </el-form-item>
            <el-form-item>
              <el-input-number v-model="operFilters.userId" :min="1" placeholder="用户 ID" controls-position="right" style="width: 130px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="operDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 260px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="fetchOperLogs">查询</el-button>
              <el-button @click="resetOperFilters">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="table-card">
          <div class="table-toolbar">
            <span class="table-title">操作日志</span>
            <span class="table-count">共 {{ operTotal }} 条</span>
          </div>
          <el-table :data="operLogs" v-loading="operLoading" row-key="id">
            <el-table-column label="用户" min-width="140" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="cell-primary">{{ row.nickname || row.username || row.userId || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="bizType" label="业务类型" width="120" show-overflow-tooltip />
            <el-table-column prop="bizId" label="业务 ID" width="95" />
            <el-table-column prop="action" label="动作" width="120" show-overflow-tooltip />
            <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
            <el-table-column prop="ip" label="IP" width="135" />
            <el-table-column label="时间" width="165">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-button size="small" link @click="openDetail('operation', row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="table-pagination">
            <el-pagination
              v-model:current-page="operPage"
              v-model:page-size="operPageSize"
              :page-sizes="[10, 20, 50]"
              :total="operTotal"
              layout="total, sizes, prev, pager, next"
              @change="fetchOperLogs"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-drawer v-model="detailVisible" title="日志详情" size="460px" direction="rtl">
      <div v-if="currentLog" class="log-detail">
        <div v-for="item in detailRows" :key="item.label" class="detail-row">
          <span class="detail-label">{{ item.label }}</span>
          <span class="detail-value">{{ item.value || '-' }}</span>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { opsApi } from '@/api/ops'
import { formatDate } from '@/utils/format'
import { logBusiness } from '@/utils/logger'

const activeTab = ref('ai')
const aiLoading = ref(false)
const operLoading = ref(false)
const aiLogs = ref([])
const operLogs = ref([])
const aiTotal = ref(0)
const operTotal = ref(0)
const aiPage = ref(1)
const aiPageSize = ref(10)
const operPage = ref(1)
const operPageSize = ref(10)
const detailVisible = ref(false)
const detailType = ref('ai')
const currentLog = ref(null)
const aiDateRange = ref([])
const operDateRange = ref([])

const aiFilters = reactive({ bizType: '', status: '', dateStart: '', dateEnd: '' })
const operFilters = reactive({ userId: null, bizType: '', action: '', dateStart: '', dateEnd: '' })

const detailRows = computed(() => {
  const row = currentLog.value
  if (!row) return []
  if (detailType.value === 'ai') {
    return [
      { label: '业务类型', value: row.bizType },
      { label: '业务 ID', value: row.bizId },
      { label: '模型', value: row.modelName },
      { label: 'Token', value: row.tokenUsage },
      { label: '状态', value: row.status },
      { label: '错误信息', value: row.errorMsg },
      { label: '调用时间', value: formatDate(row.createdAt) },
    ]
  }
  return [
    { label: '用户 ID', value: row.userId },
    { label: '用户', value: row.nickname || row.username },
    { label: '业务类型', value: row.bizType },
    { label: '业务 ID', value: row.bizId },
    { label: '动作', value: row.action },
    { label: '内容', value: row.content },
    { label: 'IP', value: row.ip },
    { label: '时间', value: formatDate(row.createdAt) },
  ]
})

watch(aiDateRange, (range) => {
  aiFilters.dateStart = range?.[0] || ''
  aiFilters.dateEnd = range?.[1] || ''
})

watch(operDateRange, (range) => {
  operFilters.dateStart = range?.[0] || ''
  operFilters.dateEnd = range?.[1] || ''
})

function logStep(action, payload = {}) {
  logBusiness('ops-logs', action, payload)
}

function statusBadge(status) {
  if (status === 'SUCCESS') return 'badge-success'
  if (status === 'FAILED' || status === 'FAIL') return 'badge-danger'
  return 'badge-gray'
}

function buildParams(filters, pageNum, pageSize) {
  const params = { pageNum, pageSize }
  Object.keys(filters).forEach((key) => {
    if (filters[key] !== '' && filters[key] !== null && filters[key] !== undefined) {
      params[key] = filters[key]
    }
  })
  return params
}

async function fetchAiLogs() {
  aiLoading.value = true
  const params = buildParams(aiFilters, aiPage.value, aiPageSize.value)
  try {
    const data = await opsApi.aiLogs(params)
    aiLogs.value = data?.list || []
    aiTotal.value = data?.total || 0
  } finally {
    aiLoading.value = false
  }
}

async function fetchOperLogs() {
  operLoading.value = true
  const params = buildParams(operFilters, operPage.value, operPageSize.value)
  try {
    const data = await opsApi.operationLogs(params)
    operLogs.value = data?.list || []
    operTotal.value = data?.total || 0
  } finally {
    operLoading.value = false
  }
}

function resetAiFilters() {
  aiDateRange.value = []
  Object.assign(aiFilters, { bizType: '', status: '', dateStart: '', dateEnd: '' })
  aiPage.value = 1
  fetchAiLogs()
}

function resetOperFilters() {
  operDateRange.value = []
  Object.assign(operFilters, { userId: null, bizType: '', action: '', dateStart: '', dateEnd: '' })
  operPage.value = 1
  fetchOperLogs()
}

function openDetail(type, row) {
  detailType.value = type
  currentLog.value = row
  detailVisible.value = true
}

onMounted(() => {
  fetchAiLogs()
  fetchOperLogs()
})
</script>

<style scoped>
.ops-tabs :deep(.el-tabs__header) {
  margin-bottom: 14px;
}

.log-detail {
  font-size: 13px;
}

.detail-value {
  line-height: 1.7;
  word-break: break-word;
}
</style>
