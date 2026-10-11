<script setup lang="ts">
import { computed, onMounted } from 'vue'
import type { Component } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Box, DataBoard, SwitchButton, TakeawayBox } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

/** 侧边栏导航项 */
interface NavItem {
  path: string
  title: string
  icon: Component
}

const NAV_ITEMS: NavItem[] = [
  { path: '/material', title: '原材料管理', icon: Box },
  { path: '/product', title: '货架管理', icon: TakeawayBox },
  { path: '/dashboard', title: '数据看板', icon: DataBoard },
]

/** 每个页面的副标题，用于头部说明 */
const PAGE_DESC: Record<string, string> = {
  material: '维护原材料档案、批次库存与出库流水',
  product: '管理成品档案、货架批次与销售记录',
  dashboard: '实时掌握库存、销售与临期风险',
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const pageTitle = computed(() => route.meta.title ?? '')
const pageDesc = computed(() => PAGE_DESC[route.name as string] ?? '')

const displayName = computed(() => authStore.adminInfo?.realName || authStore.adminInfo?.username || '未登录')
const avatarText = computed(() => displayName.value.slice(0, 1).toUpperCase())

onMounted(() => {
  // 刷新页面后 store 里没有用户信息，重新拉取
  if (!authStore.adminInfo) {
    authStore.fetchAdminInfo()
  }
})

function handleLogout(): void {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="brand">
        <div class="brand-logo">🧁</div>
        <div class="brand-text">
          <span class="brand-name">小云烘焙店</span>
          <span class="brand-sub">库存与货架管理</span>
        </div>
      </div>

      <el-menu :default-active="route.path" router class="nav-menu">
        <el-menu-item v-for="item in NAV_ITEMS" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span class="nav-text">{{ item.title }}</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-foot">v1.0.0</div>
    </el-aside>

    <el-container class="body">
      <el-header class="header">
        <div class="header-title">
          <h2>{{ pageTitle }}</h2>
          <p v-if="pageDesc">{{ pageDesc }}</p>
        </div>
        <div class="header-user">
          <div class="user-chip">
            <div class="user-avatar">{{ avatarText }}</div>
            <div class="user-meta">
              <span class="user-name">{{ displayName }}</span>
              <span class="user-role">管理员</span>
            </div>
          </div>
          <el-button class="logout-btn" :icon="SwitchButton" @click="handleLogout">退出</el-button>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100vh;
}

/* ---------- 侧边栏 ---------- */
.sidebar {
  display: flex;
  flex-direction: column;
  background: var(--app-sidebar-bg);
  overflow: hidden;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 18px 18px;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  flex: none;
  border-radius: 12px;
  font-size: 20px;
  background: linear-gradient(135deg, #f2b75f, #d97706);
  box-shadow: 0 8px 18px -8px rgba(217, 119, 6, 0.9);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.35;
  overflow: hidden;
}

.brand-name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.brand-sub {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.45);
  white-space: nowrap;
}

.nav-menu {
  flex: 1;
  padding: 6px 12px;
  border-right: none;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: rgba(255, 255, 255, 0.7);
  --el-menu-hover-text-color: #fff;
  --el-menu-active-color: #fff;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-menu-item-height: 46px;
}

.nav-menu :deep(.el-menu-item) {
  margin: 4px 0;
  border-radius: 10px;
  font-size: 14px;
}

.nav-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  font-weight: 600;
  background: linear-gradient(90deg, rgba(217, 119, 6, 0.95), rgba(217, 119, 6, 0.35));
  box-shadow: 0 10px 22px -14px rgba(217, 119, 6, 0.95);
}

.sidebar-foot {
  padding: 14px 18px 18px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
}

/* ---------- 主体 ---------- */
.body {
  min-width: 0;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  height: 72px;
  padding: 0 24px;
  background-color: #fff;
  border-bottom: 1px solid var(--app-border);
}

.header-title {
  min-width: 0;
}

.header-title h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-strong);
}

.header-title p {
  margin: 2px 0 0;
  font-size: 12px;
  color: var(--text-muted);
}

.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: none;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 14px 5px 5px;
  border-radius: 999px;
  background-color: var(--app-panel-bg);
  border: 1px solid var(--app-border);
}

.user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #f2b75f, #d97706);
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-strong);
}

.user-role {
  font-size: 11px;
  color: var(--text-muted);
}

.main {
  padding: 20px 24px 28px;
  background-color: var(--app-bg);
  overflow-y: auto;
}

/* ---------- 小屏适配：侧边栏收成图标条 ---------- */
@media (max-width: 900px) {
  .layout .sidebar {
    width: 76px;
  }

  .brand {
    justify-content: center;
    padding: 16px 0;
  }

  .brand-text,
  .nav-text,
  .sidebar-foot {
    display: none;
  }

  .nav-menu {
    padding: 6px 8px;
  }

  .nav-menu :deep(.el-menu-item) {
    justify-content: center;
    padding: 0 !important;
  }

  .header {
    padding: 0 16px;
  }

  .user-meta {
    display: none;
  }
}
</style>
