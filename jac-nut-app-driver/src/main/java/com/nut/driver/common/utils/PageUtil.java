package com.nut.driver.common.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nut.common.base.BaseForm;
import com.nut.common.result.PagingInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuBing
 * @Classname PageUtil
 * @Description TODO
 * @Date 2021/6/23 17:41
 */
@Component
public class PageUtil {

    public <C extends BaseForm> void getPage(C form) {
        // 分页查询
        if (Integer.valueOf(form.getPage_number()) >= 0 && Integer.valueOf(form.getPage_size()) > 0) {
            // 设置分页参数
            PageHelper.startPage(Integer.valueOf(form.getPage_number()), Integer.valueOf(form.getPage_size()));
        }
    }


    /**
     * 手动分页
     *
     * @param orgList    分页前list，不能为null
     * @param pageNumber 页号，从0开始
     * @param pageSize   每页个数
     * @param pagingInfo 输出分页信息：总页数、总数
     * @return 分页后list
     */
    public <T> List<T> paging(List<T> orgList, String pageNumber, String pageSize, PagingInfo pagingInfo) {
        int total = orgList.size();

        // 进行分页
        List<T> resultList = new ArrayList<>();
        try {
            int iPageNum = Integer.parseInt(pageNumber);
            if(iPageNum > 0 ){
                iPageNum = iPageNum - 1;
            }
            int iPageSize = Integer.parseInt(pageSize);
            for (int i = iPageNum * iPageSize; i < (iPageNum * iPageSize + iPageSize) && i < total; i++) {
                resultList.add(orgList.get(i));

            }
            int pageTotal = (total % iPageSize == 0) ? (total / iPageSize) : (total / iPageSize + 1);
            pagingInfo.setPage_total(pageTotal);
            pagingInfo.setTotal(total);
        } catch (Exception e) {
            resultList = orgList;

            pagingInfo.setPage_total(1);
            pagingInfo.setTotal(total);
        }

        return resultList;
    }

    /**
     * 手动分页
     *
     * @param orgList    分页前list，不能为null
     * @param pageNumber 页号，从0开始
     * @param pageSize   每页个数
     * @return 分页信息对象
     */
    public <T> PagingInfo<T> paging(List<T> orgList, String pageNumber, String pageSize) {
        PagingInfo<T> pagingInfo = new PagingInfo<>();
        List<T> result = paging(orgList, pageNumber, pageSize, pagingInfo);
        pagingInfo.setList(result);
        return pagingInfo;
    }

    public <E> PagingInfo<E> convertPagingToPage(Page<E> page) {
        PagingInfo<E> result = new PagingInfo<E>();
        result.setList(page.getResult());
        result.setPage_total(page.getPages());
        result.setTotal(page.getResult().size());
        return result;
    }
}
