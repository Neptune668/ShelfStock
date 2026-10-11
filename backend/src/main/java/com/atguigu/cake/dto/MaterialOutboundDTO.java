package com.atguigu.cake.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 原材料出库请求
 */
@Data
@Schema(description = "原材料出库请求")
public class MaterialOutboundDTO {

    @Schema(description = "批次ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @Schema(description = "出库数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "出库数量不能为空")
    @DecimalMin(value = "0.01", message = "出库数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "出库原因：NORMAL_CONSUME/EXPIRED_DISCARD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "出库原因不能为空")
    private String reason;
}
