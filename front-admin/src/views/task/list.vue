<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">任务列表</h1>
      <p class="page-desc">查看全团队跟进任务执行情况，支持任务转派和提醒日志追踪</p>
    </div>

    <div class="task-stat-grid" v-loading="statLoading">
      <div class="task-stat-card warning">
        <div class="task-stat-value">{{ taskStat.pendingCount ?? 0 }}</div>
        <div class="task-stat-label">待处理</div>
      </div>
      <div class="task-stat-card danger">
        <div class="task-stat-value">{{ taskStat.overdueCount ?? 0 }}</div>
        <div class="task-stat-label">已逾期</div>
      </div>
      <div class="task-stat-card success">
        <div class="task-stat-value">{{ taskStat.doneCount ?? 0 }}</div>
        <div class="task-stat-label">已完成</div>
      </div>
      <div class="task-stat-card gray">
        <div class="task-stat-value">{{ taskStat.delayedCount ?? 0 }}</div>
        <div class="task-stat-label">已延期</div>
      </div>
    </div>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item>
          <el-select v-model="filters.status" placeholder="任务状态" clearable style="width:110px">
            <el-option v-for="s in TASK_STATUS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="filters.date" type="date" placeholder="任务日期" value-format="YYYY-MM-DD" style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div style="margin-top:8px; font-size:12px; color:var(--text-400)">
        任务类型、优先级、负责人筛选待后端 TaskQueryDTO 扩展后接入
      </div>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title">任务列表</span>
        <span class="table-count">共 {{ total }} 条</span>
      </div>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column label="任务标题" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-primary cell-ellipsis">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="105">
          <template #default="{ row }">
            <span :class="['badge', getBadgeClass(TASK_TYPE, row.taskType)]">{{ getLabel(TASK_TYPE, row.taskType) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="关联客户" width="130" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="负责人" width="115" show-overflow-tooltip />
        <el-table-column label="任务日期" width="165">
          <template #default="{ row }">
            <span style="font-size:12px">{{ row.taskDate }}{{ row.taskTime ? ' ' + row.taskTime : '' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="优先级" width="115">
          <template #default="{ row }">
            <span :class="['badge', getBadgeClass(TASK_PRIORITY, row.priority)]">{{ getLabel(TASK_PRIORITY, row.priority) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="115">
          <template #default="{ row }">
            <span :class="['badge', getBadgeClass(TASK_STATUS, row.status)]">{{ getLabel(TASK_STATUS, row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="155" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openDetail(row)">详情</el-button>
            <el-button size="small" link type="primary" @click="openTransfer(row)">转派</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @change="fetchData"
        />
      </div>
    </div>

    <el-drawer v-model="drawerVisible" title="任务详情" size="620px">
      <div v-loading="detailLoading" v-if="cur" style="font-size:13px">
        <div class="task-head">
          <div>
            <div class="task-title">{{ cur.title }}</div>
            <div class="task-tags">
              <span :class="['badge', getBadgeClass(TASK_STATUS, cur.status)]">{{ getLabel(TASK_STATUS, cur.status) }}</span>
              <span :class="['badge', getBadgeClass(TASK_PRIORITY, cur.priority)]">{{ getLabel(TASK_PRIORITY, cur.priority) }}</span>
              <span :class="['badge', getBadgeClass(TASK_TYPE, cur.taskType)]">{{ getLabel(TASK_TYPE, cur.taskType) }}</span>
            </div>
          </div>
          <el-button size="small" type="primary" @click="openTransfer(cur)">转派任务</el-button>
        </div>

        <el-tabs v-model="detailTab">
          <el-tab-pane label="基础信息" name="base">
            <div class="detail-row"><span class="detail-label">关联客户</span><span class="detail-value">{{ cur.customerName || '—' }}</span></div>
            <div class="detail-row"><span class="detail-label">负责人</span><span class="detail-value">{{ cur.ownerName || '—' }}</span></div>
            <div class="detail-row"><span class="detail-label">计划时间</span><span class="detail-value">{{ cur.taskDate }} {{ cur.taskTime || '' }}</span></div>
            <div class="detail-row"><span class="detail-label">任务内容</span><span class="detail-value" style="white-space:pre-wrap">{{ cur.content || '—' }}</span></div>
            <div class="detail-row" v-if="cur.completeTime"><span class="detail-label">完成时间</span><span class="detail-value">{{ formatDate(cur.completeTime) }}</span></div>
          </el-tab-pane>
          <el-tab-pane label="提醒日志" name="reminds">
            <el-table :data="remindLogs" size="small">
              <el-table-column label="提醒类型" prop="remindType" width="95" />
              <el-table-column label="提醒时间" width="145">
                <template #default="{ row }">{{ formatDate(row.remindTime) }}</template>
              </el-table-column>
              <el-table-column label="状态" prop="status" width="90" />
              <el-table-column label="结果" prop="resultMsg" min-width="160" show-overflow-tooltip />
              <el-table-column label="创建时间" width="145">
                <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="转派记录" name="transfers">
            <el-table :data="transferLogs" size="small">
              <el-table-column label="原负责人" prop="fromUserId" width="95" />
              <el-table-column label="新负责人" prop="toUserId" width="95" />
              <el-table-column label="原因" prop="reason" min-width="180" show-overflow-tooltip />
              <el-table-column label="操作人" prop="createdBy" width="90" />
              <el-table-column label="时间" width="145">
                <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>

    <el-dialog v-model="transferDialog.visible" title="任务转派" width="460px">
      <el-form :model="transferForm" label-width="90px">
        <el-form-item label="任务">
          <el-input :model-value="transferDialog.task?.title" disabled />
        </el-form-item>
        <el-form-item label="新负责人">
          <el-select v-model="transferForm.toUserId" filterable clearable placeholder="选择新的负责人" style="width:100%">
            <el-option v-for="u in users" :key="u.id" :label="displayUser(u)" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="转派原因">
          <el-input v-model="transferForm.reason" type="textarea" :rows="3" maxlength="120" show-word-limit placeholder="说明转派原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="transferDialog.saving" @click="submitTransfer">确认转派</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { taskApi } from '@/api/task'
import { userApi } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { TASK_STATUS, TASK_PRIORITY, TASK_TYPE, getBadgeClass, getLabel } from '@/constants/dictionary'
import { formatDate as formatDateTime } from '@/utils/format'
import { cleanPayload } from '@/utils/payload'
import { logBusiness, errorBusiness } from '@/utils/logger'

const authStore = useAuthStore()
const loading = ref(false)
const statLoading = ref(false)
const detailLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const drawerVisible = ref(false)
const detailTab = ref('base')
const cur = ref(null)
const remindLogs = ref([])
const transferLogs = ref([])
const users = ref([])
const taskStat = reactive({ pendingCount: 0, overdueCount: 0, doneCount: 0, delayedCount: 0 })

const filters = reactive({ status: '', date: '' })
const transferDialog = reactive({ visible: false, saving: false, task: null })
const transferForm = reactive({ toUserId: null, reason: '' })

function formatDate(t) {
  return formatDateTime(t)
}
function displayUser(user) {
  return `${user.nickname || user.username}${user.deptName ? ` / ${user.deptName}` : ''}`
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNum: currentPage.value, pageSize: pageSize.value, ...filters }
    const data = await taskApi.page(params)
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

async function fetchStat() {
  statLoading.value = true
  try {
    Object.assign(taskStat, await taskApi.stat())
  } finally {
    statLoading.value = false
  }
}

async function fetchUsers() {
  try {
    const data = await userApi.page({ pageNum: 1, pageSize: 200, status: 1 })
    users.value = data?.list || []
  } catch { /* non-critical */ }
}

function handleSearch() { currentPage.value = 1; fetchData() }
function handleReset() { Object.assign(filters, { status: '', date: '' }); handleSearch() }

async function openDetail(row) {
  cur.value = row
  drawerVisible.value = true
  detailTab.value = 'base'
  detailLoading.value = true
  try {
    const detail = await taskApi.detail(row.id)
    cur.value = detail?.task || row
    remindLogs.value = detail?.remindLogs || []
    transferLogs.value = detail?.transferLogs || []
  } finally {
    detailLoading.value = false
  }
}

function openTransfer(row) {
  transferDialog.task = row
  transferDialog.visible = true
  Object.assign(transferForm, { toUserId: row.ownerId || null, reason: '' })
  if (!users.value.length) fetchUsers()
}

async function submitTransfer() {
  if (!transferForm.toUserId) return ElMessage.warning('请选择新负责人')
  transferDialog.saving = true
  try {
    const task = transferDialog.task
    const payload = cleanPayload({
      toUserId: transferForm.toUserId,
      reason: transferForm.reason,
      createdBy: authStore.userInfo?.id || authStore.userInfo?.userId || null,
    }, ['toUserId', 'reason', 'createdBy'])
    await taskApi.transfer(task.id, payload)
    logBusiness('task', 'transfer:save', payload)
    ElMessage.success('任务转派成功')
    transferDialog.visible = false
    await Promise.all([fetchData(), fetchStat()])
    if (drawerVisible.value && cur.value?.id === task.id) await openDetail(task)
  } catch (error) {
    errorBusiness('task', 'transfer:failed', error)
  } finally {
    transferDialog.saving = false
  }
}

onMounted(() => { fetchData(); fetchStat(); fetchUsers() })
</script>

<style scoped>
.task-stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 14px;
}
.task-stat-card {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 14px 16px;
  box-shadow: var(--shadow-sm);
}
.task-stat-card.warning { border-top: 3px solid var(--warning); }
.task-stat-card.danger { border-top: 3px solid var(--danger); }
.task-stat-card.success { border-top: 3px solid var(--success); }
.task-stat-card.gray { border-top: 3px solid var(--text-400); }
.task-stat-value { font-size: 24px; font-weight: 700; color: var(--text-900); line-height: 1; }
.task-stat-label { margin-top: 6px; font-size: 12px; color: var(--text-500); }
.task-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border-light);
}
.task-title { font-size: 16px; font-weight: 600; color: var(--text-900); }
.task-tags { display: flex; gap: 6px; margin-top: 8px; }
</style>
