package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 临期分布
 */
@Data
@Schema(description = "临期分布")
public class ExpiryDistributionVO {

    @Schema(description = "类型")
    private String type;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "数量")
    private Long count;
}
