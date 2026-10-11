package com.atguigu.cake.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 成品货架批次
 */
@Data
@Schema(description = "成品货架批次")
public class ProductBatchVO {

    @Schema(description = "批次ID")
    private Long id;

    @Schema(description = "成品ID")
    private Long productId;

    @Schema(description = "成品名称")
    private String productName;

    @Schema(description = "售价（元）")
    private BigDecimal price;

    @Schema(description = "生产日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date productionDate;

    @Schema(description = "过期日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expiryDate;

    @Schema(description = "临期阈值（天）")
    private Integer nearExpiryDays;

    @Schema(description = "剩余数量")
    private BigDecimal quantity;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "剩余保质期天数")
    private Long remainingDays;
}
