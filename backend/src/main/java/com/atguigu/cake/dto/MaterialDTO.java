package com.atguigu.cake.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 原材料信息（新增/修改）
 */
@Data
@Schema(description = "原材料信息")
public class MaterialDTO {

    @Schema(description = "原材料名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "原材料名称不能为空")
    private String name;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "计量单位", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "计量单位不能为空")
    private String unit;

    @Schema(description = "保质期天数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "保质期天数不能为空")
    private Integer shelfLifeDays;

    @Schema(description = "存储条件")
    private String storageCondition;
}
