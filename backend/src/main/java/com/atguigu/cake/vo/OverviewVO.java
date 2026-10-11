package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 看板总览
 */
@Data
@Schema(description = "看板总览")
public class OverviewVO {

    @Schema(description = "原材料总库存")
    private BigDecimal materialTotalQuantity;

    @Schema(description = "原材料种类数")
    private Long materialTypeCount;

    @Schema(description = "今日入库数量")
    private BigDecimal todayInboundQuantity;

    @Schema(description = "今日出库数量")
    private BigDecimal todayOutboundQuantity;

    @Schema(description = "在售成品数量")
    private BigDecimal onSaleQuantity;

    @Schema(description = "货架成品种类数")
    private Long productTypeCount;

    @Schema(description = "今日销售笔数")
    private Long todaySaleCount;

    @Schema(description = "今日销售金额")
    private BigDecimal todaySaleAmount;

    @Schema(description = "临期原材料批次数")
    private Long nearExpiryMaterialBatch;

    @Schema(description = "临期成品批次数")
    private Long nearExpiryProduct;

    @Schema(description = "过期待处理数")
    private Long expiredPending;
}
