package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Data;

/**
 * 原材料信息
 */
@Data
@Schema(description = "原材料信息")
public class MaterialVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "原材料名称")
    private String name;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "保质期天数")
    private Integer shelfLifeDays;

    @Schema(description = "存储条件")
    private String storageCondition;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
