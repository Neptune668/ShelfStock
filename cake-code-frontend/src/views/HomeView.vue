<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
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
  <div class="home-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>首页</span>
          <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </template>
      <p>欢迎，{{ authStore.adminInfo?.realName || authStore.adminInfo?.username || '管理员' }}！</p>
      <p>登录模块已就绪，原材料、货架、看板等模块将在这里展开。</p>
    </el-card>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
