package com.atguigu.cake.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 成品上架请求
 */
@Data
@Schema(description = "成品上架请求")
public class OnShelfDTO {

    @Schema(description = "成品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "成品ID不能为空")
    private Long productId;

    @Schema(description = "上架数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "上架数量不能为空")
    @DecimalMin(value = "0.01", message = "上架数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "生产日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "生产日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date productionDate;

    @Schema(description = "临期阈值（天）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "临期阈值不能为空")
    private Integer nearExpiryDays;
}
