// 接口数据类型定义，统一在此维护

/** 统一返回结构 */
export interface Result<T> {
  code: number
  message: string
  data: T
}

/** 分页返回结构 */
export interface PageResult<T> {
  total: number
  list: T[]
}

/** 登录请求参数 */
export interface LoginDTO {
  username: string
  password: string
  rememberMe?: boolean
}

/** 登录返回结果 */
export interface LoginVO {
  token: string
  adminId: number
  username: string
  realName: string
}

/** 当前登录管理员信息 */
export interface AdminInfoVO {
  adminId: number
  username: string
  realName: string
}
