<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">高意向客户</h1>
      <p class="page-desc">热度分 ≥ {{ highIntentThreshold }} 的客户，共 {{ total }} 位</p>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title">高意向客户</span>
        <span class="table-count">{{ total }} 条</span>
        <div class="toolbar-actions">
          <!-- TODO: 批量分配接口后端待补充 -->
          <el-button type="primary" size="small" disabled title="客户分配接口待接入">批量分配</el-button>
          <el-button size="small" @click="handleBatchCalc" :loading="calcLoading">重新计算热度</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column label="客户姓名" min-width="130" show-overflow-tooltip>
          <template #default="{ row }"><span class="cell-primary cell-ellipsis">{{ row.customerName }}</span></template>
        </el-table-column>
        <el-table-column label="手机号" width="155">
          <template #default="{ row }"><span class="cell-mono">{{ row.mobile }}</span></template>
        </el-table-column>
        <el-table-column label="热度分" width="155">
          <template #default="{ row }">
            <div class="heat-bar">
              <div class="heat-dots">
                <span v-for="i in 5" :key="i" :class="['heat-dot', i <= Math.min(5, Math.round((row.heatScore||0)/20)) ? 'fill' : 'empty']"></span>
              </div>
              <span class="heat-score">{{ row.heatScore }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="客户状态" width="115">
          <template #default="{ row }">
            <span :class="['badge', getBadgeClass(CUSTOMER_STATUS, row.status)]">{{ getLabel(CUSTOMER_STATUS, row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="region"      label="关注区域" width="120" />
        <el-table-column label="预算范围" width="145">
          <template #default="{ row }">
            <span style="font-size:12px" v-if="row.budgetMin != null">{{ row.budgetMin }}~{{ row.budgetMax }}万</span>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column prop="advisorName" label="所属顾问" width="115" />
        <el-table-column label="下次跟进" width="145">
          <template #default="{ row }">
            <span :style="nextStyle(row.nextFollowTime)">{{ fmt(row.nextFollowTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="95" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @change="fetchData"
        />
      </div>
    </div>

    <el-drawer v-model="drawerVisible" title="客户详情" size="400px">
      <div v-if="cur" style="font-size:13px">
        <div style="font-size:16px; font-weight:600; margin-bottom:4px">{{ cur.customerName }}</div>
        <div style="font-size:12px; color:var(--text-400); margin-bottom:14px">{{ cur.mobile }}</div>
        <div v-for="r in rows" :key="r.label" class="detail-row">
          <span class="detail-label">{{ r.label }}</span>
          <span class="detail-value">{{ r.value || '—' }}</span>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { heatApi } from '@/api/heat'
import { opsApi } from '@/api/ops'
import { CUSTOMER_STATUS, INTENT_LEVEL, getBadgeClass, getLabel } from '@/constants/dictionary'
import { logBusiness, errorBusiness } from '@/utils/logger'

const loading = ref(false)
const calcLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const drawerVisible = ref(false)
const cur = ref(null)
const highIntentThreshold = ref(80)

const rows = computed(() => !cur.value ? [] : [
  { label: '意向等级', value: getLabel(INTENT_LEVEL, cur.value.intentLevel) },
  { label: '热度分',   value: cur.value.heatScore },
  { label: '关注区域', value: cur.value.region },
  { label: '预算范围', value: cur.value.budgetMin != null ? `${cur.value.budgetMin}~${cur.value.budgetMax}万` : null },
  { label: '所属顾问', value: cur.value.advisorName },
  { label: '下次跟进', value: fmt(cur.value.nextFollowTime) },
])

const fmt = (t) => t ? dayjs(t).format('MM-DD HH:mm') : '—'
const nextStyle = (t) => {
  if (!t) return {}
  const d = dayjs(t)
  if (d.isBefore(dayjs(), 'day')) return { color: 'var(--danger)', fontWeight: 500, fontSize: '12px' }
  if (d.isSame(dayjs(), 'day'))   return { color: 'var(--warning)', fontWeight: 500, fontSize: '12px' }
  return { fontSize: '12px', color: 'var(--text-500)' }
}

async function fetchData() {
  loading.value = true
  try {
    const data = await heatApi.highIntent({ page: currentPage.value, size: pageSize.value })
    // high-intent 返回的是列表（非分页），做前端分页展示
    const list = Array.isArray(data) ? data : (data?.list || [])
    total.value = Array.isArray(data) ? data.length : (data?.total || 0)
    tableData.value = list
  } finally {
    loading.value = false
  }
}

async function fetchHighIntentThreshold() {
  try {
    const configs = await opsApi.configs({ configKey: 'heat.high.threshold' })
    const value = Number(configs?.[0]?.configValue)
    if (!Number.isNaN(value)) {
      highIntentThreshold.value = value
    }
  } catch (error) {
    errorBusiness('heat', 'threshold:fetch:failed', error)
  }
}

async function handleBatchCalc() {
  await ElMessageBox.confirm('确定重新计算全部客户热度分吗？计算完成后会刷新高意向客户列表。', '热度计算确认', {
    confirmButtonText: '重新计算',
    cancelButtonText: '取消',
    type: 'warning',
  })
  calcLoading.value = true
  try {
    const result = await heatApi.batchCalculate()
    logBusiness('heat', 'batch-calculate', result || {})
    ElMessage.success('热度分批量更新完成')
    fetchData()
  } catch (error) {
    errorBusiness('heat', 'batch-calculate:failed', error)
  } finally {
    calcLoading.value = false
  }
}

function openDetail(row) { cur.value = row; drawerVisible.value = true }

onMounted(() => {
  fetchHighIntentThreshold()
  fetchData()
})
</script>
