<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Lock, User } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import type { LoginDTO } from '@/api/types'

/** 左侧品牌面板的能力介绍 */
const FEATURES: string[] = ['原材料批次与临期预警', '货架成品上架、降价与销售', '库存销售数据看板']

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<LoginDTO>({
  username: '',
  password: '',
  rememberMe: false,
})

const rules: FormRules<LoginDTO> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin(): Promise<void> {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch {
    // 错误信息已由拦截器统一提示
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- 品牌展示区 -->
    <section class="brand-panel">
      <div class="brand-inner">
        <div class="brand-logo">🧁</div>
        <h1 class="brand-title">小云烘焙店</h1>
        <p class="brand-sub">库存与货架管理系统</p>
        <ul class="brand-features">
          <li v-for="item in FEATURES" :key="item">
            <span class="dot" />
            {{ item }}
          </li>
        </ul>
      </div>
      <p class="brand-foot">© 2026 小云烘焙店 · 内部管理系统</p>
    </section>

    <!-- 登录表单区 -->
    <section class="form-panel">
      <div class="login-card">
        <div class="card-head">
          <h2>欢迎回来</h2>
          <p>请登录以继续使用管理后台</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" :prefix-icon="User" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              :prefix-icon="Lock"
              type="password"
              placeholder="请输入密码"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="form.rememberMe">记住我（7 天内免登录）</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </section>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  background-color: var(--app-bg);
}

/* ---------- 左侧品牌区 ---------- */
.brand-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex: 1 1 46%;
  padding: 56px 56px 36px;
  color: #fff;
  overflow: hidden;
  background: var(--app-sidebar-bg);
}

/* 装饰性光晕，纯视觉，不影响交互 */
.brand-panel::before,
.brand-panel::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(10px);
}

.brand-panel::before {
  width: 360px;
  height: 360px;
  top: -120px;
  right: -80px;
  background: radial-gradient(circle, rgba(217, 119, 6, 0.55), transparent 70%);
}

.brand-panel::after {
  width: 420px;
  height: 420px;
  bottom: -180px;
  left: -120px;
  background: radial-gradient(circle, rgba(242, 183, 95, 0.28), transparent 70%);
}

.brand-inner {
  position: relative;
  z-index: 1;
  margin-top: 8%;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 62px;
  height: 62px;
  border-radius: 18px;
  font-size: 32px;
  background: linear-gradient(135deg, #f2b75f, #d97706);
  box-shadow: 0 16px 32px -14px rgba(217, 119, 6, 1);
}

.brand-title {
  margin: 22px 0 0;
  font-size: 34px;
  font-weight: 700;
  letter-spacing: 1px;
}

.brand-sub {
  margin: 8px 0 0;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.62);
}

.brand-features {
  margin: 40px 0 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.brand-features li {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.82);
}

.dot {
  width: 7px;
  height: 7px;
  flex: none;
  border-radius: 50%;
  background: #f2b75f;
  box-shadow: 0 0 0 4px rgba(242, 183, 95, 0.16);
}

.brand-foot {
  position: relative;
  z-index: 1;
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
}

/* ---------- 右侧表单区 ---------- */
.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1 1 54%;
  padding: 40px 24px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 36px 34px 26px;
  border-radius: 18px;
  background-color: #fff;
  border: 1px solid var(--app-border);
  box-shadow: 0 24px 60px -34px rgba(36, 26, 17, 0.45);
}

.card-head {
  margin-bottom: 24px;
}

.card-head h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: var(--text-strong);
}

.card-head p {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--text-muted);
}

.login-button {
  width: 100%;
  font-weight: 600;
  letter-spacing: 2px;
}

/* ---------- 小屏适配 ---------- */
@media (max-width: 860px) {
  .brand-panel {
    display: none;
  }
}
</style>
