package com.atguigu.cake.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 成品下架请求
 */
@Data
@Schema(description = "成品下架请求")
public class OffShelfDTO {

    @Schema(description = "批次ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @Schema(description = "下架数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "下架数量不能为空")
    @DecimalMin(value = "0.01", message = "下架数量必须大于 0")
    private BigDecimal quantity;
}
