package com.nut.driver.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nut.common.base.BaseForm;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.MD5Util;
import com.nut.driver.app.domain.MessagePage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 分页服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-18 15:25
 * @Version: 1.0
 */
@Slf4j
public class DriverBaseService {

    protected static <C extends BaseForm> void getPage(C form) {
        int pageNumber = Integer.parseInt(form.getPage_number());
        // 分页查询
        if (pageNumber >= 0 && Integer.valueOf(form.getPage_size()) > 0) {
            // 设置分页参数
            PageHelper.startPage(Integer.valueOf(form.getPage_number()), Integer.valueOf(form.getPage_size()));
        }
    }

    protected <E> PagingInfo<E> convertPagingToPage(Page<E> page) {
        PagingInfo<E> result = new PagingInfo<E>();
        result.setList(page.getResult());
        result.setPage_total(page.getPages());
        result.setTotal(page.getTotal());
        return result;
    }

    protected <E> PagingInfo<E> convertPagingToPage(MessagePage<E> page) {
        PagingInfo<E> result = new PagingInfo<E>();
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
            if (iPageNum > 0) {
                iPageNum = iPageNum - 1;
            }
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

    /**
     * @Description TODO 访问商城需要验证签名使用：拼装get请求参数(包含验签)
     */
    public static Map<String, String> createGetParamSignStr(Object o, String secret) throws Exception {
        Map<String, String> params = MD5Util.createGetParamMap(o);
        createSignByMap(params, secret);
        return params;
    }

    /**
     * 生成验签字符串
     */
    public static void createSignByMap(Map<String, String> params, String secret) throws Exception {
        params.put("appSecret", secret);
        String sign = MD5Util.sign(params);
        params.put("sign", sign);
        params.remove("appSecret");
    }

}
