package com.nut.common.result;

import com.nut.common.base.BaseForm;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description: 分页查询
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.result
 * @Author: yzl
 * @CreateTime: 2021-06-18 15:15
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class PagingInfo<T> {
    /**
     * 查询数据总数
     */
    private long total;

    /**
     * 总页数
     */
    //private long totalPages;

    private long page_total;

    /**
     * 当前页数据集合
     */
    private List<T> list;

    public PagingInfo() {
    }

    /**
     * @param total 查询数据总数
     * @param list  当前页数据集合
     */
    public PagingInfo(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public PagingInfo(long total, List<T> list, BaseForm form) {
        this.total = total;
        this.list = list;
        // 计算page_total
        long pageSize = Long.parseLong(form.getPage_size());
        this.page_total = total % pageSize == 0 ? total / pageSize : (total / pageSize + 1);
    }
}
