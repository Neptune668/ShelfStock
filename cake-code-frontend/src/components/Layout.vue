<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

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
    <el-aside width="200px" class="aside">
      <div class="logo">小云烘焙店</div>
      <el-menu :default-active="route.path" router class="menu">
        <el-menu-item index="/material">原材料管理</el-menu-item>
        <el-menu-item index="/product">货架管理</el-menu-item>
        <el-menu-item index="/dashboard">数据看板</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="user">{{ authStore.adminInfo?.realName || authStore.adminInfo?.username }}</span>
        <el-button size="small" @click="handleLogout">退出登录</el-button>
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

.aside {
  background-color: #fff;
  border-right: 1px solid #e5e4e7;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #303133;
}

.menu {
  border-right: none;
}

.header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  background-color: #fff;
  border-bottom: 1px solid #e5e4e7;
}

.main {
  background-color: #f5f7fa;
}
</style>
