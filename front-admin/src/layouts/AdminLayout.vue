<template>
  <div class="admin-layout">
    <!-- ── Sidebar ── -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="sidebar-logo">
          <div class="sidebar-logo-mark">购</div>
          <div>
            <div class="sidebar-logo-name">购房 CRM</div>
            <div class="sidebar-logo-sub">ADMIN PANEL</div>
          </div>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section">
          <div class="nav-section-label">工作台</div>
          <router-link to="/dashboard" class="nav-item" active-class="active">
            <span class="nav-icon">⊞</span>数据概览
          </router-link>
        </div>

        <div class="nav-section">
          <div class="nav-section-label">客户中心</div>
          <router-link to="/customer/list" class="nav-item" active-class="active">
            <span class="nav-icon">👥</span>客户列表
          </router-link>
          <router-link to="/customer/high-intent" class="nav-item" active-class="active">
            <span class="nav-icon">⭐</span>高意向客户
          </router-link>
        </div>

        <div class="nav-section">
          <div class="nav-section-label">跟进任务</div>
          <router-link to="/task/list" class="nav-item" active-class="active">
            <span class="nav-icon">☑</span>任务列表
          </router-link>
          <router-link to="/task/today" class="nav-item" active-class="active">
            <span class="nav-icon">◷</span>今日任务
          </router-link>
          <router-link to="/task/overdue" class="nav-item" active-class="active">
            <span class="nav-icon">⚠</span>逾期任务
          </router-link>
        </div>

        <div class="nav-section">
          <div class="nav-section-label">组织管理</div>
          <router-link to="/system/user" class="nav-item" active-class="active">
            <span class="nav-icon">👤</span>用户管理
          </router-link>
          <router-link to="/system/role" class="nav-item" active-class="active">
            <span class="nav-icon">🔑</span>角色管理
          </router-link>
          <router-link to="/system/dept" class="nav-item" active-class="active">
            <span class="nav-icon">🏢</span>部门管理
          </router-link>
        </div>

        <div class="nav-section">
          <div class="nav-section-label">系统</div>
          <router-link to="/profile" class="nav-item" active-class="active">
            <span class="nav-icon">⚙</span>个人信息
          </router-link>
          <div class="nav-item" @click="handleLogout">
            <span class="nav-icon">⏏</span>退出登录
          </div>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="sidebar-user">
          <div class="s-avatar">{{ avatarChar }}</div>
          <div>
            <div class="s-name">{{ authStore.username }}</div>
            <div class="s-role">{{ roleName }}</div>
          </div>
        </div>
      </div>
    </aside>

    <!-- ── Main ── -->
    <div class="main-content">
      <header class="header">
        <div class="breadcrumb">
          <span>后台管理</span>
          <span class="sep">›</span>
          <span>{{ currentParent }}</span>
          <span class="sep">›</span>
          <span class="current">{{ currentTitle }}</span>
        </div>
        <div class="header-actions">
          <el-icon :size="16" color="var(--text-500)" style="cursor:pointer"><Bell /></el-icon>
          <div class="h-avatar">{{ avatarChar }}</div>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const currentTitle  = computed(() => route.meta.title  || '')
const currentParent = computed(() => route.meta.parent || '')
const avatarChar    = computed(() => (authStore.username || '管')[0])
const roleName      = computed(() => {
  const map = { ADMIN: '管理员', MANAGER: '销售经理', ADVISOR: '销售顾问' }
  return map[authStore.roleCode] || authStore.roleCode
})

async function handleLogout() {
  await ElMessageBox.confirm('确定退出登录吗？', '退出确认', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning',
  })
  try { await authApi.logout() } catch { /* ignore */ }
  authStore.clearAuth()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

/* ── Sidebar ── */
.sidebar {
  width: 240px;
  min-height: 100vh;
  background: var(--navy-900);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0; top: 0; bottom: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 22px 18px 18px;
  border-bottom: 1px solid rgba(255,255,255,.06);
}
.sidebar-logo { display: flex; align-items: center; gap: 10px; }
.sidebar-logo-mark {
  width: 34px; height: 34px;
  background: linear-gradient(135deg, var(--gold-500), var(--gold-300));
  border-radius: 8px; display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: var(--navy-900); flex-shrink: 0;
}
.sidebar-logo-name { font-size: 14px; font-weight: 600; color: #fff; letter-spacing: .3px; }
.sidebar-logo-sub  { font-size: 10px; color: var(--text-400); letter-spacing: .5px; }

.sidebar-nav {
  flex: 1; padding: 14px 10px; overflow-y: auto;
}
.sidebar-nav::-webkit-scrollbar { width: 0; }

.nav-section { margin-bottom: 22px; }
.nav-section-label {
  font-size: 10px; font-weight: 600; text-transform: uppercase;
  letter-spacing: 1px; color: rgba(255,255,255,.25);
  padding: 0 8px; margin-bottom: 5px;
}
.nav-item {
  display: flex; align-items: center; gap: 9px;
  padding: 8px 10px; border-radius: var(--radius-sm);
  cursor: pointer; color: rgba(255,255,255,.52);
  font-size: 13px; font-weight: 400;
  text-decoration: none; user-select: none;
  transition: background .15s, color .15s;
}
.nav-item:hover { background: rgba(255,255,255,.06); color: rgba(255,255,255,.85); }
.nav-item.active { background: rgba(201,168,76,.15); color: var(--gold-400); }

.nav-icon { width: 17px; display: flex; align-items: center; justify-content: center; font-size: 14px; flex-shrink: 0; }

.sidebar-footer {
  padding: 14px 10px;
  border-top: 1px solid rgba(255,255,255,.06);
}
.sidebar-user {
  display: flex; align-items: center; gap: 10px;
  padding: 9px 10px; border-radius: var(--radius-sm);
  cursor: pointer; transition: background .15s;
}
.sidebar-user:hover { background: rgba(255,255,255,.06); }
.s-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, var(--gold-500), var(--navy-600));
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600; color: #fff; flex-shrink: 0;
}
.s-name { font-size: 12.5px; font-weight: 500; color: #fff; }
.s-role { font-size: 11px; color: var(--text-400); }

/* ── Main ── */
.main-content {
  margin-left: 240px;
  flex: 1; display: flex; flex-direction: column; min-height: 100vh;
}

.header {
  height: 58px; background: var(--white);
  border-bottom: 1px solid var(--border);
  display: flex; align-items: center;
  padding: 0 24px; gap: 16px;
  position: sticky; top: 0; z-index: 50;
  box-shadow: var(--shadow-sm);
}
.breadcrumb {
  display: flex; align-items: center; gap: 6px;
  flex: 1; font-size: 13px; color: var(--text-500);
}
.sep { color: var(--text-400); }
.current { color: var(--text-700); font-weight: 500; }
.header-actions {
  display: flex; align-items: center; gap: 12px;
}
.h-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, var(--gold-500), var(--navy-600));
  display: flex; align-items: center; justify-content: center;
  font-size: 11px; font-weight: 600; color: #fff; cursor: pointer;
}

.content { flex: 1; padding: 22px 24px; }
</style>
