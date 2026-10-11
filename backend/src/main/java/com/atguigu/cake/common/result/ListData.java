package com.atguigu.cake.common.result;

import java.util.List;
import lombok.Data;

/**
 * 列表包装结构，序列化为 { "list": [...] }，用于看板等无分页的列表返回
 *
 * @param <T> 列表元素类型
 */
@Data
public class ListData<T> {

    private List<T> list;

    public static <T> ListData<T> of(List<T> list) {
        ListData<T> data = new ListData<>();
        data.setList(list);
        return data;
    }
}
