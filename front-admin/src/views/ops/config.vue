<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">系统配置</h1>
      <p class="page-desc">维护系统参数与 AI 模型配置，调整后请观察日志中心确认调用状态</p>
    </div>

    <el-tabs v-model="activeTab" class="ops-tabs">
      <el-tab-pane label="系统参数" name="system">
        <div class="filter-card">
          <el-form inline>
            <el-form-item>
              <el-input
                v-model="configKey"
                placeholder="配置 Key"
                clearable
                style="width: 220px"
                @keyup.enter="fetchConfigs"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="fetchConfigs">查询</el-button>
              <el-button @click="handleConfigReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="table-card">
          <div class="table-toolbar">
            <span class="table-title">系统参数列表</span>
            <span class="table-count">共 {{ configs.length }} 条</span>
          </div>
          <el-table :data="configs" v-loading="configLoading" row-key="configKey">
            <el-table-column prop="configKey" label="配置 Key" min-width="170" show-overflow-tooltip />
            <el-table-column prop="configName" label="配置名称" min-width="150" show-overflow-tooltip />
            <el-table-column label="配置值" min-width="190" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="cell-mono">{{ row.configValue }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="说明" min-width="190" show-overflow-tooltip />
            <el-table-column label="更新时间" width="165">
              <template #default="{ row }">{{ formatDate(row.updatedAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="90" fixed="right">
              <template #default="{ row }">
                <el-button size="small" link @click="openConfigEdit(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="AI 配置" name="ai">
        <div class="config-layout">
          <div class="table-card">
            <div class="table-toolbar">
              <span class="table-title">当前 AI 模型</span>
              <span :class="['badge', aiForm.status === 1 ? 'badge-success' : 'badge-gray']">
                {{ aiForm.status === 1 ? '启用' : '停用' }}
              </span>
            </div>
            <div class="config-form-body" v-loading="aiLoading">
              <el-form ref="aiFormRef" :model="aiForm" :rules="aiRules" label-width="96px">
                <el-row :gutter="16">
                  <el-col :span="12">
                    <el-form-item label="供应商" prop="providerName">
                      <el-input v-model="aiForm.providerName" placeholder="例如：Qwen" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="模型名称" prop="modelName">
                      <el-input v-model="aiForm.modelName" placeholder="例如：qwen-plus" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item label="接口地址" prop="apiUrl">
                  <el-input v-model="aiForm.apiUrl" />
                </el-form-item>
                <el-row :gutter="16">
                  <el-col :span="12">
                    <el-form-item label="API Key">
                      <el-input v-model="aiForm.apiKey" type="password" show-password placeholder="留空则保留原密钥" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="密钥状态">
                      <span :class="['badge', aiForm.apiKeyConfigured ? 'badge-success' : 'badge-warning']">
                        {{ aiForm.apiKeyConfigured ? '已配置' : '未配置' }}
                      </span>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="16">
                  <el-col :span="12">
                    <el-form-item label="温度">
                      <el-input-number v-model="aiForm.temperature" :min="0" :max="2" :step="0.1" :precision="2" controls-position="right" style="width: 100%" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="状态">
                      <el-radio-group v-model="aiForm.status">
                        <el-radio-button :value="1">启用</el-radio-button>
                        <el-radio-button :value="0">停用</el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item label="更新时间">
                  <span class="cell-muted">{{ formatDate(aiForm.updatedAt) }}</span>
                </el-form-item>
              </el-form>
            </div>
            <div class="config-actions">
              <el-button @click="fetchAiConfig">刷新</el-button>
              <el-button type="primary" :loading="aiSubmitLoading" @click="submitAiConfig">保存 AI 配置</el-button>
            </div>
          </div>

          <div class="ops-note">
            <div class="ops-note-title">调试提示</div>
            <p>API Key 留空时，后端会保留原密钥，不会覆盖为空。</p>
            <p>修改模型或接口地址后，可以到“日志中心”查看 AI 调用是否成功。</p>
            <p>温度建议保持在 0.2 到 0.8 之间，便于业务话术稳定输出。</p>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="configDialogVisible" title="编辑系统参数" width="520px" destroy-on-close>
      <el-form ref="configFormRef" :model="configForm" :rules="configRules" label-width="88px">
        <el-form-item label="配置 Key">
          <el-input v-model="configForm.configKey" disabled />
        </el-form-item>
        <el-form-item label="配置名称">
          <el-input v-model="configForm.configName" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="configForm.configValue" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="configForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="configSubmitLoading" @click="submitConfig">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { opsApi } from '@/api/ops'
import { formatDate } from '@/utils/format'
import { logBusiness } from '@/utils/logger'

const activeTab = ref('system')
const configKey = ref('')
const configs = ref([])
const configLoading = ref(false)
const configSubmitLoading = ref(false)
const configDialogVisible = ref(false)
const configFormRef = ref()
const aiFormRef = ref()
const aiLoading = ref(false)
const aiSubmitLoading = ref(false)

const configForm = reactive({ configKey: '', configValue: '', configName: '', remark: '' })
const aiForm = reactive({
  providerName: '',
  modelName: '',
  apiUrl: '',
  apiKey: '',
  apiKeyConfigured: false,
  temperature: 0.7,
  status: 1,
  updatedAt: '',
})

const configRules = {
  configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }],
}

