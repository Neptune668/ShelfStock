import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    requiresAuth?: boolean
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/material',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'material',
        name: 'material',
        component: () => import('@/views/MaterialView.vue'),
        meta: { title: '原材料管理' },
      },
      {
        path: 'product',
        name: 'product',
        component: () => import('@/views/ProductView.vue'),
        meta: { title: '货架管理' },
      },
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: '数据看板' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫：未登录跳转登录页，已登录访问登录页跳首页
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.matched.some((record) => record.meta.requiresAuth) && !token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && token) {
    return { path: '/' }
  }
  return true
})

// 动态设置页面标题
router.afterEach((to) => {
  const base = '小云烘焙店库存与货架管理系统'
  document.title = to.meta.title ? `${to.meta.title} - ${base}` : base
})

export default router
