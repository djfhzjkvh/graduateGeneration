<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-logo">
        <div class="logo-mark">购</div>
        <div>
          <div class="logo-title">购房 CRM</div>
          <div class="logo-sub">客户意向跟进提醒系统</div>
        </div>
      </div>

      <h2 class="login-heading">欢迎回来</h2>
      <p class="login-sub">请使用管理员账号登录后台管理端</p>

      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <label class="field-label">用户名</label>
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <label class="field-label">密码</label>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>
        <el-button
          type="primary" size="large" :loading="loading"
          style="width:100%; margin-top:8px; height:46px; font-size:15px; font-weight:600; letter-spacing:.3px;"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码',   trigger: 'blur' }],
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await authApi.login({ username: form.username, password: form.password })
    authStore.setAuth(data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: var(--navy-900);
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
}
.login-page::before {
  content: '';
  position: absolute; inset: 0;
  background:
    radial-gradient(ellipse 80% 60% at 20% 80%, rgba(201,168,76,.12) 0%, transparent 60%),
    radial-gradient(ellipse 60% 80% at 80% 20%, rgba(30,45,74,.6) 0%, transparent 60%);
}
.login-page::after {
  content: '';
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(201,168,76,.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(201,168,76,.04) 1px, transparent 1px);
  background-size: 40px 40px;
}

.login-card {
  position: relative; z-index: 1;
  background: var(--navy-800);
  border: 1px solid rgba(201,168,76,.15);
  border-radius: var(--radius-lg);
  padding: 48px; width: 420px;
  box-shadow: 0 24px 60px rgba(0,0,0,.4), 0 0 0 1px rgba(255,255,255,.03);
}

.login-logo {
  display: flex; align-items: center; gap: 12px; margin-bottom: 36px;
}
.logo-mark {
  width: 44px; height: 44px;
  background: linear-gradient(135deg, var(--gold-500), var(--gold-300));
  border-radius: 10px; display: flex; align-items: center; justify-content: center;
  font-size: 18px; font-weight: 700; color: var(--navy-900); flex-shrink: 0;
}
.logo-title { font-size: 17px; font-weight: 600; color: #fff; letter-spacing: .5px; }
.logo-sub   { font-size: 11px; color: var(--text-400); letter-spacing: .5px; margin-top: 1px; }

.login-heading { font-size: 24px; font-weight: 600; color: #fff; margin-bottom: 8px; }
.login-sub { font-size: 13px; color: var(--text-400); margin-bottom: 28px; }

.field-label {
  display: block; font-size: 11px; font-weight: 500;
  color: var(--text-400); text-transform: uppercase; letter-spacing: .8px;
  margin-bottom: 7px;
}

/* Override El Input for dark background */
:deep(.el-input__wrapper) {
  background: rgba(255,255,255,.05) !important;
  box-shadow: 0 0 0 1px rgba(255,255,255,.1) inset !important;
  border-radius: var(--radius-sm) !important;
}
:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(255,255,255,.2) inset !important;
}
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--gold-500) inset !important;
}
:deep(.el-input__inner) { color: #fff; font-size: 14px; }
:deep(.el-input__inner::placeholder) { color: rgba(255,255,255,.25); }
:deep(.el-input__suffix .el-icon) { color: rgba(255,255,255,.4); }
:deep(.el-form-item) { margin-bottom: 18px; }
:deep(.el-form-item__error) { color: var(--gold-400); }
</style>
