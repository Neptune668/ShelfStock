import http from './http'
import type { AdminInfoVO, LoginDTO, LoginVO, Result } from './types'

/** 登录 */
export function login(data: LoginDTO): Promise<Result<LoginVO>> {
  return http.post<Result<LoginVO>>('/auth/login', data).then((res) => res.data)
}

/** 获取当前登录管理员信息 */
export function getAdminInfo(): Promise<Result<AdminInfoVO>> {
  return http.get<Result<AdminInfoVO>>('/auth/info').then((res) => res.data)
}

/** 登出 */
export function logout(): Promise<Result<void>> {
  return http.post<Result<void>>('/auth/logout').then((res) => res.data)
}
