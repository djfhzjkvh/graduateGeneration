<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <p class="page-desc">管理系统用户账号、角色权限和部门归属</p>
    </div>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item>
          <el-input v-model="filters.keyword" placeholder="用户名、昵称或手机号" clearable style="width:200px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.roleId" placeholder="角色" clearable style="width:110px">
            <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.deptId" placeholder="部门" clearable style="width:110px">
            <el-option v-for="d in flatDepts" :key="d.id" :label="d.deptName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.status" placeholder="状态" clearable style="width:90px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title">用户列表</span>
        <span class="table-count">共 {{ total }} 条</span>
        <div class="toolbar-actions">
          <el-button type="primary" size="small" @click="openCreate">+ 新增用户</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column label="用户名" min-width="100">
          <template #default="{ row }"><span class="cell-primary">{{ row.username }}</span></template>
        </el-table-column>
        <el-table-column prop="nickname"    label="昵称"   width="90" />
        <el-table-column label="手机号" width="130">
          <template #default="{ row }"><span class="cell-mono">{{ row.mobile }}</span></template>
        </el-table-column>
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <span :class="['badge', roleBadge(row.roleCode)]">{{ row.roleName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deptName"    label="部门"   width="90" />
        <el-table-column prop="managerName" label="上级经理" width="90" />
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <span :class="['badge', row.status === 1 ? 'badge-success' : 'badge-danger']">{{ row.status === 1 ? '启用' : '禁用' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" link :type="row.status === 1 ? 'danger' : 'primary'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- Create / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="480px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="!isEdit">
              <el-input v-model="form.password" type="password" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="mobile">
              <el-input v-model="form.mobile" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="角色" prop="roleId">
              <el-select v-model="form.roleId" style="width:100%">
                <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <el-select v-model="form.deptId" style="width:100%">
                <el-option v-for="d in flatDepts" :key="d.id" :label="d.deptName" :value="d.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="上级经理">
              <el-select v-model="form.managerId" clearable style="width:100%">
                <el-option v-for="m in managers" :key="m.id" :label="m.nickname || m.username" :value="m.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width:100%">
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '确认新增' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'
import { roleApi } from '@/api/role'
import { deptApi } from '@/api/dept'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const roles = ref([])
const flatDepts = ref([])
const managers = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const filters = reactive({ keyword: '', roleId: null, deptId: null, status: null })
const form = reactive({ id: null, username: '', password: '', nickname: '', mobile: '', roleId: null, deptId: null, managerId: null, status: 1 })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码',   trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称',   trigger: 'blur' }],
  roleId:   [{ required: true, message: '请选择角色',   trigger: 'change' }],
}

function roleBadge(code) {
  const map = { ADMIN: 'badge-purple', MANAGER: 'badge-gold', ADVISOR: 'badge-info' }
  return map[code] || 'badge-gray'
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNum: currentPage.value, pageSize: pageSize.value, ...filters }
    const data = await userApi.page(params)
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

async function fetchOptions() {
  const [r, t] = await Promise.allSettled([roleApi.list(), deptApi.tree()])
  roles.value = r.status === 'fulfilled' ? (r.value || []) : []
  const tree = t.status === 'fulfilled' ? (t.value || []) : []
  flatDepts.value = flattenTree(tree)
}

async function fetchManagers() {
  try {
    const r = roles.value.find(r => r.roleCode === 'MANAGER')
    if (r) {
      const data = await userApi.page({ roleId: r.id, pageNum: 1, pageSize: 100 })
      managers.value = data?.list || []
    }
  } catch { /* ignore */ }
}

function flattenTree(nodes) {
  return nodes.reduce((a, n) => { a.push(n); if (n.children?.length) a.push(...flattenTree(n.children)); return a }, [])
}

function handleSearch() { currentPage.value = 1; fetchData() }
function handleReset() { Object.assign(filters, { keyword: '', roleId: null, deptId: null, status: null }); handleSearch() }

function openCreate() {
  isEdit.value = false
  Object.assign(form, { id: null, username: '', password: '', nickname: '', mobile: '', roleId: null, deptId: null, managerId: null, status: 1 })
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  const data = await userApi.detail(row.id)
  Object.assign(form, data, { password: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await userApi.update(form.id, form)
      ElMessage.success('用户信息已更新')
    } else {
      await userApi.create(form)
      ElMessage.success('用户创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

async function toggleStatus(row) {
  const next = row.status === 1 ? 0 : 1
  const label = next === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm(`确定要${label}用户「${row.nickname}」吗？`, '提示', { type: 'warning' })
  await userApi.update(row.id, { ...row, password: undefined, status: next })
  ElMessage.success(`已${label}`)
  fetchData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除用户「${row.nickname}」吗？此操作不可恢复。`, '删除确认', { type: 'warning' })
  await userApi.remove(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(async () => {
  await fetchOptions()
  await Promise.all([fetchData(), fetchManagers()])
})
</script>
