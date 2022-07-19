package com.nut.truckingteam.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nut.common.base.BaseForm;
import com.nut.common.result.PagingInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 共通服务基类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.truckingteam.app.service.ipml
 * @Author: yzl
 * @CreateTime: 2021-06-27 14:50
 * @Version: 1.0
 */
@Slf4j
public abstract class TruckTeamBaseService {

    <C extends BaseForm> void getPage(C form) {
        // 分页查询
        if (Integer.valueOf(form.getPage_number()) >= 0 && Integer.valueOf(form.getPage_size()) > 0) {
            // 设置分页参数
            PageHelper.startPage(Integer.valueOf(form.getPage_number()) + 1, Integer.valueOf(form.getPage_size()));
        }
    }

    <E> PagingInfo<E> convertPagingToPage(Page<E> page) {
        PagingInfo<E> result = new PagingInfo<>();
        result.setList(page.getResult());
        result.setPage_total(page.getPages());
        result.setTotal(page.getTotal());
        return result;
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
    <T> List<T> paging(List<T> orgList, String pageNumber, String pageSize, PagingInfo pagingInfo) {
        int total = orgList.size();
        log.info("分页前元素个数:" + total + "(" + pageNumber + "*" + pageSize + ")");
        // 进行分页
        List<T> resultList = new ArrayList<>();
        try {
            int iPageNum = Integer.parseInt(pageNumber);
            int iPageSize = Integer.parseInt(pageSize);
            for (int i = iPageNum * iPageSize; i < (iPageNum * iPageSize + iPageSize) && i < total; i++) {
                resultList.add(orgList.get(i));
                log.debug("选取第" + i + "个元素");
            }
            int pageTotal = (total % iPageSize == 0) ? (total / iPageSize) : (total / iPageSize + 1);
            pagingInfo.setPage_total(pageTotal);
            pagingInfo.setTotal(total);

        } catch (Exception e) {
            resultList = orgList;
            log.info("出现异常，保留所有元素");
            pagingInfo.setPage_total(1);
            pagingInfo.setTotal(total);
        }
        log.info("分页后元素个数:" + resultList.size());
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
    <T> PagingInfo<T> paging(List<T> orgList, String pageNumber, String pageSize) {
        PagingInfo<T> pagingInfo = new PagingInfo<>();
        List<T> result = paging(orgList, pageNumber, pageSize, pagingInfo);
        pagingInfo.setList(result);
        return pagingInfo;
    }
}
