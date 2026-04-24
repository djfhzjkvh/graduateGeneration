<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">个人信息</h1>
      <p class="page-desc">查看和修改个人账户信息</p>
    </div>

    <div class="profile-card" v-loading="loading">
      <div class="profile-header">
        <div class="profile-avatar">{{ avatarChar }}</div>
        <div>
          <div class="profile-name">{{ userInfo?.nickname || userInfo?.username }}</div>
          <div class="profile-sub">{{ userInfo?.username }} · {{ userInfo?.deptName || '未分配部门' }}</div>
          <div style="margin-top:8px">
            <span :class="['badge', roleBadge]">{{ userInfo?.roleName }}</span>
          </div>
        </div>
      </div>

      <div class="profile-body">
        <el-form ref="formRef" :model="form" label-width="80px" style="max-width:380px">
          <el-form-item label="用户名">
            <el-input :value="userInfo?.username" disabled />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" />
          </el-form-item>
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="form.mobile" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitLoading" @click="handleSave">保存修改</el-button>
            <el-button @click="resetForm">取消</el-button>
          </el-form-item>
        </el-form>

        <el-divider />
        <div style="font-size:13px; color:var(--text-500); padding:4px 0">
          如需修改密码，请联系管理员或等待重置密码接口上线。
          <!-- TODO: 重置密码接口 PUT /api/admin/users/{id}/password/reset 后端待补充 -->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api/user'

const authStore = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const userInfo = computed(() => authStore.userInfo)
const avatarChar = computed(() => (userInfo.value?.nickname || userInfo.value?.username || '管')[0])
const roleBadge = computed(() => {
  const map = { ADMIN: 'badge-purple', MANAGER: 'badge-gold', ADVISOR: 'badge-info' }
  return map[userInfo.value?.roleCode] || 'badge-gray'
})

const form = reactive({ nickname: '', mobile: '' })

function resetForm() {
  form.nickname = userInfo.value?.nickname || ''
  form.mobile   = userInfo.value?.mobile || ''
}

async function handleSave() {
  submitLoading.value = true
  try {
    const updated = await userApi.update(userInfo.value.id, {
      nickname: form.nickname,
      mobile:   form.mobile,
    })
    // 更新本地存储的用户信息
    authStore.setAuth({ token: authStore.token, userInfo: { ...userInfo.value, ...updated } })
    ElMessage.success('个人信息已更新')
  } finally {
    submitLoading.value = false
  }
}

onMounted(resetForm)
</script>
