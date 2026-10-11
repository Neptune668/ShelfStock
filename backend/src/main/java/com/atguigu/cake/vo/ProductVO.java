package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 成品信息
 */
@Data
@Schema(description = "成品信息")
public class ProductVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "成品名称")
    private String name;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "售价（元）")
    private BigDecimal price;

    @Schema(description = "保质期天数")
    private Integer shelfLifeDays;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
