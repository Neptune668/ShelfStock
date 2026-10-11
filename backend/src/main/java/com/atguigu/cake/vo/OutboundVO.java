package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 原材料出库流水
 */
@Data
@Schema(description = "原材料出库流水")
public class OutboundVO {

    @Schema(description = "流水ID")
    private Long id;

    @Schema(description = "原材料名称")
    private String materialName;

    @Schema(description = "批次ID")
    private Long batchId;

    @Schema(description = "出库数量")
    private BigDecimal quantity;

    @Schema(description = "出库原因")
    private String reason;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "出库时间")
    private Date createdAt;
}
