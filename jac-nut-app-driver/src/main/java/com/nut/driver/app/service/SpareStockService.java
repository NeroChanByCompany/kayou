package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.QuerySpareStockForm;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 16:17:56
 */
public interface SpareStockService {
    HttpCommandResultWithData getSpareStockList(QuerySpareStockForm form);
}

