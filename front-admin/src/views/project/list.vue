<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">楼盘管理</h1>
      <p class="page-desc">维护本项目楼盘资料、价格信息、交付时间和户型卖点</p>
    </div>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="楼盘名称或地址"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.region" placeholder="区域" clearable style="width: 130px" />
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
        <span class="table-title">楼盘列表</span>
        <span class="table-count">共 {{ total }} 条</span>
        <div class="toolbar-actions">
          <el-button type="primary" size="small" @click="openCreate">+ 新增楼盘</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" row-key="id">
        <el-table-column label="楼盘名称" min-width="190" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="project-name">
              <span class="cell-primary cell-ellipsis">{{ row.projectName }}</span>
              <span class="cell-muted cell-ellipsis">{{ row.city || '-' }} · {{ row.region || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="均价" width="135">
          <template #default="{ row }">
            <span class="price-text">{{ formatPrice(row.avgPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="地址" min-width="210" show-overflow-tooltip>
          <template #default="{ row }">{{ row.address || '-' }}</template>
        </el-table-column>
        <el-table-column label="交付日期" width="120">
          <template #default="{ row }">{{ row.handoverDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span :class="['badge', row.status === 1 ? 'badge-success' : 'badge-gray']">
              {{ row.status === 1 ? '启用' : '停用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" link type="primary" @click="openHouseTypes(row)">户型</el-button>
            <el-button size="small" link :type="row.status === 1 ? 'warning' : 'primary'" @click="toggleProjectStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" link type="danger" @click="handleDeleteProject(row)">删除</el-button>
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
          @change="fetchProjects"
        />
      </div>
    </div>

    <el-dialog
      v-model="projectDialogVisible"
      :title="isProjectEdit ? '编辑楼盘' : '新增楼盘'"
      width="680px"
      destroy-on-close
    >
      <el-form ref="projectFormRef" :model="projectForm" :rules="projectRules" label-width="86px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="楼盘名称" prop="projectName">
              <el-input v-model="projectForm.projectName" maxlength="40" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在城市">
              <el-input v-model="projectForm.city" placeholder="例如：杭州" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="区域">
              <el-input v-model="projectForm.region" placeholder="例如：滨江区" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="均价">
              <el-input-number v-model="projectForm.avgPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="projectForm.address" maxlength="80" show-word-limit />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="交付日期">
              <el-date-picker v-model="projectForm.handoverDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="projectForm.status">
                <el-radio-button :value="1">启用</el-radio-button>
                <el-radio-button :value="0">停用</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="项目卖点">
          <el-input v-model="projectForm.highlights" type="textarea" :rows="3" maxlength="240" show-word-limit />
        </el-form-item>
        <el-form-item label="优惠信息">
          <el-input v-model="projectForm.discountInfo" type="textarea" :rows="2" maxlength="180" show-word-limit />
        </el-form-item>
        <el-form-item label="楼盘图片">
          <div class="inline-upload">
            <el-upload
              ref="projectUploadRef"
              :auto-upload="false"
              :limit="1"
              accept="image/*"
              :on-change="handleProjectImageChange"
              :on-remove="handleProjectImageRemove"
            >
              <el-button size="small">选择图片</el-button>
            </el-upload>
            <el-button size="small" type="primary" :loading="projectImageUploading" @click="uploadProjectImage">
              上传图片
            </el-button>
          </div>
          <div v-if="projectImage" class="uploaded-file">
            <img v-if="isImage(projectImage)" :src="projectImage.fileUrl" :alt="projectImage.fileName" />
            <div class="uploaded-file-info">
              <div class="cell-primary">{{ projectImage.fileName }}</div>
              <div class="cell-muted cell-ellipsis">{{ projectImage.fileUrl }}</div>
            </div>
          </div>
          <div class="field-tip">后端楼盘表暂未提供图片字段，当前图片会先作为附件上传并返回文件地址。</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="projectSubmitLoading" @click="handleSubmitProject">
          {{ isProjectEdit ? '保存修改' : '确认新增' }}
        </el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="houseDrawerVisible" size="720px" direction="rtl" destroy-on-close>
      <template #header>
        <div>
          <div class="drawer-title">{{ currentProject?.projectName || '楼盘户型' }}</div>
          <div class="drawer-subtitle">维护面积、房型、总价区间和销售卖点</div>
        </div>
      </template>

      <div class="house-toolbar">
        <span class="table-count">共 {{ houseTypes.length }} 个户型</span>
        <el-button type="primary" size="small" @click="openCreateHouseType">+ 新增户型</el-button>
      </div>

      <el-table :data="houseTypes" v-loading="houseLoading" row-key="id">
        <el-table-column label="户型" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-primary">{{ row.typeName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="房型" width="95">
          <template #default="{ row }">{{ row.rooms || '-' }}</template>
        </el-table-column>
        <el-table-column label="面积" width="95">
          <template #default="{ row }">{{ row.area ? `${row.area}㎡` : '-' }}</template>
        </el-table-column>
        <el-table-column label="总价区间" width="140">
          <template #default="{ row }">{{ formatTotalPrice(row) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span :class="['badge', row.status === 1 ? 'badge-success' : 'badge-gray']">
              {{ row.status === 1 ? '启用' : '停用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openEditHouseType(row)">编辑</el-button>
            <el-button size="small" link type="danger" @click="handleDeleteHouseType(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <el-dialog
      v-model="houseDialogVisible"
      :title="isHouseEdit ? '编辑户型' : '新增户型'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="houseFormRef" :model="houseForm" :rules="houseRules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="户型名称" prop="typeName">
              <el-input v-model="houseForm.typeName" placeholder="例如：89方三房" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房型">
              <el-input v-model="houseForm.rooms" placeholder="例如：3室2厅2卫" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="面积">
              <el-input-number v-model="houseForm.area" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="houseForm.status">
                <el-radio-button :value="1">启用</el-radio-button>
                <el-radio-button :value="0">停用</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最低总价">
              <el-input-number v-model="houseForm.totalPriceMin" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高总价">
              <el-input-number v-model="houseForm.totalPriceMax" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="销售卖点">
          <el-input v-model="houseForm.sellingPoints" type="textarea" :rows="3" maxlength="220" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="houseDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="houseSubmitLoading" @click="handleSubmitHouseType">
          {{ isHouseEdit ? '保存修改' : '确认新增' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { projectApi } from '@/api/project'
import { formatPrice } from '@/utils/format'
import { cleanPayload } from '@/utils/payload'
import { logBusiness } from '@/utils/logger'
import { isImageFile, uploadBusinessFile } from '@/utils/upload'

const loading = ref(false)
const projectSubmitLoading = ref(false)
const houseLoading = ref(false)
const houseSubmitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const projectDialogVisible = ref(false)
const houseDrawerVisible = ref(false)
const houseDialogVisible = ref(false)
const isProjectEdit = ref(false)
const isHouseEdit = ref(false)
const currentProject = ref(null)
const houseTypes = ref([])
const projectFormRef = ref()
const houseFormRef = ref()
const projectUploadRef = ref()
const selectedProjectImage = ref(null)
const projectImageUploading = ref(false)
const projectImage = ref(null)
const MAX_UPLOAD_IMAGE_SIZE = 50 * 1024 * 1024

const filters = reactive({ keyword: '', region: '', status: null })

const projectForm = reactive({
  id: null,
  projectName: '',
  city: '',
  region: '',
  address: '',
  avgPrice: null,
  highlights: '',
  discountInfo: '',
  handoverDate: '',
  status: 1,
})

const houseForm = reactive({
  id: null,
  projectId: null,
  typeName: '',
  area: null,
  rooms: '',
  totalPriceMin: null,
  totalPriceMax: null,
  sellingPoints: '',
  status: 1,
})

const projectRules = {
  projectName: [{ required: true, message: '请输入楼盘名称', trigger: 'blur' }],
}

const houseRules = {
  typeName: [{ required: true, message: '请输入户型名称', trigger: 'blur' }],
}

function logStep(action, payload = {}) {
  logBusiness('projects', action, payload)
}

function formatTotalPrice(row) {
  const min = row.totalPriceMin
  const max = row.totalPriceMax
  if (min && max) return `${min} ~ ${max} 万`
  if (min) return `${min} 万起`
  if (max) return `${max} 万以内`
  return '-'
}

async function fetchProjects() {
  loading.value = true
  const params = { pageNum: currentPage.value, pageSize: pageSize.value, ...filters }
  try {
    const data = await projectApi.page(params)
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchProjects()
}

function handleReset() {
  Object.assign(filters, { keyword: '', region: '', status: null })
  handleSearch()
}

function resetProjectForm() {
  Object.assign(projectForm, {
    id: null,
    projectName: '',
    city: '',
    region: '',
    address: '',
    avgPrice: null,
    highlights: '',
    discountInfo: '',
    handoverDate: '',
    status: 1,
  })
  selectedProjectImage.value = null
  projectImage.value = null
  projectUploadRef.value?.clearFiles()
}

function openCreate() {
  isProjectEdit.value = false
  resetProjectForm()
  projectDialogVisible.value = true
}

async function openEdit(row) {
  isProjectEdit.value = true
  const data = await projectApi.detail(row.id)
  Object.assign(projectForm, {
    id: data.id,
    projectName: data.projectName || '',
    city: data.city || '',
    region: data.region || '',
    address: data.address || '',
    avgPrice: data.avgPrice ?? null,
    highlights: data.highlights || '',
    discountInfo: data.discountInfo || '',
    handoverDate: data.handoverDate || '',
    status: data.status ?? 1,
  })
  projectDialogVisible.value = true
}

async function handleSubmitProject() {
  await projectFormRef.value.validate()
  projectSubmitLoading.value = true
  const payload = cleanPayload(projectForm, ['projectName', 'city', 'region', 'address', 'avgPrice', 'highlights', 'discountInfo', 'handoverDate', 'status'])
  logStep(isProjectEdit.value ? 'update:start' : 'create:start', payload)
  try {
    if (isProjectEdit.value) {
      await projectApi.update(projectForm.id, payload)
      ElMessage.success('楼盘信息已更新')
    } else {
      await projectApi.create(payload)
      ElMessage.success('楼盘创建成功')
    }
    projectDialogVisible.value = false
    await fetchProjects()
  } finally {
    projectSubmitLoading.value = false
  }
}

function handleProjectImageChange(file) {
  if (file.size > MAX_UPLOAD_IMAGE_SIZE) {
    ElMessage.warning('图片不能超过 50MB，请压缩后上传')
    projectUploadRef.value?.clearFiles()
    selectedProjectImage.value = null
    logStep('image:select:oversize', { name: file.name, size: file.size })
    return
  }
  selectedProjectImage.value = file.raw
  logStep('image:select', { name: file.name, size: file.size })
}

function handleProjectImageRemove() {
  selectedProjectImage.value = null
}

function isImage(file) {
  return isImageFile(file)
}

async function uploadProjectImage() {
  if (!selectedProjectImage.value) {
    ElMessage.warning('请先选择楼盘图片')
    return
  }
  if (selectedProjectImage.value.size > MAX_UPLOAD_IMAGE_SIZE) {
    ElMessage.warning('图片不能超过 50MB，请压缩后上传')
    return
  }
  projectImageUploading.value = true
  logStep('image:upload:start', {
    fileName: selectedProjectImage.value.name,
    projectId: projectForm.id,
  })
  try {
    projectImage.value = await uploadBusinessFile({
      file: selectedProjectImage.value,
      bizType: 'PROJECT',
      bizId: projectForm.id,
    })
    ElMessage.success('图片上传成功')
    logStep('image:upload:success', projectImage.value)
  } finally {
    projectImageUploading.value = false
  }
}

async function toggleProjectStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  const action = nextStatus === 1 ? '启用' : '停用'
  await ElMessageBox.confirm(`确定${action}楼盘「${row.projectName}」吗？`, `${action}确认`, { type: 'warning' })
  await projectApi.update(row.id, { ...row, status: nextStatus })
  ElMessage.success(`楼盘已${action}`)
  await fetchProjects()
}

async function handleDeleteProject(row) {
  await ElMessageBox.confirm(`确定删除楼盘「${row.projectName}」吗？删除后将从可用楼盘资料中移除。`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  logStep('delete:start', { id: row.id, projectName: row.projectName })
  await projectApi.remove(row.id)
  ElMessage.success('楼盘已删除')
  await fetchProjects()
}

async function openHouseTypes(row) {
  currentProject.value = row
  houseDrawerVisible.value = true
  await fetchHouseTypes(row.id)
}

async function fetchHouseTypes(projectId = currentProject.value?.id) {
  if (!projectId) return
  houseLoading.value = true
  logStep('house-types:start', { projectId })
  try {
    houseTypes.value = await projectApi.houseTypes(projectId)
    logStep('house-types:success', { count: houseTypes.value?.length || 0 })
  } finally {
    houseLoading.value = false
  }
}

function resetHouseForm() {
  Object.assign(houseForm, {
    id: null,
    projectId: currentProject.value?.id || null,
    typeName: '',
    area: null,
    rooms: '',
    totalPriceMin: null,
    totalPriceMax: null,
    sellingPoints: '',
    status: 1,
  })
}

function openCreateHouseType() {
  isHouseEdit.value = false
  resetHouseForm()
  houseDialogVisible.value = true
}

function openEditHouseType(row) {
  isHouseEdit.value = true
  Object.assign(houseForm, {
    id: row.id,
    projectId: row.projectId || currentProject.value?.id,
    typeName: row.typeName || '',
    area: row.area ?? null,
    rooms: row.rooms || '',
    totalPriceMin: row.totalPriceMin ?? null,
    totalPriceMax: row.totalPriceMax ?? null,
    sellingPoints: row.sellingPoints || '',
    status: row.status ?? 1,
  })
  houseDialogVisible.value = true
}

async function handleSubmitHouseType() {
  await houseFormRef.value.validate()
  houseSubmitLoading.value = true
  const payload = cleanPayload(houseForm, ['projectId', 'typeName', 'area', 'rooms', 'totalPriceMin', 'totalPriceMax', 'sellingPoints', 'status'])
  logStep(isHouseEdit.value ? 'house:update:start' : 'house:create:start', payload)
  try {
    if (isHouseEdit.value) {
      await projectApi.updateHouseType(houseForm.id, payload)
      ElMessage.success('户型信息已更新')
    } else {
      await projectApi.createHouseType(payload)
      ElMessage.success('户型创建成功')
    }
    houseDialogVisible.value = false
    await fetchHouseTypes()
  } finally {
    houseSubmitLoading.value = false
  }
}

async function handleDeleteHouseType(row) {
  await ElMessageBox.confirm(`确定删除户型「${row.typeName}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  logStep('house:delete:start', { id: row.id, typeName: row.typeName })
  await projectApi.removeHouseType(row.id)
  ElMessage.success('户型已删除')
  await fetchHouseTypes()
}

onMounted(fetchProjects)
</script>

<style scoped>
.project-name {
  display: grid;
  gap: 2px;
  min-width: 0;
}

.price-text {
  color: #946e10;
  font-weight: 600;
}

.drawer-title {
  color: var(--text-900);
  font-size: 15px;
  font-weight: 600;
}

.drawer-subtitle {
  color: var(--text-400);
  font-size: 12px;
  margin-top: 3px;
}

.house-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.inline-upload {
  display: flex;
  align-items: center;
  gap: 8px;
}

.uploaded-file {
  width: 100%;
  display: flex;
  gap: 10px;
  padding: 10px;
  margin-top: 10px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  background: var(--surface-50);
}

.uploaded-file img {
  width: 64px;
  height: 48px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
}

.uploaded-file-info {
  min-width: 0;
  flex: 1;
}

.field-tip {
  color: var(--text-400);
  font-size: 12px;
  line-height: 1.5;
  margin-top: 8px;
}
</style>
