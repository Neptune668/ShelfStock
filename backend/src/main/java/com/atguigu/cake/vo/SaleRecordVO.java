package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 销售记录
 */
@Data
@Schema(description = "销售记录")
public class SaleRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "成品名称")
    private String productName;

    @Schema(description = "销售数量")
    private BigDecimal quantity;

    @Schema(description = "销售类型")
    private String saleType;

    @Schema(description = "原价（元）")
    private BigDecimal originalPrice;

    @Schema(description = "实际成交单价（元）")
    private BigDecimal actualPrice;

    @Schema(description = "销售时间")
    private Date createdAt;
}
