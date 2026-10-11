import http from './http'
import type {
  ExpiryDistributionVO,
  ListData,
  OutboundReasonVO,
  OverviewVO,
  Result,
  SaleTrendVO,
} from './types'

/** 看板总览 */
export function getOverview(): Promise<Result<OverviewVO>> {
  return http.get<Result<OverviewVO>>('/dashboard/overview').then((res) => res.data)
}

/** 销售趋势（近 N 天） */
export function getSaleTrend(days: number): Promise<Result<ListData<SaleTrendVO>>> {
  return http.get<Result<ListData<SaleTrendVO>>>('/dashboard/sale-trend', { params: { days } }).then((res) => res.data)
}

/** 出库原因占比 */
export function getOutboundReason(): Promise<Result<ListData<OutboundReasonVO>>> {
  return http.get<Result<ListData<OutboundReasonVO>>>('/dashboard/outbound-reason').then((res) => res.data)
}

/** 临期分布 */
export function getExpiryDistribution(): Promise<Result<ListData<ExpiryDistributionVO>>> {
  return http.get<Result<ListData<ExpiryDistributionVO>>>('/dashboard/expiry-distribution').then((res) => res.data)
}
