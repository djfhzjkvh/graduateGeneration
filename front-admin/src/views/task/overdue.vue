<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">逾期任务</h1>
      <p class="page-desc" style="color:var(--danger)">⚠ 共 {{ total }} 个逾期任务，请及时处理或转派</p>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title" style="color:var(--danger)">逾期任务</span>
        <span class="table-count">{{ total }} 条</span>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column label="任务标题" min-width="180">
          <template #default="{ row }"><span class="cell-primary">{{ row.title }}</span></template>
        </el-table-column>
        <el-table-column prop="customerName" label="关联客户" width="90" />
        <el-table-column prop="ownerName"    label="负责人"   width="80" />
        <el-table-column label="应完成时间" width="130">
          <template #default="{ row }">
            <span style="font-size:12px; color:var(--danger)">{{ row.taskDate }} {{ row.taskTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="优先级" width="80">
          <template #default="{ row }">
            <span :class="['badge', getBadgeClass(TASK_PRIORITY, row.priority)]">{{ getLabel(TASK_PRIORITY, row.priority) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openDetail(row)">详情</el-button>
            <!-- TODO: 转派接口后端待补充 -->
            <el-button size="small" link type="danger" disabled>转派</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-pagination">
        <el-pagination v-model:current-page="currentPage" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </div>

    <el-drawer v-model="drawerVisible" title="逾期任务详情" size="400px">
      <div v-if="cur" style="font-size:13px">
        <div style="font-size:16px; font-weight:600; margin-bottom:12px">{{ cur.title }}</div>
        <div class="detail-row"><span class="detail-label">关联客户</span><span class="detail-value">{{ cur.customerName }}</span></div>
        <div class="detail-row"><span class="detail-label">负责人</span><span class="detail-value">{{ cur.ownerName }}</span></div>
        <div class="detail-row"><span class="detail-label">计划日期</span><span class="detail-value" style="color:var(--danger)">{{ cur.taskDate }} {{ cur.taskTime }}</span></div>
        <div class="detail-row"><span class="detail-label">任务内容</span><span class="detail-value">{{ cur.content || '—' }}</span></div>
        <div style="margin-top:16px; padding:10px 12px; background:rgba(239,68,68,.06); border-radius:var(--radius-sm); font-size:12px; color:var(--danger)">
          任务转派功能待后端 /admin/tasks/{id}/transfer 接口补充后接入
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { taskApi } from '@/api/task'
import { TASK_PRIORITY, getBadgeClass, getLabel } from '@/constants/dictionary'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const drawerVisible = ref(false)
const cur = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const data = await taskApi.page({ status: 'OVERDUE', pageNum: currentPage.value, pageSize: 20 })
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function openDetail(row) { cur.value = row; drawerVisible.value = true }
onMounted(fetchData)
</script>