const aiRules = {
  providerName: [{ required: true, message: '请输入模型供应商', trigger: 'blur' }],
  modelName: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
  apiUrl: [{ required: true, message: '请输入接口地址', trigger: 'blur' }],
}

function logStep(action, payload = {}) {
  logBusiness('ops-config', action, payload)
}

async function fetchConfigs() {
  configLoading.value = true
  const params = configKey.value ? { configKey: configKey.value } : {}
  try {
    configs.value = await opsApi.configs(params)
  } finally {
    configLoading.value = false
  }
}

function handleConfigReset() {
  configKey.value = ''
  fetchConfigs()
}

function openConfigEdit(row) {
  Object.assign(configForm, {
    configKey: row.configKey,
    configValue: row.configValue || '',
    configName: row.configName || '',
    remark: row.remark || '',
  })
  configDialogVisible.value = true
}

async function submitConfig() {
  await configFormRef.value.validate()
  configSubmitLoading.value = true
  const payload = {
    configValue: configForm.configValue,
    configName: configForm.configName,
    remark: configForm.remark,
  }
  logStep('config:update:start', { key: configForm.configKey, ...payload })
  try {
    await opsApi.updateConfig(configForm.configKey, payload)
    ElMessage.success('系统参数已更新')
    configDialogVisible.value = false
    await fetchConfigs()
  } finally {
    configSubmitLoading.value = false
  }
}

async function fetchAiConfig() {
  aiLoading.value = true
  try {
    const data = await opsApi.aiConfig()
    Object.assign(aiForm, {
      providerName: data.providerName || '',
      modelName: data.modelName || '',
      apiUrl: data.apiUrl || '',
      apiKey: '',
      apiKeyConfigured: !!data.apiKeyConfigured,
      temperature: data.temperature ?? 0.7,
      status: data.status ?? 1,
      updatedAt: data.updatedAt || '',
    })
  } finally {
    aiLoading.value = false
  }
}

async function submitAiConfig() {
  await aiFormRef.value.validate()
  aiSubmitLoading.value = true
  const payload = {
    providerName: aiForm.providerName,
    modelName: aiForm.modelName,
    apiUrl: aiForm.apiUrl,
    apiKey: aiForm.apiKey,
    temperature: aiForm.temperature,
    status: aiForm.status,
  }
  logStep('ai-config:update:start', { ...payload, apiKey: payload.apiKey ? '[set]' : '[keep]' })
  try {
    await opsApi.updateAiConfig(payload)
    ElMessage.success('AI 配置已更新')
    await fetchAiConfig()
  } finally {
    aiSubmitLoading.value = false
  }
}

onMounted(() => {
  fetchConfigs()
  fetchAiConfig()
})
</script>

<style scoped>
.ops-tabs :deep(.el-tabs__header) {
  margin-bottom: 14px;
}

.config-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 14px;
  align-items: start;
}

.config-form-body {
  padding: 18px 20px 4px;
}

.config-actions {
  padding: 14px 20px;
  border-top: 1px solid var(--border-light);
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.ops-note {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  padding: 16px 18px;
  color: var(--text-500);
  font-size: 13px;
}

.ops-note-title {
  color: var(--text-900);
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 10px;
}

.ops-note p + p {
  margin-top: 8px;
}

@media (max-width: 1100px) {
  .config-layout {
    grid-template-columns: 1fr;
  }
}
</style>
