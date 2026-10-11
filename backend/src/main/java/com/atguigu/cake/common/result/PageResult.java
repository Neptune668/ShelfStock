package com.atguigu.cake.common.result;

import java.util.List;
import lombok.Data;

/**
 * 分页返回结构
 *
 * @param <T> 列表元素类型
 */
@Data
public class PageResult<T> {

    private Long total;

    private List<T> list;
}
