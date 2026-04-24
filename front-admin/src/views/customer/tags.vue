<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">客户标签</h1>
      <p class="page-desc">维护客户画像标签，统一配置标签类型、颜色和启用状态</p>
    </div>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="标签名称或类型"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.tagType" placeholder="标签类型" clearable style="width: 130px">
            <el-option v-for="item in tagTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.status" placeholder="状态" clearable style="width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
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
        <span class="table-title">标签列表</span>
        <span class="table-count">共 {{ filteredTags.length }} 条</span>
        <div class="toolbar-actions">
          <el-button type="primary" size="small" @click="openCreate">+ 新增标签</el-button>
        </div>
      </div>

      <el-table :data="filteredTags" v-loading="loading" row-key="id">
        <el-table-column label="标签名称" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="tag-preview" :style="{ color: normalizeColor(row.color) }">
              <span class="tag-preview-dot" :style="{ backgroundColor: normalizeColor(row.color) }"></span>
              {{ row.tagName }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="标签类型" width="150">
          <template #default="{ row }">
            <span :class="['badge', typeBadge(row.tagType)]">{{ typeLabel(row.tagType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="颜色" width="160">
          <template #default="{ row }">
            <div class="color-cell">
              <span class="color-swatch" :style="{ backgroundColor: normalizeColor(row.color) }"></span>
              <span class="cell-mono">{{ row.color || defaultColor }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <span :class="['badge', row.status === 1 ? 'badge-success' : 'badge-gray']">
              {{ row.status === 1 ? '启用' : '停用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" link :type="row.status === 1 ? 'warning' : 'primary'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑标签' : '新增标签'"
      width="460px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="86px">
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="form.tagName" maxlength="20" show-word-limit placeholder="例如：高预算、首套刚需" />
        </el-form-item>
        <el-form-item label="标签类型" prop="tagType">
          <el-select v-model="form.tagType" placeholder="请选择标签类型" style="width: 100%">
            <el-option v-for="item in tagTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签颜色" prop="color">
          <div class="color-editor">
            <el-color-picker v-model="form.color" />
            <el-input v-model="form.color" placeholder="#C9A84C" />
          </div>
        </el-form-item>
        <el-form-item v-if="isEdit" label="状态">
          <el-radio-group v-model="form.status">
            <el-radio-button :value="1">启用</el-radio-button>
            <el-radio-button :value="0">停用</el-radio-button>
          </el-radio-group>
        </el-form-item>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerApi } from '@/api/customer'

const defaultColor = '#C9A84C'
const loading = ref(false)
const submitLoading = ref(false)
const tags = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const filters = reactive({ keyword: '', tagType: '', status: null })
const form = reactive({ id: null, tagName: '', tagType: 'INTENT', color: defaultColor, status: 1 })

const tagTypes = [
  { label: '意向特征', value: 'INTENT', badge: 'badge-gold' },
  { label: '需求偏好', value: 'NEED', badge: 'badge-info' },
  { label: '风险提示', value: 'RISK', badge: 'badge-danger' },
  { label: '跟进策略', value: 'FOLLOW', badge: 'badge-purple' },
  { label: '其他', value: 'OTHER', badge: 'badge-gray' },
]

const rules = {
  tagName: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
  tagType: [{ required: true, message: '请选择标签类型', trigger: 'change' }],
  color: [{ required: true, message: '请选择标签颜色', trigger: 'change' }],
}

const filteredTags = computed(() => {
  const keyword = filters.keyword.trim().toLowerCase()
  return tags.value.filter((tag) => {
    const matchKeyword = !keyword
      || tag.tagName?.toLowerCase().includes(keyword)
      || tag.tagType?.toLowerCase().includes(keyword)
      || typeLabel(tag.tagType).toLowerCase().includes(keyword)
    const matchType = !filters.tagType || tag.tagType === filters.tagType
    const matchStatus = filters.status === null || filters.status === '' || tag.status === filters.status
    return matchKeyword && matchType && matchStatus
  })
})

function typeLabel(value) {
  return tagTypes.find((item) => item.value === value)?.label || value || '未分类'
}

function typeBadge(value) {
  return tagTypes.find((item) => item.value === value)?.badge || 'badge-gray'
}

function normalizeColor(color) {
  return color || defaultColor
}

function logStep(action, payload = {}) {
  console.info(`[customer-tags] ${action}`, payload)
}

async function fetchTags() {
  loading.value = true
  logStep('fetch:start', { filters: { ...filters } })
  try {
    const data = await customerApi.tagList()
    tags.value = Array.isArray(data) ? data : []
    logStep('fetch:success', { count: tags.value.length })
  } catch (error) {
    console.error('[customer-tags] fetch:failed', error)
    throw error
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  logStep('filter:apply', { filters: { ...filters }, count: filteredTags.value.length })
}

function handleReset() {
  Object.assign(filters, { keyword: '', tagType: '', status: null })
  handleSearch()
}

function openCreate() {
  isEdit.value = false
  Object.assign(form, { id: null, tagName: '', tagType: 'INTENT', color: defaultColor, status: 1 })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    tagName: row.tagName,
    tagType: row.tagType || 'OTHER',
    color: normalizeColor(row.color),
    status: row.status ?? 1,
  })
  dialogVisible.value = true
}

function buildPayload() {
  return {
    tagName: form.tagName.trim(),
    tagType: form.tagType,
    color: normalizeColor(form.color),
    ...(isEdit.value ? { status: form.status } : {}),
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  const payload = buildPayload()
  logStep(isEdit.value ? 'update:start' : 'create:start', payload)
  try {
    if (isEdit.value) {
      await customerApi.updateTag(form.id, payload)
      ElMessage.success('标签已更新')
    } else {
      await customerApi.createTag(payload)
      ElMessage.success('标签创建成功')
    }
    dialogVisible.value = false
    await fetchTags()
  } catch (error) {
    console.error('[customer-tags] submit:failed', error)
    throw error
  } finally {
    submitLoading.value = false
  }
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  const action = nextStatus === 1 ? '启用' : '停用'
  await ElMessageBox.confirm(`确定${action}标签「${row.tagName}」吗？`, `${action}确认`, { type: 'warning' })
  logStep('status:update:start', { id: row.id, nextStatus })
  await customerApi.updateTag(row.id, {
    tagName: row.tagName,
    tagType: row.tagType,
    color: normalizeColor(row.color),
    status: nextStatus,
  })
  ElMessage.success(`标签已${action}`)
  await fetchTags()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除标签「${row.tagName}」吗？已有客户标签关系会被保留，标签将从可用列表移除。`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  logStep('delete:start', { id: row.id, tagName: row.tagName })
  await customerApi.deleteTag(row.id)
  ElMessage.success('标签已删除')
  await fetchTags()
}

onMounted(fetchTags)
</script>

<style scoped>
.tag-preview {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  max-width: 100%;
  font-weight: 600;
}

.tag-preview-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  box-shadow: 0 0 0 3px rgba(15, 23, 42, .04);
  flex-shrink: 0;
}

.color-cell,
.color-editor {
  display: flex;
  align-items: center;
  gap: 10px;
}

.color-swatch {
  width: 18px;
  height: 18px;
  border-radius: 5px;
  border: 1px solid var(--border);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, .35);
  flex-shrink: 0;
}

.color-editor {
  width: 100%;
}

.color-editor :deep(.el-input) {
  flex: 1;
}
</style>
