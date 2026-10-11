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

// ==================== 认证 ====================

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

// ==================== 原材料 ====================

/** 原材料信息 */
export interface MaterialVO {
  id: number
  name: string
  category: string
  unit: string
  shelfLifeDays: number
  storageCondition: string
  createdAt: string
  updatedAt: string
}

/** 原材料新增/修改 */
export interface MaterialDTO {
  name: string
  category?: string
  unit: string
  shelfLifeDays: number
  storageCondition?: string
}

/** 原材料批次库存 */
export interface MaterialBatchVO {
  id: number
  materialId: number
  materialName: string
  unit: string
  productionDate: string
  expiryDate: string
  nearExpiryDays: number
  quantity: number
  status: MaterialStatus
  remainingDays: number
}

/** 入库请求 */
export interface MaterialInboundDTO {
  materialId: number
  quantity: number
  productionDate: string
  nearExpiryDays: number
  supplier?: string
}

/** 出库请求 */
export interface MaterialOutboundDTO {
  batchId: number
  quantity: number
  reason: OutboundReason
}

/** 出库流水 */
export interface OutboundVO {
  id: number
  materialName: string
  batchId: number
  quantity: number
  reason: OutboundReason
  operator: string
  createdAt: string
}

// ==================== 枚举与常量 ====================

/** 原材料批次状态 */
export type MaterialStatus = 'IN_STOCK' | 'NEAR_EXPIRY' | 'EXPIRED'

/** 原材料出库原因 */
export type OutboundReason = 'NORMAL_CONSUME' | 'EXPIRED_DISCARD'

/** 状态中文文案 */
export const MATERIAL_STATUS_TEXT: Record<MaterialStatus, string> = {
  IN_STOCK: '在库',
  NEAR_EXPIRY: '临期',
  EXPIRED: '已过期',
}

/** 状态对应的 tag 颜色 */
export const MATERIAL_STATUS_TAG: Record<MaterialStatus, 'success' | 'warning' | 'danger'> = {
  IN_STOCK: 'success',
  NEAR_EXPIRY: 'warning',
  EXPIRED: 'danger',
}

/** 出库原因中文文案 */
export const OUTBOUND_REASON_TEXT: Record<OutboundReason, string> = {
  NORMAL_CONSUME: '正常烘焙消耗',
  EXPIRED_DISCARD: '过期丢弃',
}
