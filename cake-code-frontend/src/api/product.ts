import http from './http'
import type {
  OffShelfDTO,
  OnShelfDTO,
  PageResult,
  ProductBatchVO,
  ProductDTO,
  ProductVO,
  Result,
  SaleDTO,
  SaleRecordVO,
} from './types'

/** 分页查询成品 */
export function pageProduct(params: {
  pageNum: number
  pageSize: number
  name?: string
  category?: string
}): Promise<Result<PageResult<ProductVO>>> {
  return http.get<Result<PageResult<ProductVO>>>('/product/page', { params }).then((res) => res.data)
}

/** 新增成品 */
export function addProduct(data: ProductDTO): Promise<Result<void>> {
  return http.post<Result<void>>('/product', data).then((res) => res.data)
}

/** 修改成品 */
export function updateProduct(id: number, data: ProductDTO): Promise<Result<void>> {
  return http.put<Result<void>>(`/product/${id}`, data).then((res) => res.data)
}

/** 删除成品 */
export function deleteProduct(id: number): Promise<Result<void>> {
  return http.delete<Result<void>>(`/product/${id}`).then((res) => res.data)
}

/** 货架批次分页查询 */
export function pageProductBatch(params: {
  pageNum: number
  pageSize: number
  productId?: number
  status?: string
}): Promise<Result<PageResult<ProductBatchVO>>> {
  return http.get<Result<PageResult<ProductBatchVO>>>('/product/batch/page', { params }).then((res) => res.data)
}

/** 成品上架 */
export function onShelf(data: OnShelfDTO): Promise<Result<void>> {
  return http.post<Result<void>>('/product/on-shelf', data).then((res) => res.data)
}

/** 成品下架 */
export function offShelf(data: OffShelfDTO): Promise<Result<void>> {
  return http.post<Result<void>>('/product/off-shelf', data).then((res) => res.data)
}

/** 成品销售 */
export function sale(data: SaleDTO): Promise<Result<void>> {
  return http.post<Result<void>>('/product/sale', data).then((res) => res.data)
}

/** 销售记录查询 */
export function pageSale(params: {
  pageNum: number
  pageSize: number
  saleType?: string
  startDate?: string
  endDate?: string
}): Promise<Result<PageResult<SaleRecordVO>>> {
  return http.get<Result<PageResult<SaleRecordVO>>>('/sale/page', { params }).then((res) => res.data)
}
