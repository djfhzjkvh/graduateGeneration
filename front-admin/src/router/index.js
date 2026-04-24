import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard',              component: () => import('@/views/dashboard/index.vue'),        meta: { title: '数据概览', parent: '工作台'   } },
      { path: 'customer/list',          component: () => import('@/views/customer/list.vue'),           meta: { title: '客户列表', parent: '客户中心' } },
      { path: 'customer/high-intent',   component: () => import('@/views/customer/high-intent.vue'),   meta: { title: '高意向客户', parent: '客户中心' } },
      { path: 'customer/tags',          component: () => import('@/views/customer/tags.vue'),          meta: { title: '客户标签', parent: '客户中心' } },
      { path: 'project/list',           component: () => import('@/views/project/list.vue'),            meta: { title: '楼盘管理', parent: '项目管理' } },
      { path: 'project/competitors',    component: () => import('@/views/project/competitors.vue'),     meta: { title: '竞品管理', parent: '项目管理' } },
      { path: 'task/list',              component: () => import('@/views/task/list.vue'),               meta: { title: '任务列表', parent: '跟进任务' } },
      { path: 'task/today',             component: () => import('@/views/task/today.vue'),              meta: { title: '今日任务', parent: '跟进任务' } },
      { path: 'task/overdue',           component: () => import('@/views/task/overdue.vue'),            meta: { title: '逾期任务', parent: '跟进任务' } },
      { path: 'system/user',            component: () => import('@/views/system/user/index.vue'),       meta: { title: '用户管理', parent: '组织管理' } },
      { path: 'system/role',            component: () => import('@/views/system/role/index.vue'),       meta: { title: '角色管理', parent: '组织管理' } },
      { path: 'system/dept',            component: () => import('@/views/system/dept/index.vue'),       meta: { title: '部门管理', parent: '组织管理' } },
      { path: 'profile',                component: () => import('@/views/profile/index.vue'),           meta: { title: '个人信息', parent: '系统'     } },
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('admin_token')
  if (!to.meta.public && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
