<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">今日任务</h1>
      <p class="page-desc">{{ today }} · 共 {{ total }} 个任务</p>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title">今日任务</span>
        <span class="table-count">{{ total }} 条</span>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column label="任务标题" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-primary cell-ellipsis">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="关联客户" width="130" show-overflow-tooltip />
        <el-table-column prop="ownerName"    label="负责人"   width="115" show-overflow-tooltip />
        <el-table-column label="计划时间" width="130">
          <template #default="{ row }">
            <span style="font-size:12px">{{ row.taskTime || '全天' }}</span>
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
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openDetail(row)">详情</el-button>
            <!-- TODO: 标记完成接口后端待补充 -->
            <el-button size="small" link type="primary" v-if="row.status === 'PENDING'" disabled>完成</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-pagination">
        <el-pagination v-model:current-page="currentPage" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </div>

    <el-drawer v-model="drawerVisible" title="任务详情" size="400px">
      <div v-if="cur" style="font-size:13px">
        <div style="font-size:16px; font-weight:600; margin-bottom:12px">{{ cur.title }}</div>
        <div class="detail-row"><span class="detail-label">关联客户</span><span class="detail-value">{{ cur.customerName }}</span></div>
        <div class="detail-row"><span class="detail-label">负责人</span><span class="detail-value">{{ cur.ownerName }}</span></div>
        <div class="detail-row"><span class="detail-label">计划时间</span><span class="detail-value">{{ cur.taskDate }} {{ cur.taskTime }}</span></div>
        <div class="detail-row"><span class="detail-label">任务内容</span><span class="detail-value">{{ cur.content || '—' }}</span></div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import dayjs from 'dayjs'
import { taskApi } from '@/api/task'
import { TASK_STATUS, TASK_PRIORITY, getBadgeClass, getLabel } from '@/constants/dictionary'

const today = dayjs().format('YYYY年M月D日')
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const drawerVisible = ref(false)
const cur = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const data = await taskApi.page({ date: dayjs().format('YYYY-MM-DD'), pageNum: currentPage.value, pageSize: 20 })
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function openDetail(row) { cur.value = row; drawerVisible.value = true }
onMounted(fetchData)
</script>
