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

// ==================== 成品 / 货架 ====================

/** 成品信息 */
export interface ProductVO {
  id: number
  name: string
  category: string
  price: number
  shelfLifeDays: number
  createdAt: string
  updatedAt: string
}

/** 成品新增/修改 */
export interface ProductDTO {
  name: string
  category?: string
  price: number
  shelfLifeDays: number
}

/** 成品货架批次 */
export interface ProductBatchVO {
  id: number
  productId: number
  productName: string
  price: number
  productionDate: string
  expiryDate: string
  nearExpiryDays: number
  quantity: number
  status: ShelfStatus
  remainingDays: number
}

/** 上架请求 */
export interface OnShelfDTO {
  productId: number
  quantity: number
  productionDate: string
  nearExpiryDays: number
}

/** 下架请求 */
export interface OffShelfDTO {
  batchId: number
  quantity: number
}

/** 销售请求 */
export interface SaleDTO {
  batchId: number
  quantity: number
  saleType: SaleType
  actualPrice?: number
}

/** 销售记录 */
export interface SaleRecordVO {
  id: number
  productName: string
  quantity: number
  saleType: SaleType
  originalPrice: number
  actualPrice: number
  createdAt: string
}

/** 成品货架状态 */
export type ShelfStatus = 'ON_SALE' | 'DISCOUNT' | 'OFF_SHELF' | 'EXPIRED_OFF'

/** 销售类型 */
export type SaleType = 'NORMAL_SALE' | 'DISCOUNT_SALE'

/** 货架状态中文文案 */
export const SHELF_STATUS_TEXT: Record<ShelfStatus, string> = {
  ON_SALE: '在售',
  DISCOUNT: '临期降价',
  OFF_SHELF: '已下架',
  EXPIRED_OFF: '过期下架',
}

/** 货架状态 tag 颜色 */
export const SHELF_STATUS_TAG: Record<ShelfStatus, 'success' | 'warning' | 'info' | 'danger'> = {
  ON_SALE: 'success',
  DISCOUNT: 'warning',
  OFF_SHELF: 'info',
  EXPIRED_OFF: 'danger',
}

/** 销售类型中文文案 */
export const SALE_TYPE_TEXT: Record<SaleType, string> = {
  NORMAL_SALE: '正常销售',
  DISCOUNT_SALE: '临期降价',
}
