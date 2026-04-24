<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">任务调度</h1>
      <p class="page-desc">手动触发系统自动化任务，查看本次处理结果</p>
    </div>

    <div class="job-grid">
      <div v-for="job in jobs" :key="job.key" class="job-card">
        <div class="job-icon">{{ job.icon }}</div>
        <div class="job-body">
          <div class="job-title">{{ job.title }}</div>
          <div class="job-desc">{{ job.desc }}</div>
          <div class="job-meta">接口：{{ job.path }}</div>
        </div>
        <el-button type="primary" :loading="runningKey === job.key" @click="runJob(job)">立即执行</el-button>
      </div>
    </div>

    <div class="table-card result-card">
      <div class="table-toolbar">
        <span class="table-title">最近执行结果</span>
        <span class="table-count">{{ runResults.length }} 条</span>
        <div class="toolbar-actions">
          <el-button size="small" :disabled="!runResults.length" @click="runResults = []">清空</el-button>
        </div>
      </div>

      <el-table :data="runResults" empty-text="暂无执行结果">
        <el-table-column label="任务名称" min-width="180">
          <template #default="{ row }">
            <span class="cell-primary">{{ row.jobName || row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="影响数量" width="110">
          <template #default="{ row }">{{ row.affectedCount ?? 0 }}</template>
        </el-table-column>
        <el-table-column label="执行消息" prop="message" min-width="260" show-overflow-tooltip />
        <el-table-column label="执行时间" width="155">
          <template #default="{ row }">{{ formatDate(row.runAt) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { opsApi } from '@/api/ops'
import { formatDate } from '@/utils/format'
import { logBusiness, errorBusiness } from '@/utils/logger'

const runningKey = ref('')
const runResults = ref([])

const jobs = [
  {
    key: 'refreshOverdueTasks',
    title: '刷新逾期任务',
    desc: '扫描过期未完成任务，并统一标记为已逾期。',
    path: 'POST /api/admin/jobs/refresh-overdue-tasks',
    icon: '期',
    action: opsApi.refreshOverdueTasks,
  },
  {
    key: 'generateFollowTasks',
    title: '生成跟进提醒任务',
    desc: '根据客户下次跟进时间生成待处理任务。',
    path: 'POST /api/admin/jobs/generate-follow-tasks',
    icon: '跟',
    action: opsApi.generateFollowTasks,
  },
  {
    key: 'remindDueSoonTasks',
    title: '即将到期任务提醒',
    desc: '扫描临近到期任务并生成提醒记录。',
    path: 'POST /api/admin/jobs/remind-due-soon-tasks',
    icon: '醒',
    action: opsApi.remindDueSoonTasks,
  },
  {
    key: 'generateDailyReport',
    title: '生成每日统计报表',
    desc: '汇总当日客户、任务和成交数据，生成日报。',
    path: 'POST /api/admin/jobs/generate-daily-report',
    icon: '报',
    action: opsApi.generateDailyReport,
  },
]

async function runJob(job) {
  await ElMessageBox.confirm(`确定立即执行「${job.title}」吗？`, '任务调度确认', {
    confirmButtonText: '立即执行',
    cancelButtonText: '取消',
    type: 'warning',
  })

  runningKey.value = job.key
  try {
    const result = await job.action()
    const row = { title: job.title, ...result, runAt: new Date().toISOString() }
    runResults.value.unshift(row)
    logBusiness('ops-job', 'run:success', row)
    ElMessage.success(result?.message || '任务执行完成')
  } catch (error) {
    errorBusiness('ops-job', 'run:failed', { job: job.key, error })
  } finally {
    runningKey.value = ''
  }
}
</script>

<style scoped>
.job-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 14px;
}
.job-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 18px;
  box-shadow: var(--shadow-sm);
}
.job-icon {
  width: 42px;
  height: 42px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: rgba(201,168,76,.12);
  color: var(--gold-500);
  font-weight: 700;
}
.job-body { flex: 1; min-width: 0; }
.job-title { font-size: 14px; font-weight: 600; color: var(--text-900); }
.job-desc { margin-top: 4px; font-size: 12px; color: var(--text-500); }
.job-meta { margin-top: 7px; font-size: 11px; color: var(--text-400); font-family: monospace; }
.result-card { margin-top: 14px; }
@media (max-width: 1200px) {
  .job-grid { grid-template-columns: 1fr; }
}
</style>
