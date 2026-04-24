<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">竞品管理</h1>
      <p class="page-desc">维护竞品楼盘价格、户型、优惠、优势与短板，为竞品对比和话术生成提供资料</p>
    </div>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="竞品名称"
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
        <span class="table-title">竞品列表</span>
        <span class="table-count">共 {{ total }} 条</span>
        <div class="toolbar-actions">
          <el-button type="primary" size="small" @click="openCreate">+ 新增竞品</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" row-key="id">
        <el-table-column label="竞品楼盘" min-width="185" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="competitor-name">
              <span class="cell-primary cell-ellipsis">{{ row.projectName }}</span>
              <span class="cell-muted">{{ row.region || '未填写区域' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="均价" width="130">
          <template #default="{ row }">
            <span class="price-text">{{ formatPrice(row.avgPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="户型信息" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.houseTypes || '-' }}</template>
        </el-table-column>
        <el-table-column label="核心优势" min-width="190" show-overflow-tooltip>
          <template #default="{ row }">{{ row.highlights || '-' }}</template>
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
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link @click="openDetail(row)">详情</el-button>
            <el-button size="small" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" link :type="row.status === 1 ? 'warning' : 'primary'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑竞品' : '新增竞品'"
      width="700px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="竞品名称" prop="projectName">
              <el-input v-model="form.projectName" maxlength="40" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域">
              <el-input v-model="form.region" placeholder="例如：滨江区" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="均价">
              <el-input-number v-model="form.avgPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交付日期">
              <el-date-picker v-model="form.handoverDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio-button :value="1">启用</el-radio-button>
                <el-radio-button :value="0">停用</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="户型信息">
          <el-input v-model="form.houseTypes" type="textarea" :rows="2" maxlength="180" show-word-limit placeholder="例如：89方三房、118方四房" />
        </el-form-item>
        <el-form-item label="优惠信息">
          <el-input v-model="form.discountInfo" type="textarea" :rows="2" maxlength="180" show-word-limit />
        </el-form-item>
        <el-form-item label="核心优势">
          <el-input v-model="form.highlights" type="textarea" :rows="3" maxlength="240" show-word-limit />
        </el-form-item>
        <el-form-item label="主要短板">
          <el-input v-model="form.weakness" type="textarea" :rows="3" maxlength="240" show-word-limit />
        </el-form-item>
        <el-form-item label="竞品图片">
          <div class="inline-upload">
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :limit="1"
              accept="image/*"
              :on-change="handleImageChange"
              :on-remove="handleImageRemove"
            >
              <el-button size="small">选择图片</el-button>
            </el-upload>
            <el-button size="small" type="primary" :loading="imageUploading" @click="uploadImage">
              上传图片
            </el-button>
          </div>
          <div v-if="uploadedImage" class="uploaded-file">
            <img v-if="isImage(uploadedImage)" :src="uploadedImage.fileUrl" :alt="uploadedImage.fileName" />
            <div class="uploaded-file-info">
              <div class="cell-primary">{{ uploadedImage.fileName }}</div>
              <div class="cell-muted cell-ellipsis">{{ uploadedImage.fileUrl }}</div>
            </div>
          </div>
          <div class="field-tip">后端竞品表暂未提供图片字段，当前图片会先作为附件上传并返回文件地址。</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '确认新增' }}
        </el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="竞品详情" size="480px" direction="rtl">
      <div v-if="currentItem" class="detail-panel">
        <div class="detail-head">
          <div>
            <div class="detail-name">{{ currentItem.projectName }}</div>
            <div class="detail-meta">{{ currentItem.region || '未填写区域' }} · {{ formatPrice(currentItem.avgPrice) }}</div>
          </div>
          <span :class="['badge', currentItem.status === 1 ? 'badge-success' : 'badge-gray']">
            {{ currentItem.status === 1 ? '启用' : '停用' }}
          </span>
        </div>

        <div v-for="item in detailRows" :key="item.label" class="detail-row">
          <span class="detail-label">{{ item.label }}</span>
          <span class="detail-value">{{ item.value || '-' }}</span>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { competitorApi } from '@/api/competitor'
import { formatPrice } from '@/utils/format'
import { cleanPayload as cleanPayloadBase } from '@/utils/payload'
import { logBusiness } from '@/utils/logger'
import { isImageFile, uploadBusinessFile } from '@/utils/upload'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const uploadRef = ref()
const currentItem = ref(null)
const selectedImage = ref(null)
const uploadedImage = ref(null)
const imageUploading = ref(false)
const MAX_UPLOAD_IMAGE_SIZE = 50 * 1024 * 1024

const filters = reactive({ keyword: '', region: '', status: null })

const form = reactive({
  id: null,
  projectName: '',
  region: '',
  avgPrice: null,
  discountInfo: '',
  houseTypes: '',
  handoverDate: '',
  highlights: '',
  weakness: '',
  status: 1,
})

const rules = {
  projectName: [{ required: true, message: '请输入竞品楼盘名称', trigger: 'blur' }],
}

const detailRows = computed(() => {
  const item = currentItem.value
  if (!item) return []
  return [
    { label: '交付日期', value: item.handoverDate },
    { label: '户型信息', value: item.houseTypes },
    { label: '优惠信息', value: item.discountInfo },
    { label: '核心优势', value: item.highlights },
    { label: '主要短板', value: item.weakness },
  ]
})

function logStep(action, payload = {}) {
  logBusiness('competitors', action, payload)
}

function cleanPayload(source) {
  const payload = cleanPayloadBase(source, ['projectName', 'region', 'avgPrice', 'discountInfo', 'houseTypes', 'handoverDate', 'highlights', 'weakness', 'status'])
  payload.projectName = payload.projectName?.trim()
  return payload
}

async function fetchData() {
  loading.value = true
  const params = { pageNum: currentPage.value, pageSize: pageSize.value, ...filters }
  try {
    const data = await competitorApi.page(params)
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchData()
}

function handleReset() {
  Object.assign(filters, { keyword: '', region: '', status: null })
  handleSearch()
}

function resetForm() {
  Object.assign(form, {
    id: null,
    projectName: '',
    region: '',
    avgPrice: null,
    discountInfo: '',
    houseTypes: '',
    handoverDate: '',
    highlights: '',
    weakness: '',
    status: 1,
  })
  selectedImage.value = null
  uploadedImage.value = null
  uploadRef.value?.clearFiles()
}

function openCreate() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  const data = await competitorApi.detail(row.id)
  Object.assign(form, {
    id: data.id,
    projectName: data.projectName || '',
    region: data.region || '',
    avgPrice: data.avgPrice ?? null,
    discountInfo: data.discountInfo || '',
    houseTypes: data.houseTypes || '',
    handoverDate: data.handoverDate || '',
    highlights: data.highlights || '',
    weakness: data.weakness || '',
    status: data.status ?? 1,
  })
  dialogVisible.value = true
}

async function openDetail(row) {
  currentItem.value = await competitorApi.detail(row.id)
  detailVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  const payload = cleanPayload(form)
  logStep(isEdit.value ? 'update:start' : 'create:start', payload)
  try {
    if (isEdit.value) {
      await competitorApi.update(form.id, payload)
      ElMessage.success('竞品信息已更新')
    } else {
      await competitorApi.create(payload)
      ElMessage.success('竞品创建成功')
    }
    dialogVisible.value = false
    await fetchData()
  } finally {
    submitLoading.value = false
  }
}

function handleImageChange(file) {
  if (file.size > MAX_UPLOAD_IMAGE_SIZE) {
    ElMessage.warning('图片不能超过 50MB，请压缩后上传')
    uploadRef.value?.clearFiles()
    selectedImage.value = null
    logStep('image:select:oversize', { name: file.name, size: file.size })
    return
  }
  selectedImage.value = file.raw
  logStep('image:select', { name: file.name, size: file.size })
}

function handleImageRemove() {
  selectedImage.value = null
}

function isImage(file) {
  return isImageFile(file)
}

async function uploadImage() {
  if (!selectedImage.value) {
    ElMessage.warning('请先选择竞品图片')
    return
  }
  if (selectedImage.value.size > MAX_UPLOAD_IMAGE_SIZE) {
    ElMessage.warning('图片不能超过 50MB，请压缩后上传')
    return
  }
  imageUploading.value = true
  logStep('image:upload:start', {
    fileName: selectedImage.value.name,
    competitorId: form.id,
  })
  try {
    uploadedImage.value = await uploadBusinessFile({
      file: selectedImage.value,
      bizType: 'COMPETITOR_PROJECT',
      bizId: form.id,
    })
    ElMessage.success('图片上传成功')
    logStep('image:upload:success', uploadedImage.value)
  } finally {
    imageUploading.value = false
  }
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  const action = nextStatus === 1 ? '启用' : '停用'
  await ElMessageBox.confirm(`确定${action}竞品「${row.projectName}」吗？`, `${action}确认`, { type: 'warning' })
  await competitorApi.update(row.id, { ...row, status: nextStatus })
  ElMessage.success(`竞品已${action}`)
  await fetchData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除竞品「${row.projectName}」吗？删除后将从可用竞品资料中移除。`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  logStep('delete:start', { id: row.id, projectName: row.projectName })
  await competitorApi.remove(row.id)
  ElMessage.success('竞品已删除')
  await fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.competitor-name {
  display: grid;
  gap: 2px;
  min-width: 0;
}

.price-text {
  color: #946e10;
  font-weight: 600;
}

.detail-panel {
  font-size: 13px;
}

.detail-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 16px;
  margin-bottom: 4px;
  border-bottom: 1px solid var(--border-light);
}

.detail-name {
  color: var(--text-900);
  font-size: 17px;
  font-weight: 600;
}

.detail-meta {
  color: var(--text-400);
  font-size: 12px;
  margin-top: 4px;
}

.detail-value {
  line-height: 1.7;
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
