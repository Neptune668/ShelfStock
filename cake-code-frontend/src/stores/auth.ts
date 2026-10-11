import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAdminInfo as getAdminInfoApi, login as loginApi } from '@/api/auth'
import type { AdminInfoVO, LoginDTO } from '@/api/types'

/**
 * 认证状态：token 与当前登录管理员信息
 */
export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem('token') ?? '')
  const adminInfo = ref<AdminInfoVO | null>(null)

  async function login(dto: LoginDTO): Promise<void> {
    const result = await loginApi(dto)
    token.value = result.data.token
    localStorage.setItem('token', result.data.token)
    adminInfo.value = {
      adminId: result.data.adminId,
      username: result.data.username,
      realName: result.data.realName,
    }
  }

  async function fetchAdminInfo(): Promise<void> {
    const result = await getAdminInfoApi()
    adminInfo.value = result.data
  }

  function logout(): void {
    token.value = ''
    localStorage.removeItem('token')
    adminInfo.value = null
  }

  return { token, adminInfo, login, fetchAdminInfo, logout }
})
