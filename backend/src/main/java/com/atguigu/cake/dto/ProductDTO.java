package com.atguigu.cake.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 成品信息（新增/修改）
 */
@Data
@Schema(description = "成品信息")
public class ProductDTO {

    @Schema(description = "成品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "成品名称不能为空")
    private String name;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "售价（元）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于 0")
    private BigDecimal price;

    @Schema(description = "保质期天数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "保质期天数不能为空")
    private Integer shelfLifeDays;
}
