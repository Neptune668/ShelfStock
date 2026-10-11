package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 出库原因占比
 */
@Data
@Schema(description = "出库原因占比")
public class OutboundReasonVO {

    @Schema(description = "出库原因")
    private String reason;

    @Schema(description = "出库原因名称")
    private String reasonName;

    @Schema(description = "出库数量")
    private BigDecimal quantity;
}
