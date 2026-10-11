import http from './http'
import type {
  MaterialBatchVO,
  MaterialDTO,
  MaterialInboundDTO,
  MaterialOutboundDTO,
  MaterialVO,
  OutboundVO,
  PageResult,
  Result,
} from './types'

/** 分页查询原材料 */
export function pageMaterial(params: {
  pageNum: number
  pageSize: number
  name?: string
  category?: string
}): Promise<Result<PageResult<MaterialVO>>> {
  return http.get<Result<PageResult<MaterialVO>>>('/material/page', { params }).then((res) => res.data)
}

/** 新增原材料 */
export function addMaterial(data: MaterialDTO): Promise<Result<void>> {
  return http.post<Result<void>>('/material', data).then((res) => res.data)
}

/** 修改原材料 */
export function updateMaterial(id: number, data: MaterialDTO): Promise<Result<void>> {
  return http.put<Result<void>>(`/material/${id}`, data).then((res) => res.data)
}

/** 删除原材料 */
export function deleteMaterial(id: number): Promise<Result<void>> {
  return http.delete<Result<void>>(`/material/${id}`).then((res) => res.data)
}

/** 批次库存分页查询 */
export function pageMaterialBatch(params: {
  pageNum: number
  pageSize: number
  materialId?: number
  status?: string
  keyword?: string
}): Promise<Result<PageResult<MaterialBatchVO>>> {
  return http.get<Result<PageResult<MaterialBatchVO>>>('/material/batch/page', { params }).then((res) => res.data)
}

/** 原材料入库 */
export function inbound(data: MaterialInboundDTO): Promise<Result<void>> {
  return http.post<Result<void>>('/material/inbound', data).then((res) => res.data)
}

/** 原材料出库 */
export function outbound(data: MaterialOutboundDTO): Promise<Result<void>> {
  return http.post<Result<void>>('/material/outbound', data).then((res) => res.data)
}

/** 出库流水查询 */
export function pageOutbound(params: {
  pageNum: number
  pageSize: number
  reason?: string
  startDate?: string
  endDate?: string
}): Promise<Result<PageResult<OutboundVO>>> {
  return http.get<Result<PageResult<OutboundVO>>>('/material/outbound/page', { params }).then((res) => res.data)
}
