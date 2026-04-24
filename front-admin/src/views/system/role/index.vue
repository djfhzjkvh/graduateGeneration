<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">角色管理</h1>
      <p class="page-desc">系统内置角色列表（当前为只读模式，MVP 阶段暂不支持编辑）</p>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title">角色列表</span>
        <span class="table-count">{{ tableData.length }} 条</span>
      </div>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column label="角色编码" width="150">
          <template #default="{ row }"><span class="cell-mono">{{ row.roleCode }}</span></template>
        </el-table-column>
        <el-table-column label="角色名称" min-width="150" show-overflow-tooltip>
          <template #default="{ row }"><span class="cell-primary cell-ellipsis">{{ row.roleName }}</span></template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" min-width="260" show-overflow-tooltip>
          <template #default="{ row }"><span class="cell-muted cell-ellipsis">{{ row.remark || roleDesc(row.roleCode) }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span :class="['badge', row.status === 1 ? 'badge-success' : 'badge-danger']">{{ row.status === 1 ? '启用' : '禁用' }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div style="padding:14px 18px; font-size:12px; color:var(--text-400); border-top:1px solid var(--border-light)">
        后续可接入 <code>sys_permission</code> 和 <code>sys_role_permission</code> 表，实现按钮级、接口级权限控制。
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { roleApi } from '@/api/role'

const loading = ref(false)
const tableData = ref([])

const descMap = {
  ADMIN: '拥有全部后台管理权限',
  MANAGER: '可查看团队数据，管理客户和任务分配',
  ADVISOR: '负责日常客户跟进和任务执行',
}
const roleDesc = (code) => descMap[code] || ''

async function fetchData() {
  loading.value = true
  try {
    tableData.value = await roleApi.list() || []
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>
