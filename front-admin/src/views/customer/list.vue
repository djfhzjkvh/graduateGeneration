<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">客户列表</h1>
      <p class="page-desc">管理全部客户信息，支持多条件筛选与查询</p>
    </div>

    <!-- Filter -->
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
        <!-- 热度区间 -->
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
        <!-- TODO: intentLevel / source 筛选 — 后端 CustomerQueryDTO 待扩展 -->
        意向等级、客户来源筛选待后端 CustomerQueryDTO 扩展后接入
      </div>
    </div>

    <!-- Table -->
    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title">客户列表</span>
        <span class="table-count">共 {{ total }} 条</span>
        <div class="toolbar-actions">
          <!-- TODO: 导出接口后端待补充 -->
          <el-button size="small" disabled title="导出接口待接入">↓ 导出</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" row-key="id">
        <el-table-column label="客户姓名" min-width="100">
          <template #default="{ row }">
            <span class="cell-primary">{{ row.customerName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="140">
          <template #default="{ row }">
            <span class="cell-mono">{{ row.mobile }}</span>
          </template>
        </el-table-column>
        <el-table-column label="客户状态" width="90">
          <template #default="{ row }">
            <span :class="['badge', getBadge(CUSTOMER_STATUS, row.status)]">{{ getLabel(CUSTOMER_STATUS, row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="意向等级" width="90">
          <template #default="{ row }">
            <span :class="['badge', getBadge(INTENT_LEVEL, row.intentLevel)]">{{ getLabel(INTENT_LEVEL, row.intentLevel) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="热度分" width="130">
          <template #default="{ row }">
            <div class="heat-bar">
              <div class="heat-dots">
                <span v-for="i in 5" :key="i" :class="['heat-dot', i <= heatLevel(row.heatScore) ? 'fill' : 'empty']"></span>
              </div>
              <span class="heat-score">{{ row.heatScore }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="advisorName" label="所属顾问" width="90" />
        <el-table-column label="下次跟进" width="110">
          <template #default="{ row }">
            <span :style="nextFollowStyle(row.nextFollowTime)">{{ formatDate(row.nextFollowTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openDetail(row)">详情</el-button>
            <!-- TODO: 跟进功能跳转 app 端或弹窗 -->
            <el-button size="small" link type="primary" disabled title="跟进功能待接入">跟进</el-button>
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

    <!-- Detail drawer -->
    <el-drawer v-model="drawerVisible" title="客户详情" size="440px" direction="rtl">
      <div v-if="currentCustomer" style="font-size:13px">
        <div style="display:flex; align-items:center; gap:14px; margin-bottom:20px; padding-bottom:16px; border-bottom:1px solid var(--border-light)">
          <div class="recent-avatar" style="width:44px; height:44px; font-size:16px">{{ currentCustomer.customerName?.[0] }}</div>
          <div>
            <div style="font-size:16px; font-weight:600">{{ currentCustomer.customerName }}</div>
            <div style="font-size:12px; color:var(--text-400); margin-top:2px">{{ currentCustomer.mobile }}</div>
            <div style="margin-top:6px">
              <span :class="['badge', getBadge(CUSTOMER_STATUS, currentCustomer.status)]">{{ getLabel(CUSTOMER_STATUS, currentCustomer.status) }}</span>
              &nbsp;
              <span :class="['badge', getBadge(INTENT_LEVEL, currentCustomer.intentLevel)]">{{ getLabel(INTENT_LEVEL, currentCustomer.intentLevel) }}</span>
            </div>
          </div>
        </div>

        <div v-for="row in detailRows" :key="row.label" class="detail-row">
          <span class="detail-label">{{ row.label }}</span>
          <span class="detail-value">{{ row.value || '—' }}</span>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { customerApi } from '@/api/customer'
import { deptApi } from '@/api/dept'
import { CUSTOMER_STATUS, INTENT_LEVEL, getBadgeClass, getLabel } from '@/constants/dictionary'

const getBadge = getBadgeClass

const loading  = ref(false)
const tableData = ref([])
const total    = ref(0)
const currentPage = ref(1)
const pageSize    = ref(20)
const flatDepts   = ref([])
const drawerVisible   = ref(false)
const currentCustomer = ref(null)

const filters = reactive({
  keyword: '', status: '', deptId: null, minHeatScore: null, maxHeatScore: null,
})

const detailRows = computed(() => {
  const c = currentCustomer.value
  if (!c) return []
  return [
    { label: '热度分',   value: c.heatScore },
    { label: '预算范围', value: c.budgetMin != null ? `${c.budgetMin} ~ ${c.budgetMax} 万` : null },
    { label: '关注区域', value: c.region },
    { label: '户型需求', value: c.houseType },
    { label: '所属顾问', value: c.advisorName },
    { label: '最近跟进', value: formatDate(c.latestFollowTime) },
    { label: '下次跟进', value: formatDate(c.nextFollowTime) },
  ]
})

function heatLevel(score) {
  if (!score) return 0
  return Math.min(5, Math.round(score / 20))
}
function formatDate(t) {
  if (!t) return '—'
  return dayjs(t).format('YYYY-MM-DD HH:mm')
}
function nextFollowStyle(t) {
  if (!t) return {}
  const d = dayjs(t)
  if (d.isBefore(dayjs(), 'day')) return { color: 'var(--danger)', fontWeight: 500 }
  if (d.isSame(dayjs(), 'day'))   return { color: 'var(--warning)', fontWeight: 500 }
  return { color: 'var(--text-500)', fontSize: '12px' }
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

function openDetail(row) {
  currentCustomer.value = row
  drawerVisible.value = true
}

onMounted(() => { fetchData(); fetchDepts() })
</script>
