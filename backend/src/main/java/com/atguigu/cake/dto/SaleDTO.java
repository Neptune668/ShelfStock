package com.atguigu.cake.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 成品销售请求
 */
@Data
@Schema(description = "成品销售请求")
public class SaleDTO {

    @Schema(description = "批次ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @Schema(description = "销售数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "销售数量不能为空")
    @DecimalMin(value = "0.01", message = "销售数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "销售类型：NORMAL_SALE/DISCOUNT_SALE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "销售类型不能为空")
    private String saleType;

    @Schema(description = "实际成交单价（元），临期降价时必填且需小于原价")
    @DecimalMin(value = "0.01", message = "实际成交单价必须大于 0")
    private BigDecimal actualPrice;
}
