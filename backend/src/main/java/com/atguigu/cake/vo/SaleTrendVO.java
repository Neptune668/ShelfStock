package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 销售趋势（按天统计）
 */
@Data
@Schema(description = "销售趋势")
public class SaleTrendVO {

    @Schema(description = "日期 yyyy-MM-dd")
    private String date;

    @Schema(description = "销售笔数")
    private Long saleCount;

    @Schema(description = "销售金额（元）")
    private BigDecimal saleAmount;
}
