<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">客户列表</h1>
      <p class="page-desc">管理全部客户信息，支持客户分配、状态流转和详情追踪</p>
    </div>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item>
          <el-input v-model="filters.keyword" placeholder="客户姓名或手机号" clearable style="width:200px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.status" placeholder="客户状态" clearable style="width:120px">
            <el-option v-for="s in CUSTOMER_STATUS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.deptId" placeholder="所属部门" clearable style="width:120px">
            <el-option v-for="d in flatDepts" :key="d.id" :label="d.deptName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="热度">
          <el-input v-model.number="filters.minHeatScore" placeholder="最低" style="width:64px" />
          <span style="margin:0 4px; color:var(--text-400)">~</span>
          <el-input v-model.number="filters.maxHeatScore" placeholder="最高" style="width:64px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div style="margin-top:8px; font-size:12px; color:var(--text-400)">
        意向等级、客户来源筛选待后端 CustomerQueryDTO 扩展后接入
      </div>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title">客户列表</span>
        <span class="table-count">共 {{ total }} 条</span>
        <div class="toolbar-actions">
          <el-button size="small" disabled title="导出接口待接入">↓ 导出</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" row-key="id">
        <el-table-column label="客户姓名" min-width="130" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-primary cell-ellipsis">{{ row.customerName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="155">
          <template #default="{ row }"><span class="cell-mono">{{ row.mobile }}</span></template>
        </el-table-column>
        <el-table-column label="客户状态" width="115">
          <template #default="{ row }">
            <span :class="['badge', getBadge(CUSTOMER_STATUS, row.status)]">{{ getLabel(CUSTOMER_STATUS, row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="意向等级" width="115">
          <template #default="{ row }">
            <span :class="['badge', getBadge(INTENT_LEVEL, row.intentLevel)]">{{ getLabel(INTENT_LEVEL, row.intentLevel) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="热度分" width="155">
          <template #default="{ row }">
            <div class="heat-bar">
              <div class="heat-dots">
                <span v-for="i in 5" :key="i" :class="['heat-dot', i <= heatLevel(row.heatScore) ? 'fill' : 'empty']"></span>
              </div>
              <span class="heat-score">{{ row.heatScore }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="advisorName" label="所属顾问" width="115" />
        <el-table-column label="下次跟进" width="145">
          <template #default="{ row }">
            <span :style="nextFollowStyle(row.nextFollowTime)">{{ formatDate(row.nextFollowTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDetail(row)">详情</el-button>
            <el-button size="small" link type="warning" @click="openAssign(row)">分配</el-button>
            <el-button size="small" link type="warning" @click="openStatus(row)">状态修改</el-button>
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

    <el-drawer v-model="drawerVisible" title="客户详情" size="620px" direction="rtl">
      <div v-loading="detailLoading">
        <div v-if="currentCustomer" class="drawer-head">
          <div class="recent-avatar" style="width:44px; height:44px; font-size:16px">{{ currentCustomer.customerName?.[0] }}</div>
          <div class="drawer-title">
            <div class="name">{{ currentCustomer.customerName }}</div>
            <div class="mobile">{{ currentCustomer.mobile }}</div>
            <div class="badges">
              <span :class="['badge', getBadge(CUSTOMER_STATUS, currentCustomer.status)]">{{ getLabel(CUSTOMER_STATUS, currentCustomer.status) }}</span>
              <span :class="['badge', getBadge(INTENT_LEVEL, currentCustomer.intentLevel)]">{{ getLabel(INTENT_LEVEL, currentCustomer.intentLevel) }}</span>
            </div>
          </div>
          <div class="drawer-actions">
            <el-button size="small" type="primary" @click="openAssign(currentCustomer)">分配</el-button>
            <el-button size="small" @click="openStatus(currentCustomer)">改状态</el-button>
          </div>
        </div>

        <el-tabs v-model="detailTab">
          <el-tab-pane label="基础信息" name="base">
            <div v-for="row in detailRows" :key="row.label" class="detail-row">
              <span class="detail-label">{{ row.label }}</span>
              <span class="detail-value">{{ row.value || '—' }}</span>
            </div>
            <div class="tag-line" v-if="currentCustomer?.tagNames?.length">
              <el-tag v-for="tag in currentCustomer.tagNames" :key="tag" size="small" effect="plain">{{ tag }}</el-tag>
            </div>
          </el-tab-pane>
          <el-tab-pane label="跟进记录" name="follows">
            <el-timeline v-if="followLogs.length">
              <el-timeline-item v-for="item in followLogs" :key="item.id" :timestamp="formatDate(item.createdAt)">
                <div class="timeline-title">{{ item.userName || '—' }} · {{ item.followType || '跟进' }}</div>
                <div class="timeline-content">{{ item.content || item.summary || '暂无内容' }}</div>
                <div class="timeline-meta" v-if="item.nextFollowTime">下次跟进：{{ formatDate(item.nextFollowTime) }}</div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无跟进记录" />
          </el-tab-pane>
          <el-tab-pane label="分配记录" name="assign">
            <el-table :data="assignLogs" size="small">
              <el-table-column label="类型" prop="actionType" width="95" />
              <el-table-column label="原负责人" prop="fromUserId" width="95" />
              <el-table-column label="新负责人" prop="toUserId" width="95" />
              <el-table-column label="备注" prop="remark" min-width="140" show-overflow-tooltip />
              <el-table-column label="时间" width="145">
                <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>

    <el-dialog v-model="assignDialog.visible" title="客户分配" width="460px">
      <el-form :model="assignForm" label-width="90px">
        <el-form-item label="客户">
          <el-input :model-value="assignDialog.customer?.customerName" disabled />
        </el-form-item>
        <el-form-item label="分配顾问">
          <el-select v-model="assignForm.toUserId" filterable clearable placeholder="选择新的负责人" style="width:100%">
            <el-option v-for="u in users" :key="u.id" :label="displayUser(u)" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" :rows="3" maxlength="120" show-word-limit placeholder="说明分配原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="assignDialog.saving" @click="submitAssign">确认分配</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statusDialog.visible" title="客户状态流转" width="460px">
      <el-form :model="statusForm" label-width="90px">
        <el-form-item label="客户">
          <el-input :model-value="statusDialog.customer?.customerName" disabled />
        </el-form-item>
        <el-form-item label="客户状态">
          <el-select v-model="statusForm.status" placeholder="选择状态" style="width:100%">
            <el-option v-for="s in CUSTOMER_STATUS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="statusForm.remark" type="textarea" :rows="3" maxlength="120" show-word-limit placeholder="说明流转原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="statusDialog.saving" @click="submitStatus">保存状态</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { customerApi } from '@/api/customer'
import { deptApi } from '@/api/dept'
import { userApi } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { CUSTOMER_STATUS, INTENT_LEVEL, getBadgeClass, getLabel } from '@/constants/dictionary'
import { formatDate as formatDateTime, formatAmount } from '@/utils/format'
import { cleanPayload } from '@/utils/payload'
import { logBusiness, errorBusiness } from '@/utils/logger'

const getBadge = getBadgeClass
const authStore = useAuthStore()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const flatDepts = ref([])
const users = ref([])

const drawerVisible = ref(false)
const detailLoading = ref(false)
const detailTab = ref('base')
const currentCustomer = ref(null)
const followLogs = ref([])
const assignLogs = ref([])

const filters = reactive({
  keyword: '', status: '', deptId: null, minHeatScore: null, maxHeatScore: null,
})
const assignDialog = reactive({ visible: false, saving: false, customer: null })
const assignForm = reactive({ toUserId: null, remark: '' })
const statusDialog = reactive({ visible: false, saving: false, customer: null })
const statusForm = reactive({ status: '', remark: '' })

const operatorId = computed(() => authStore.userInfo?.id || authStore.userInfo?.userId || null)

const detailRows = computed(() => {
  const c = currentCustomer.value
  if (!c) return []
  return [
    { label: '客户来源', value: c.source },
    { label: '性别年龄', value: [c.gender, c.age ? `${c.age}岁` : ''].filter(Boolean).join(' / ') },
    { label: '热度分', value: c.heatScore },
    { label: '预算范围', value: c.budgetMin != null ? `${formatAmount(c.budgetMin)} ~ ${formatAmount(c.budgetMax)}` : null },
    { label: '关注区域', value: c.region },
    { label: '户型需求', value: c.houseType },
    { label: '购房用途', value: c.purpose },
    { label: '所属顾问', value: c.advisorName },
    { label: '销售经理', value: c.managerName },
    { label: '最近跟进', value: formatDate(c.latestFollowTime) },
    { label: '下次跟进', value: formatDate(c.nextFollowTime) },
    { label: '备注', value: c.remark },
  ]
})

function heatLevel(score) {
  if (!score) return 0
  return Math.min(5, Math.round(score / 20))
}
function formatDate(t) {
  return formatDateTime(t)
}
function nextFollowStyle(t) {
  if (!t) return {}
  const d = dayjs(t)
  if (d.isBefore(dayjs(), 'day')) return { color: 'var(--danger)', fontWeight: 500 }
  if (d.isSame(dayjs(), 'day')) return { color: 'var(--warning)', fontWeight: 500 }
  return { color: 'var(--text-500)', fontSize: '12px' }
}
function displayUser(user) {
  return `${user.nickname || user.username}${user.deptName ? ` / ${user.deptName}` : ''}`
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNum: currentPage.value, pageSize: pageSize.value, ...filters }
    const data = await customerApi.page(params)
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

async function fetchDepts() {
  try {
    const tree = await deptApi.tree()
    flatDepts.value = flattenTree(tree || [])
  } catch { /* non-critical */ }
}

async function fetchUsers() {
  try {
    const data = await userApi.page({ pageNum: 1, pageSize: 200, status: 1 })
    users.value = data?.list || []
  } catch { /* non-critical */ }
}

function flattenTree(nodes) {
  return nodes.reduce((acc, n) => {
    acc.push(n)
    if (n.children?.length) acc.push(...flattenTree(n.children))
    return acc
  }, [])
}

function handleSearch() { currentPage.value = 1; fetchData() }
function handleReset() {
  Object.assign(filters, { keyword: '', status: '', deptId: null, minHeatScore: null, maxHeatScore: null })
  handleSearch()
}

async function openDetail(row) {
  currentCustomer.value = row
  drawerVisible.value = true
  detailTab.value = 'base'
  detailLoading.value = true
  try {
    // 详情聚合复用 app 端成熟接口，避免后台列表字段不足。
    const [detail, follows, logs] = await Promise.all([
      customerApi.detail(row.id),
      customerApi.follows(row.id).catch(() => []),
      customerApi.assignLogsByCustomer(row.id, { pageNum: 1, pageSize: 20 }).catch(() => ({ list: [] })),
    ])
    currentCustomer.value = detail || row
    followLogs.value = follows || []
    assignLogs.value = logs?.list || []
  } finally {
    detailLoading.value = false
  }
}

function openAssign(row) {
  assignDialog.customer = row
  assignDialog.visible = true
  Object.assign(assignForm, { toUserId: row.advisorId || null, remark: '' })
  if (!users.value.length) fetchUsers()
}

async function submitAssign() {
  if (!assignForm.toUserId) return ElMessage.warning('请选择分配顾问')
  assignDialog.saving = true
  try {
    const customer = assignDialog.customer
    const payload = cleanPayload({
      toUserId: assignForm.toUserId,
      managerId: customer?.managerId,
      deptId: customer?.deptId,
      actionType: 'ASSIGN',
      remark: assignForm.remark,
      createdBy: operatorId.value,
    }, ['toUserId', 'managerId', 'deptId', 'actionType', 'remark', 'createdBy'])
    await customerApi.assign(customer.id, payload)
    logBusiness('customer', 'assign:save', payload)
    ElMessage.success('客户分配成功')
    assignDialog.visible = false
    await fetchData()
    if (drawerVisible.value && currentCustomer.value?.id === customer.id) await openDetail(customer)
  } catch (error) {
    errorBusiness('customer', 'assign:failed', error)
  } finally {
    assignDialog.saving = false
  }
}

function openStatus(row) {
  statusDialog.customer = row
  statusDialog.visible = true
  Object.assign(statusForm, { status: row.status || '', remark: '' })
}

async function submitStatus() {
  if (!statusForm.status) return ElMessage.warning('请选择客户状态')
  statusDialog.saving = true
  try {
    const customer = statusDialog.customer
    const payload = cleanPayload({
      status: statusForm.status,
      remark: statusForm.remark,
      operatorId: operatorId.value,
    }, ['status', 'remark', 'operatorId'])
    await customerApi.updateStatus(customer.id, payload)
    logBusiness('customer', 'status:save', payload)
    ElMessage.success('客户状态已更新')
    statusDialog.visible = false
    await fetchData()
    if (drawerVisible.value && currentCustomer.value?.id === customer.id) await openDetail(customer)
  } catch (error) {
    errorBusiness('customer', 'status:failed', error)
  } finally {
    statusDialog.saving = false
  }
}

onMounted(() => { fetchData(); fetchDepts(); fetchUsers() })
</script>

<style scoped>
.drawer-head {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-light);
}
.drawer-title { flex: 1; min-width: 0; }
.drawer-title .name { font-size: 16px; font-weight: 600; color: var(--text-900); }
.drawer-title .mobile { font-size: 12px; color: var(--text-400); margin-top: 2px; }
.drawer-title .badges { display: flex; gap: 6px; margin-top: 6px; }
.drawer-actions { display: flex; gap: 8px; }
.tag-line { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 14px; }
.timeline-title { font-size: 13px; font-weight: 600; color: var(--text-700); }
.timeline-content { margin-top: 4px; color: var(--text-700); white-space: pre-wrap; }
.timeline-meta { margin-top: 4px; font-size: 12px; color: var(--text-400); }
</style>
