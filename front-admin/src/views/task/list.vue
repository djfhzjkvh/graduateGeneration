<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">任务列表</h1>
      <p class="page-desc">查看全团队跟进任务执行情况</p>
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
        <!-- TODO: taskType / priority / deptId — TaskQueryDTO 待扩展 -->
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
        <el-table-column label="任务标题" min-width="160">
          <template #default="{ row }"><span class="cell-primary">{{ row.title }}</span></template>
        </el-table-column>
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <span :class="['badge', getBadgeClass(TASK_TYPE, row.taskType)]">{{ getLabel(TASK_TYPE, row.taskType) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="关联客户" width="100" />
        <el-table-column prop="ownerName"    label="负责人"   width="90" />
        <el-table-column label="任务日期" width="140">
          <template #default="{ row }">
            <span style="font-size:12px">{{ row.taskDate }}{{ row.taskTime ? ' ' + row.taskTime : '' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="优先级" width="80">
          <template #default="{ row }">
            <span :class="['badge', getBadgeClass(TASK_PRIORITY, row.priority)]">{{ getLabel(TASK_PRIORITY, row.priority) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <span :class="['badge', getBadgeClass(TASK_STATUS, row.status)]">{{ getLabel(TASK_STATUS, row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openDetail(row)">详情</el-button>
            <!-- TODO: 完成 / 转派接口后端待补充 -->
            <el-button size="small" link type="primary" v-if="row.status === 'PENDING'" disabled>完成</el-button>
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
    <el-drawer v-model="drawerVisible" title="任务详情" size="400px">
      <div v-if="cur" style="font-size:13px">
        <div style="font-size:16px; font-weight:600; margin-bottom:4px">{{ cur.title }}</div>
        <div style="margin-bottom:14px; display:flex; gap:6px">
          <span :class="['badge', getBadgeClass(TASK_STATUS, cur.status)]">{{ getLabel(TASK_STATUS, cur.status) }}</span>
          <span :class="['badge', getBadgeClass(TASK_PRIORITY, cur.priority)]">{{ getLabel(TASK_PRIORITY, cur.priority) }}</span>
        </div>
        <div class="detail-row"><span class="detail-label">任务类型</span><span class="detail-value">{{ getLabel(TASK_TYPE, cur.taskType) }}</span></div>
        <div class="detail-row"><span class="detail-label">关联客户</span><span class="detail-value">{{ cur.customerName }}</span></div>
        <div class="detail-row"><span class="detail-label">负责人</span><span class="detail-value">{{ cur.ownerName }}</span></div>
        <div class="detail-row"><span class="detail-label">计划时间</span><span class="detail-value">{{ cur.taskDate }} {{ cur.taskTime }}</span></div>
        <div class="detail-row" v-if="cur.content"><span class="detail-label">任务内容</span><span class="detail-value" style="white-space:pre-wrap">{{ cur.content }}</span></div>
        <div class="detail-row" v-if="cur.completeTime"><span class="detail-label">完成时间</span><span class="detail-value">{{ cur.completeTime }}</span></div>
        <div style="margin-top:16px; padding:10px 12px; background:var(--surface-100); border-radius:var(--radius-sm); font-size:12px; color:var(--text-400)">
          任务完成 / 转派功能待后端接口补充后接入
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { taskApi } from '@/api/task'
import { TASK_STATUS, TASK_PRIORITY, TASK_TYPE, getBadgeClass, getLabel } from '@/constants/dictionary'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const drawerVisible = ref(false)
const cur = ref(null)

const filters = reactive({ status: '', date: '' })

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

function handleSearch() { currentPage.value = 1; fetchData() }
function handleReset() { Object.assign(filters, { status: '', date: '' }); handleSearch() }
function openDetail(row) { cur.value = row; drawerVisible.value = true }

onMounted(fetchData)
</script>
